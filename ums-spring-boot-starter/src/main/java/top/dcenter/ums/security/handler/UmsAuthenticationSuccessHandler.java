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

package top.dcenter.ums.security.handler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.log.LogMessage;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.UrlUtils;
import top.dcenter.ums.security.common.consts.SecurityConstants;
import top.dcenter.ums.security.common.enums.LoginProcessType;
import top.dcenter.ums.security.common.utils.IpUtil;
import top.dcenter.ums.security.common.utils.UuidUtils;
import top.dcenter.ums.security.common.vo.ResponseResult;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.vo.AuthTokenVo;
import top.dcenter.ums.security.jwt.JwtContext;
import top.dcenter.ums.security.properties.UmsProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.hasText;
import static top.dcenter.ums.security.common.utils.JsonUtil.isAjaxOrJson;
import static top.dcenter.ums.security.common.utils.JsonUtil.responseWithJson;
import static top.dcenter.ums.security.common.utils.JsonUtil.toJsonString;
import static top.dcenter.ums.security.core.util.MvcUtil.isSelfTopDomain;
import static top.dcenter.ums.security.core.util.RequestUtil.getRequestUri;
import static top.dcenter.ums.security.jwt.JwtContext.TEMPORARY_JWT_REFRESH_TOKEN;

/**
 * 客户端认证成功处理器, 默认简单实现，需自己去实现.<br><br>
 * 继承 {@link BaseAuthenticationSuccessHandler } 后，再向 IOC 容器注册自己来实现自定义功能。
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Slf4j
public class UmsAuthenticationSuccessHandler extends BaseAuthenticationSuccessHandler {

    protected final RequestCache requestCache;

    protected final LoginProcessType loginProcessType;

    private final RedisConnectionFactory redisConnectionFactory;

    private final UmsProperties umsProperties;

    private final String domain;
    private final String refreshTokenHeaderName;

    public UmsAuthenticationSuccessHandler(@NonNull ClientProperties clientProperties,
                                           @Nullable String auth2RedirectUrl,
                                           @NonNull String domain,
                                           @NonNull String refreshTokenHeaderName,
                                           @NonNull RedisConnectionFactory redisConnectionFactory,
                                           @NonNull UmsProperties umsProperties) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.umsProperties = umsProperties;
        this.auth2RedirectUrl = auth2RedirectUrl;
        this.domain = domain;
        this.refreshTokenHeaderName = refreshTokenHeaderName;
        this.requestCache = new HttpSessionRequestCache();
        this.loginProcessType = clientProperties.getLoginProcessType();

        setTargetUrlParameter(clientProperties.getTargetUrlParameter());
        setUseReferer(clientProperties.getUseReferer());
        setAlwaysUseDefaultTargetUrl(clientProperties.getAlwaysUseDefaultTargetUrl());

        ignoreUrls.add(clientProperties.getLoginPage());
        ignoreUrls.add(clientProperties.getLogoutUrl());

        super.setDefaultTargetUrl(clientProperties.getSuccessUrl());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 客户端成功处理器,
        String username = authentication.getName();
        String ip = IpUtil.getRealIp(request);
        String userAgent = request.getHeader(SecurityConstants.HEADER_USER_AGENT);
        HttpSession session = request.getSession(true);
        String sid = session.getId();

        log.info("登录成功: uid={}, ip={}, ua={}, sid={}",
                 username, ip, userAgent, sid);

        try
        {

            // 设置跳转的 url
            String targetUrl = determineTargetUrl(request, response);

            // 第三方登录成功请求, 返回 token 需特殊处理
            if (nonNull(auth2RedirectUrl)
                    && request.getServletPath().startsWith(auth2RedirectUrl)
                    && authentication instanceof AbstractOAuth2TokenAuthenticationToken) {
                //noinspection unchecked
                AbstractOAuth2TokenAuthenticationToken<Jwt> jwtAuthentication =
                        (AbstractOAuth2TokenAuthenticationToken<Jwt>) authentication;
                oauth2CallbackUrl(request, response, targetUrl, jwtAuthentication);
                return ;
            }

            // 判断是否返回 json 类型 或 accept 是否要求返回 json
            if (LoginProcessType.JSON.equals(this.loginProcessType) || isAjaxOrJson(request))
            {
                clearAuthenticationAttributes(request);
                AuthTokenVo authTokenVo = new AuthTokenVo(username,
                                                          username,
                                                          null,
                                                          null,
                                                          null,
                                                          null,
                                                          getJsonTargetUrl(targetUrl, request),
                                                          null);
                // 设置 jwt
                String jwtStringIfAllowBodyParameter = JwtContext.getJwtStringIfAllowBodyParameter(authentication);
                if (hasText(jwtStringIfAllowBodyParameter)) {
                    authTokenVo.setToken(jwtStringIfAllowBodyParameter);
                    authTokenVo.setExpiresIn(ofNullable(JwtContext.getJwtExpiresInByAuthentication(authentication)).orElse(-1L));
                }
                // 设置 jwt refresh token
                if (JwtContext.isRefreshJwtByRefreshToken()) {
                    authTokenVo.setRefreshToken(JwtContext.getJwtRefreshTokenFromSession());
                }

                clearAuthenticationAttributes(request);
                responseWithJson(response, HttpStatus.OK.value(),
                                 toJsonString(ResponseResult.success(null, authTokenVo)));
                return;
            }

            clearAuthenticationAttributes(request);
            session.removeAttribute(TEMPORARY_JWT_REFRESH_TOKEN);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
        catch (Exception e)
        {
            log.error(String.format("设置登录成功后跳转的URL失败: error=%s, uid=%s, ip=%s, ua=%s, sid=%s",
                                    e.getMessage(), username, ip, userAgent, sid), e);
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }

    /**
     * Builds the target URL according to the logic defined in the main class Javadoc.
     */
    @SneakyThrows
    @Override
    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response) {
        String defaultTargetUrl = getDefaultTargetUrl();
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);

        //noinspection AlibabaAvoidComplexCondition
        if (isAlwaysUseDefaultTargetUrl()) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(LogMessage.format("Using default url %s", defaultTargetUrl));
            }
            this.requestCache.removeRequest(request, response);
            return defaultTargetUrl;
        }

        if (savedRequest != null)
        {
            final String redirectUrl = savedRequest.getRedirectUrl();
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(LogMessage.format("using url %s from default saved request %s", redirectUrl));
            }
            return redirectUrl;
        }

        // Check for the parameter and use that if available
        String targetUrl = null;

        String targetUrlParameter = getTargetUrlParameter();
        if (targetUrlParameter != null) {
            targetUrl = request.getParameter(targetUrlParameter);

            if (hasText(targetUrl) && isSelfTopDomain(targetUrl)) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace(LogMessage.format("Using url %s from request parameter %s", targetUrl,
                                                        targetUrlParameter));
                }
                return targetUrl;
            }
            targetUrl = null;
        }

        if (useReferer) {
            String referer = request.getHeader("Referer");
            if (hasText(referer) && isSelfTopDomain(referer))
            {
                targetUrl = referer;
            }
        }

        // 当 targetUrl 为 登录 url 时, 设置为 defaultTargetUrl
        if (!hasText(targetUrl) || isIgnoreUrl(targetUrl, request))
        {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(LogMessage.format("Using default url %s", defaultTargetUrl));
            }
            return defaultTargetUrl;
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(LogMessage.format("Using url %s from Referer header", targetUrl));
        }
        return targetUrl;
    }

    @Override
    public void setUseReferer(boolean useReferer) {
        super.setUseReferer(useReferer);
        this.useReferer = useReferer;
    }

    private void oauth2CallbackUrl(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                   @NonNull String targetUrl,
                                   @NonNull AbstractOAuth2TokenAuthenticationToken<Jwt> jwtAuthentication)
            throws IOException {
        String uuid = UuidUtils.getUUID();
        request.getSession().setAttribute(umsProperties.getOauth2TokenParamName(), uuid);
        String tkValue = jwtAuthentication.getToken().getTokenValue();
        String delimiterOfTokenAndRefreshToken = umsProperties.getDelimiterOfTokenAndRefreshToken();

        String jwtRefreshTokenFromSession = JwtContext.getJwtRefreshTokenFromSession();
        if (hasText(jwtRefreshTokenFromSession)) {
            tkValue = tkValue.concat(delimiterOfTokenAndRefreshToken)
                             .concat(jwtRefreshTokenFromSession);
        }
        else {
            String refreshTokenFromHeader = response.getHeader(refreshTokenHeaderName);
            if (hasText(refreshTokenFromHeader)) {
                tkValue = tkValue.concat(delimiterOfTokenAndRefreshToken)
                                 .concat(refreshTokenFromHeader);
            }
        }
        //noinspection AlibabaUndefineMagicConstant
        if ((domain + "/").equals(targetUrl) || domain.equals(targetUrl)) {
            targetUrl = domain + request.getContextPath();
        }
        tkValue = tkValue.concat(delimiterOfTokenAndRefreshToken)
                         .concat(targetUrl);
        try (RedisConnection connection = getConnection()) {
            connection.setEx((umsProperties.getTempOauth2TokenPrefix() + uuid).getBytes(StandardCharsets.UTF_8),
                             umsProperties.getTempOauth2TokenTimeout().getSeconds(),
                             tkValue.getBytes(StandardCharsets.UTF_8));
        }

        clearAuthenticationAttributes(request);
        //noinspection StringBufferReplaceableByString
        StringBuilder url = new StringBuilder(request.getContextPath() + umsProperties.getAutoGetTokenUri());
        url.append("?")
           .append(umsProperties.getOauth2TokenParamName())
           .append("=")
           .append(uuid)
           .append("&username=")
           .append(URLEncoder.encode(jwtAuthentication.getName(), StandardCharsets.UTF_8.name()))
           .append("&id=")
           .append(URLEncoder.encode(jwtAuthentication.getName(), StandardCharsets.UTF_8.name()));
        response.sendRedirect(url.toString());
    }

    /**
     * 获取用于 json 的跳转地址
     */
    private String getJsonTargetUrl(String targetUrl, HttpServletRequest request) {
        if (!UrlUtils.isAbsoluteUrl(targetUrl))
        {
            String contextPath = request.getContextPath();
            if (!targetUrl.startsWith(contextPath)) {
                targetUrl = contextPath + targetUrl;
            }
        }
        return targetUrl;
    }

    /**
     * 判断 ignoreUrls 中是否包含 targetUrl
     * @param targetUrl 不能为 null
     * @return boolean
     */
    private boolean isIgnoreUrl(final String targetUrl, HttpServletRequest request) {
        String url = getRequestUri(targetUrl, request);
        return ignoreUrls.stream().anyMatch(url::startsWith);
    }

    @NonNull
    private RedisConnection getConnection() {
        return this.redisConnectionFactory.getConnection();
    }

}