/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.common.consts.SecurityConstants;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.common.enums.LoginProcessType;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.exception.AbstractResponseJsonAuthenticationException;
import top.dcenter.ums.security.core.vo.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static top.dcenter.ums.security.common.consts.SecurityConstants.SERVLET_CONTEXT_AUTHORIZE_REQUESTS_MAP_KEY;
import static top.dcenter.ums.security.common.consts.SecurityConstants.SESSION_REDIRECT_URL_KEY;
import static top.dcenter.ums.security.core.util.MvcUtil.toJsonString;

/**
 * Authentication util
 *
 * @author YongWu zheng
 * @version V1.0  Created by 2020/6/1 22:39
 */
public class AuthenticationUtil {

    /**
     * 在提取 User-Agent 时, 需要被移除掉的字符的正则表达式
     */
    @SuppressWarnings("RegExpRedundantEscape")
    public static final String EXTRACT_USER_AGENT_REGEX = "[\\.\\d\\s\\(\\)]";

    /**
     * 给 {@link AbstractAuthenticationProcessingFilter} 注册 {@link BaseAuthenticationFailureHandler},
     * {@link BaseAuthenticationSuccessHandler} 和 {@link PersistentTokenBasedRememberMeServices}.
     * @param abstractAuthenticationProcessingFilter    abstractAuthenticationProcessingFilter
     * @param baseAuthenticationSuccessHandler          认证成功处理器
     * @param baseAuthenticationFailureHandler          认证失败处理器
     * @param persistentTokenRepository                 RememberMe 持久化 Repository
     * @param userDetailsService                        本地用户服务
     * @param clientProperties                          客户端属性
     */
    public static void registerHandlerAndRememberMeServices(AbstractAuthenticationProcessingFilter abstractAuthenticationProcessingFilter,
                                                            BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler,
                                                            BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                                            PersistentTokenRepository persistentTokenRepository,
                                                            UmsUserDetailsService userDetailsService,
                                                            ClientProperties clientProperties) {

        if (baseAuthenticationFailureHandler != null)
        {
            // 添加认证失败处理器
            abstractAuthenticationProcessingFilter.setAuthenticationFailureHandler(baseAuthenticationFailureHandler);
        }
        if (baseAuthenticationSuccessHandler != null)
        {
            // 添加认证成功处理器
            abstractAuthenticationProcessingFilter.setAuthenticationSuccessHandler(baseAuthenticationSuccessHandler);
        }

        // 添加 PersistentTokenBasedRememberMeServices
        if (persistentTokenRepository != null)
        {
            PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices =
                    new PersistentTokenBasedRememberMeServices(UUID.randomUUID().toString(), userDetailsService,
                                                               persistentTokenRepository);
            final ClientProperties.RememberMeProperties rememberMe = clientProperties.getRememberMe();
            persistentTokenBasedRememberMeServices.setTokenValiditySeconds(Integer.parseInt(String.valueOf(rememberMe.getRememberMeTimeout().getSeconds())));
            persistentTokenBasedRememberMeServices.setParameter(rememberMe.getRememberMeCookieName());
            // 添加rememberMe功能配置
            abstractAuthenticationProcessingFilter.setRememberMeServices(persistentTokenBasedRememberMeServices);
        }
    }

    /**
     * 判断 exception 是不是 {@link AbstractResponseJsonAuthenticationException} 的子类, 如果不是, 则返回 null.
     * @param exception not null
     * @return  如果不是 {@link AbstractResponseJsonAuthenticationException} 的子类, 则返回 null.
     */
    public static AbstractResponseJsonAuthenticationException getAbstractResponseJsonAuthenticationException(AuthenticationException exception) {
        AbstractResponseJsonAuthenticationException e = null;
        if (exception instanceof AbstractResponseJsonAuthenticationException)
        {
            e = (AbstractResponseJsonAuthenticationException) exception;
        }
        return e;
    }

    /**
     * 是否是 permitUri 且 HttpMethod 也对应
     * @param request       request
     * @param session       session
     * @param matcher       AntPathMatcher
     * @return  boolean
     */
    public static boolean isPermitUri(@NonNull HttpServletRequest request, @NonNull HttpSession session,
                                      @NonNull AntPathMatcher matcher) {
        String requestUri = MvcUtil.getUrlPathHelper().getPathWithinApplication(request);
        String method = request.getMethod();

        return isPermitUri(requestUri, method, session, matcher);
    }

