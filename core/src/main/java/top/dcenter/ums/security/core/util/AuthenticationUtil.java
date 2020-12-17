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
import org.springframework.lang.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.common.consts.SecurityConstants;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.common.enums.LoginProcessType;
import top.dcenter.ums.security.common.utils.UrlUtil;
import top.dcenter.ums.security.common.vo.ResponseResult;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.exception.AbstractResponseJsonAuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static top.dcenter.ums.security.common.consts.SecurityConstants.SERVLET_CONTEXT_PERMIT_ALL_SET_KEY;
import static top.dcenter.ums.security.common.consts.SecurityConstants.SESSION_REDIRECT_URL_KEY;
import static top.dcenter.ums.security.common.utils.JsonUtil.isAjaxOrJson;
import static top.dcenter.ums.security.common.utils.JsonUtil.responseWithJson;
import static top.dcenter.ums.security.common.utils.JsonUtil.toJsonString;

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
     * @param rememberMe                                rememberMe 属性
     */
    public static void registerHandlerAndRememberMeServices(AbstractAuthenticationProcessingFilter abstractAuthenticationProcessingFilter,
                                                            BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler,
                                                            BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                                            PersistentTokenRepository persistentTokenRepository,
                                                            UmsUserDetailsService userDetailsService,
                                                            RememberMeServices rememberMeServices,
                                                            ClientProperties.RememberMeProperties rememberMe) {

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

        if (rememberMeServices != null) {
            abstractAuthenticationProcessingFilter.setRememberMeServices(rememberMeServices);
        }
        else {
            // 添加 PersistentTokenBasedRememberMeServices, 不支持多租户
            if (persistentTokenRepository != null) {
                PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices =
                        new PersistentTokenBasedRememberMeServices(UUID.randomUUID().toString(), userDetailsService,
                                                                   persistentTokenRepository);

                persistentTokenBasedRememberMeServices.setTokenValiditySeconds(Integer.parseInt(String.valueOf(rememberMe.getRememberMeTimeout().getSeconds())));
                persistentTokenBasedRememberMeServices.setParameter(rememberMe.getRememberMeCookieName());
                // 添加rememberMe功能配置
                abstractAuthenticationProcessingFilter.setRememberMeServices(persistentTokenBasedRememberMeServices);
            }
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
        String requestUri = UrlUtil.getUrlPathHelper().getPathWithinApplication(request);
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
        // permitAllSet 通过 SecurityCoreAutoConfigurer.groupingAuthorizeRequestUris(..) 注入 ServletContext,

        // noinspection unchecked
        Set<UriHttpMethodTuple> permitAllSet =
                (Set<UriHttpMethodTuple>) session.getServletContext().getAttribute(SERVLET_CONTEXT_PERMIT_ALL_SET_KEY);
        permitAllSet = ofNullable(permitAllSet).orElse(new HashSet<>(0));

        for (UriHttpMethodTuple tuple : permitAllSet)
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
     * @param loginProcessType  {@link LoginProcessType}
     * @return 如果通过 {@link HttpServletResponse} 返回 JSON 数据则返回 true, 否则 false
     * @throws IOException IOException
     */
    public static boolean authenticationFailureProcessing(HttpServletResponse response, HttpServletRequest request,
                                                          AuthenticationException exception,
                                                          AbstractResponseJsonAuthenticationException e,
                                                          LoginProcessType loginProcessType) throws IOException {

        boolean isJsonProcessType = LoginProcessType.JSON.equals(loginProcessType);
        // 判断是否返回 json 类型
        if (isJsonProcessType || isAjaxOrJson(request))
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
     * @param logoutSuccessUrl  logoutSuccessUrl
     * @param loginProcessType  {@link LoginProcessType}
     * @param redirectStrategy  redirectStrategy
     * @param errorCodeEnum errorCodeEnum
     * @throws IOException IOException
     */
    public static void redirectProcessingLogoutByLoginProcessType(HttpServletRequest request,
                                                                  HttpServletResponse response,
                                                                  String logoutSuccessUrl,
                                                                  LoginProcessType loginProcessType,
                                                                  RedirectStrategy redirectStrategy,
                                                                  ErrorCodeEnum errorCodeEnum) throws IOException {

        redirectProcessing(request, response, loginProcessType, redirectStrategy,
                           errorCodeEnum, logoutSuccessUrl);
    }

    /**
     * 根据 LoginProcessType 进行转发处理
     * @param request   request
     * @param response  response
     * @param loginProcessType  {@link LoginProcessType}
     * @param redirectStrategy  redirectStrategy
     * @param errorCodeEnum errorCodeEnum
     * @param redirectUrl   redirectUrl
     * @throws IOException IOException
     */
    public static void redirectProcessingByLoginProcessType(HttpServletRequest request, HttpServletResponse response,
                                                            LoginProcessType loginProcessType,
                                                            RedirectStrategy redirectStrategy, ErrorCodeEnum errorCodeEnum,
                                                            String redirectUrl) throws IOException {

        String referer = ofNullable(request.getHeader(SecurityConstants.HEADER_REFERER)).orElse(redirectUrl);

        redirectProcessing(request, response, loginProcessType, redirectStrategy, errorCodeEnum, referer);
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

        String queryString = request.getQueryString();
        if (StringUtils.hasText(queryString)) {
            queryString = "?" + queryString;
        }

        // 是否为 permitAll url
        if (isPermitUri(request, session, matcher))
        {
            // 设置跳转目标 url 为自己, 重新刷新 session
            redirectUrl = request.getRequestURL().toString() + ofNullable(queryString).orElse("");
        }
        else
        {
            // 获取原始请求的 url
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            originalUrl = request.getRequestURL().toString() + ofNullable(queryString).orElse("");
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
     * 从请求中获取原始请求 url, 如: 引发登录的原始请求, 登录成功后获取此 url 跳转回原始 url
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

    private static void redirectProcessing(HttpServletRequest request, HttpServletResponse response, LoginProcessType loginProcessType, RedirectStrategy redirectStrategy, ErrorCodeEnum errorCodeEnum, String redirectUrl) throws IOException {
        if (LoginProcessType.JSON.equals(loginProcessType))
        {
            int status = HttpStatus.UNAUTHORIZED.value();
            responseWithJson(response, status, toJsonString(ResponseResult.fail(errorCodeEnum, redirectUrl)));
            return;
        }

        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}