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

package top.dcenter.ums.security.core.auth.mobile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.common.consts.SecurityConstants;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.auth.properties.SmsCodeLoginAuthenticationProperties;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.exception.LoginFailureException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 短信登录配置过滤器
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/7 15:34
 */
@SuppressWarnings("unused")
@Slf4j
public class SmsCodeLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    // ~ Static fields/initializers
    // =====================================================================================

    private String mobileParameter;
    private boolean postOnly = true;
    private final ValidateCodeProperties validateCodeProperties;

    // ~ Constructors
    // ===================================================================================================

    public SmsCodeLoginAuthenticationFilter(@NonNull ValidateCodeProperties validateCodeProperties,
                                            @NonNull SmsCodeLoginAuthenticationProperties smsCodeLoginAuthenticationProperties,
                                            @Nullable AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        super(new AntPathRequestMatcher(smsCodeLoginAuthenticationProperties.getLoginProcessingUrlMobile(), SecurityConstants.POST_METHOD));
        this.validateCodeProperties = validateCodeProperties;
        this.mobileParameter = validateCodeProperties.getSms().getRequestParamMobileName();
        if (authenticationDetailsSource != null) {
            setAuthenticationDetailsSource(authenticationDetailsSource);
        }
    }

    // ~ Methods
    // ========================================================================================================

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals(SecurityConstants.POST_METHOD)) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String mobile = obtainMobile(request);

        if (StringUtils.isEmpty(mobile)) {
            throw new LoginFailureException(ErrorCodeEnum.MOBILE_NOT_EMPTY,
                                            this.validateCodeProperties.getSms().getRequestParamMobileName(),
                                            request.getSession(true).getId());
        }

        mobile = mobile.trim();

        SmsCodeLoginAuthenticationToken authRequest = new SmsCodeLoginAuthenticationToken(mobile);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }


    /**
     * 获取手机号方法
     *
     * @param request so that request attributes can be retrieved
     *
     * @return the username that will be presented in the <codes>Authentication</codes>
     * request token to the <codes>AuthenticationManager</codes>
     */
    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(mobileParameter);
    }

    /**
     * Provided so that subclasses may configure what is put into the auth
     * request's details property.
     *
     * @param request that an auth request is being created for
     * @param authRequest the auth request object that should have its details
     * set
     */
    protected void setDetails(HttpServletRequest request,
                              SmsCodeLoginAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    /**
     * Sets the parameter name which will be used to obtain the username from the login
     * request.
     *
     * @param mobileParameter the parameter name. Defaults to "mobile".
     */
    public void setMobileParameter(String mobileParameter) {
        Assert.hasText(mobileParameter, "mobile parameter must not be empty or null");
        this.mobileParameter = mobileParameter;
    }

    /**
     * Defines whether only HTTP POST requests will be allowed by this filter. If set to
     * true, and an auth request is received which is not a POST request, an
     * exception will be raised immediately and auth will not be attempted. The
     * <tt>unsuccessfulAuthentication()</tt> method will be called as if handling a failed
     * auth.
     * <p>
     * Defaults to <tt>true</tt> but may be overridden by subclasses.
     */
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getMobileParameter() {
        return mobileParameter;
    }


}