    /**
     * 是否是 permitUri 且 HttpMethod 也对应
     * @param requestUri    requestUri
     * @param method        httpMethod
     * @param session       session
     * @param matcher       AntPathMatcher
     * @return  boolean
     */
    private static boolean isPermitUri(@NonNull String requestUri, @NonNull String method, @NonNull HttpSession session,
                                      @NonNull AntPathMatcher matcher) {
        // authorizeRequestMap 通过 SecurityCoreAutoConfigurer.groupingAuthorizeRequestUris(..) 注入 ServletContext,

        // noinspection unchecked
        Map<String, Set<UriHttpMethodTuple>> authorizeRequestMap =
                (Map<String, Set<UriHttpMethodTuple>>) session.getServletContext()
                                                              .getAttribute(SERVLET_CONTEXT_AUTHORIZE_REQUESTS_MAP_KEY);
        authorizeRequestMap = ofNullable(authorizeRequestMap).orElse(new HashMap<>(0));
        Set<UriHttpMethodTuple> permitSet =
                ofNullable(authorizeRequestMap.get(HttpSecurityAware.PERMIT_ALL)).orElse(new HashSet<>(0));

        for (UriHttpMethodTuple tuple : permitSet)
        {
            // uri 匹配
            if (matcher.match(tuple.getUri(), requestUri))
            {
                HttpMethod httpMethod = tuple.getMethod();
                // 没有 HttpMethod 类型, 只需要 uri 匹配
                if (httpMethod == null)
                {
                    return true;
                }

                // 有 HttpMethod 类型, 还需要 method 匹配
                String name = httpMethod.name();
                if (name.equalsIgnoreCase(method))
                {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 认证失败处理
     * {@link AbstractResponseJsonAuthenticationException} 类的异常, Response 会返回 Json 数据.
     * @param response          {@link javax.servlet.http.HttpServletRequest}
     * @param exception         {@link HttpServletResponse}
     * @param clientProperties {@link ClientProperties}
     * @return 如果通过 {@link HttpServletResponse} 返回 JSON 数据则返回 true, 否则 false
     * @throws IOException IOException
     */
    public static boolean authenticationFailureProcessing(HttpServletResponse response, AuthenticationException exception,
                                                          AbstractResponseJsonAuthenticationException e, String acceptHeader,
                                                          ClientProperties clientProperties) throws IOException {

        boolean isJsonProcessType = LoginProcessType.JSON.equals(clientProperties.getLoginProcessType());
        boolean isAcceptHeader =
                StringUtils.hasText(acceptHeader) && (acceptHeader.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE) || acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE));
        // 判断是否返回 json 类型
        if (isJsonProcessType || isAcceptHeader)
        {

            int status = HttpStatus.UNAUTHORIZED.value();
            ResponseResult result;
            if (e != null)
            {
                result = ResponseResult.fail(e.getErrorCodeEnum(), e.getData());
            }
            else
            {
                result = ResponseResult.fail(exception.getMessage(), ErrorCodeEnum.UNAUTHORIZED);
            }

            responseWithJson(response, status, toJsonString(result));
            return true;
        }
        return false;
    }

    /**
     * 向客户端响应 json 格式
     * @param response  response
     * @param status    响应的状态码
     * @param result    相应的结果字符串
     * @throws IOException IOException
     */
    public static void responseWithJson(HttpServletResponse response, int status,
                                         String result) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(SecurityConstants.CHARSET_UTF8);
        PrintWriter writer = response.getWriter();
        writer.write(result);
        writer.flush();
    }

    /**
     * 去除 User-Agent 中的(\.|\s|\(|\)|\d), 再返回剩余的字符
     *
     * @param userAgent User-Agent
     * @return 去掉 User-Agent 中的(\.|\s|\(|\)|\d), 再返回剩余的字符
     */
    @SuppressWarnings("unused")
    public static String extractUserAgent(String userAgent) {
        return userAgent.replaceAll(EXTRACT_USER_AGENT_REGEX, "");
    }

    /**
     * 根据 LoginProcessType 进行 logout 转发处理
     * @param request   request
     * @param response  response
     * @param clientProperties  clientProperties
     * @param redirectStrategy  redirectStrategy
     * @param errorCodeEnum errorCodeEnum
     * @throws IOException IOException
     */
    public static void redirectProcessingLogoutByLoginProcessType(HttpServletRequest request,
                                                                  HttpServletResponse response,
                                                                  ClientProperties clientProperties,
                                                                  RedirectStrategy redirectStrategy,
                                                                  ErrorCodeEnum errorCodeEnum) throws IOException {

        redirectProcessing(request, response, clientProperties, redirectStrategy,
                           errorCodeEnum, clientProperties.getLogoutSuccessUrl());
    }

    /**
     * 根据 LoginProcessType 进行转发处理
     * @param request   request
     * @param response  response
     * @param clientProperties  clientProperties
     * @param redirectStrategy  redirectStrategy
     * @param errorCodeEnum errorCodeEnum
     * @param redirectUrl   redirectUrl
     * @throws IOException IOException
     */
    public static void redirectProcessingByLoginProcessType(HttpServletRequest request, HttpServletResponse response,
                                                            ClientProperties clientProperties,
                                                            RedirectStrategy redirectStrategy, ErrorCodeEnum errorCodeEnum,
                                                            String redirectUrl) throws IOException {

        String referer = ofNullable(request.getHeader(SecurityConstants.HEADER_REFERER)).orElse(redirectUrl);

        redirectProcessing(request, response, clientProperties, redirectStrategy, errorCodeEnum, referer);
    }

    /**
     * determine invalid session redirect url
     * @param request           request
     * @param response          response
     * @param destinationUrl    destinationUrl
     * @param matcher           matcher
     * @param requestCache      requestCache
     * @return  determine redirectUrl
     */
    public static String determineInvalidSessionRedirectUrl(HttpServletRequest request, HttpServletResponse response,
                                                            String destinationUrl, AntPathMatcher matcher,
                                                            RequestCache requestCache) {
        HttpSession session = request.getSession();
        String redirectUrl = destinationUrl;

        String originalUrl = null;
        // 是否为 permitAll url
        if (isPermitUri(request, session, matcher))
        {
            // 设置跳转目标 url 为自己, 重新刷新 session
            redirectUrl = request.getRequestURL().toString() + ofNullable(request.getQueryString()).orElse("");
        }
        else
        {
            // 获取原始请求的 url
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            originalUrl = request.getRequestURL().toString() + ofNullable(request.getQueryString()).orElse("");
            if (savedRequest != null)
            {
                originalUrl = ofNullable(savedRequest.getRedirectUrl()).orElse(originalUrl);
            }
        }

        session = request.getSession();
        session.removeAttribute(SecurityConstants.SESSION_ENHANCE_CHECK_KEY);

        if (originalUrl != null)
        {
            // 保存原始请求到 session, 准备成功登录时跳转.
            session.setAttribute(SESSION_REDIRECT_URL_KEY, originalUrl);
        }
        return redirectUrl;
    }

    /**
     * 从请求中获取原始请求 url, 如: 引发登录的原始请求, 登录成功后获取此 url 跳转会原始 url
     * @param requestCache          requestCache
     * @param request               request
     * @param response              response
     * @param defaultRedirectUrl    defaultRedirectUrl
     * @return originalUrl
     * @throws IOException  IOException
     */
    @SuppressWarnings("RedundantThrows")
    public static String getOriginalUrl(RequestCache requestCache, HttpServletRequest request,
                                        HttpServletResponse response,
                                        String defaultRedirectUrl) throws IOException {
        // 设置跳转的 url
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        String redirectUrl = defaultRedirectUrl;
        if (savedRequest != null)
        {
            redirectUrl = ofNullable(savedRequest.getRedirectUrl()).orElse(redirectUrl);
        }

        // 从 session 中查看是否有原始请求连接.
        HttpSession session = request.getSession();
        String originalUrl = (String) session.getAttribute(SESSION_REDIRECT_URL_KEY);
        if (StringUtils.hasText(originalUrl))
        {
            redirectUrl = originalUrl;
            session.removeAttribute(SESSION_REDIRECT_URL_KEY);
        }

        return redirectUrl;
    }

    private static void redirectProcessing(HttpServletRequest request, HttpServletResponse response, ClientProperties clientProperties, RedirectStrategy redirectStrategy, ErrorCodeEnum errorCodeEnum, String redirectUrl) throws IOException {
        if (LoginProcessType.JSON.equals(clientProperties.getLoginProcessType()))
        {
            int status = HttpStatus.UNAUTHORIZED.value();
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(SecurityConstants.CHARSET_UTF8);

            // 在 ResponseResult 中的 data 为请求头中的 referer
            response.getWriter().write(toJsonString(ResponseResult.fail(errorCodeEnum, redirectUrl)));
            return;
        }

        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}