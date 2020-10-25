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

package top.dcenter.ums.security.core.auth.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.common.consts.SecurityConstants;
import top.dcenter.ums.security.common.enums.LoginProcessType;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.vo.ResponseResult;
import top.dcenter.ums.security.core.vo.UserInfoJsonVo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.responseWithJson;
import static top.dcenter.ums.security.core.util.MvcUtil.getServletContextPath;
import static top.dcenter.ums.security.core.util.MvcUtil.toJsonString;
import static top.dcenter.ums.security.core.util.RequestUtil.getRequestUri;

/**
 * 客户端认证成功处理器, 默认简单实现，需自己去实现.<br><br>
 * 继承 {@link BaseAuthenticationSuccessHandler } 后，再向 IOC 容器注册自己来实现自定义功能。
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Slf4j
public class ClientAuthenticationSuccessHandler extends BaseAuthenticationSuccessHandler {

    protected final ClientProperties clientProperties;
    protected final RequestCache requestCache;
    /**
     * 包含 ServletContextPath
     */
    protected final String auth2RedirectUrl;

    public ClientAuthenticationSuccessHandler(ClientProperties clientProperties, String auth2RedirectUrl) {
        this.auth2RedirectUrl = auth2RedirectUrl;
        this.requestCache = new HttpSessionRequestCache();
        this.clientProperties = clientProperties;
        setTargetUrlParameter(clientProperties.getTargetUrlParameter());
        setUseReferer(clientProperties.getUseReferer());
        ignoreUrls = new HashSet<>();
        ignoreUrls.add(clientProperties.getLoginPage());
        ignoreUrls.add(clientProperties.getLogoutUrl());

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 客户端成功处理器,
        String username = authentication.getName();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader(SecurityConstants.HEADER_USER_AGENT);
        String sid = request.getSession(true).getId();

        log.info("登录成功: user={}, ip={}, ua={}, sid={}",
                 username, ip, userAgent, sid);

        UserInfoJsonVo userInfoJsonVo;
        AbstractAuthenticationToken token = (AbstractAuthenticationToken) authentication;

        try
        {
            userInfoJsonVo = new UserInfoJsonVo(null,
                                                username,
                                                null,
                                                token.getAuthorities());
            // 设置跳转的 url
            String targetUrl = determineTargetUrl(request, response);
            if (!UrlUtils.isAbsoluteUrl(targetUrl))
            {
                targetUrl = getServletContextPath() + targetUrl;
            }

            // 判断是否返回 json 类型
            userInfoJsonVo.setTargetUrl(targetUrl);
            if (LoginProcessType.JSON.equals(clientProperties.getLoginProcessType()))
            {
                clearAuthenticationAttributes(request);
                responseWithJson(response, HttpStatus.OK.value(),
                                 toJsonString(ResponseResult.success(null, userInfoJsonVo)));
                return;
            }

            // 判断 accept 是否要求返回 json
            String acceptHeader = request.getHeader(SecurityConstants.HEADER_ACCEPT);
            if (StringUtils.hasText(acceptHeader) && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE))
            {
                clearAuthenticationAttributes(request);
                responseWithJson(response, HttpStatus.OK.value(),
                                 toJsonString(ResponseResult.success(null, userInfoJsonVo)));
                return;
            }
        }
        catch (Exception e)
        {
            log.error(String.format("设置登录成功后跳转的URL失败: error=%s, user=%s, ip=%s, ua=%s, sid=%s",
                                    e.getMessage(), username, ip, userAgent, sid), e);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }


    /**
     * Builds the target URL according to the logic defined in the main class Javadoc.
     */
    @Override
    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response) {
        String defaultTargetUrl = getDefaultTargetUrl();
        if (isAlwaysUseDefaultTargetUrl()) {
            return defaultTargetUrl;
        }

        // OAuth2 回调时 referer 直接是域名, 没有带 ServletContextPath, 直接返回 defaultTargetUrl
        if (request.getRequestURI().startsWith(auth2RedirectUrl))
        {
            return defaultTargetUrl;
        }

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null)
        {
            return savedRequest.getRedirectUrl();
        }

        // Check for the parameter and use that if available
        String targetUrl = null;

        String targetUrlParameter = getTargetUrlParameter();
        if (targetUrlParameter != null) {
            targetUrl = request.getParameter(targetUrlParameter);

            if (org.springframework.util.StringUtils.hasText(targetUrl)) {
                return targetUrl;
            }
        }

        if (useReferer) {
            String referer = request.getHeader("Referer");
            if (StringUtils.hasText(referer))
            {
                targetUrl = referer;
            }
        }

        // 当 targetUrl 为 登录 url 时, 设置为 defaultTargetUrl
        if (StringUtils.hasText(targetUrl) && isIgnoreUrl(targetUrl))
        {
            targetUrl = defaultTargetUrl;
        }

        if (!StringUtils.hasText(targetUrl)) {
            targetUrl = defaultTargetUrl;
        }

        return targetUrl;
    }

    @Override
    public void setUseReferer(boolean useReferer) {
        super.setUseReferer(useReferer);
        this.useReferer = useReferer;
    }

    /**
     * 判断 ignoreUrls 中是否包含 targetUrl
     * @param targetUrl 不能为 null
     * @return boolean
     */
    private boolean isIgnoreUrl(final String targetUrl) {
        String url = getRequestUri(targetUrl);
        return ignoreUrls.stream().anyMatch(url::startsWith);
    }
}