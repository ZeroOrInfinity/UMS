package top.dcenter.security.core.authentication.mobile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.dcenter.security.core.enums.ErrorCodeEnum;
import top.dcenter.security.core.exception.ParameterErrorException;
import top.dcenter.security.core.properties.ValidateCodeProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE;
import static top.dcenter.security.core.consts.SecurityConstants.POST_METHOD;

/**
 * 短信登录配置过滤器
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/7 15:34
 */
@Slf4j
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    // ~ Static fields/initializers
    // =====================================================================================

    private String mobileParameter;
    private boolean postOnly = true;
    private final ValidateCodeProperties validateCodeProperties;

    // ~ Constructors
    // ===================================================================================================

    public SmsCodeAuthenticationFilter(ValidateCodeProperties validateCodeProperties) {
        super(new AntPathRequestMatcher(DEFAULT_LOGIN_PROCESSING_URL_MOBILE, POST_METHOD));
        this.validateCodeProperties = validateCodeProperties;
        this.mobileParameter = validateCodeProperties.getSms().getRequestParamMobileName();
    }

    // ~ Methods
    // ========================================================================================================

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals(POST_METHOD)) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String mobile = obtainMobile(request);

        if (StringUtils.isEmpty(mobile)) {
            throw new ParameterErrorException(ErrorCodeEnum.MOBILE_NOT_EMPTY,
                                              this.validateCodeProperties.getSms().getRequestParamMobileName());
        }

        mobile = mobile.trim();

        SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(mobile);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }


    /**
     * 获取手机号方法
     *
     * @param request so that request attributes can be retrieved
     *
     * @return the username that will be presented in the <code>Authentication</code>
     * request token to the <code>AuthenticationManager</code>
     */
    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(mobileParameter);
    }

    /**
     * Provided so that subclasses may configure what is put into the authentication
     * request's details property.
     *
     * @param request that an authentication request is being created for
     * @param authRequest the authentication request object that should have its details
     * set
     */
    protected void setDetails(HttpServletRequest request,
                              SmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    /**
     * Sets the parameter name which will be used to obtain the username from the login
     * request.
     *
     * @param mobileParameter the parameter name. Defaults to "username".
     */
    public void setMobileParameter(String mobileParameter) {
        Assert.hasText(mobileParameter, "Username parameter must not be empty or null");
        this.mobileParameter = mobileParameter;
    }

    /**
     * Defines whether only HTTP POST requests will be allowed by this filter. If set to
     * true, and an authentication request is received which is not a POST request, an
     * exception will be raised immediately and authentication will not be attempted. The
     * <tt>unsuccessfulAuthentication()</tt> method will be called as if handling a failed
     * authentication.
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

