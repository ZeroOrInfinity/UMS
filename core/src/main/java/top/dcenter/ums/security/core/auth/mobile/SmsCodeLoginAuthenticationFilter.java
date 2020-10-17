package top.dcenter.ums.security.core.auth.mobile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.common.consts.SecurityConstants;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.exception.LoginFailureException;
import top.dcenter.ums.security.core.auth.properties.SmsCodeLoginAuthenticationProperties;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 短信登录配置过滤器
 * @author zhailiang
 * @author  zyw
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

    public SmsCodeLoginAuthenticationFilter(ValidateCodeProperties validateCodeProperties, SmsCodeLoginAuthenticationProperties smsCodeLoginAuthenticationProperties) {
        super(new AntPathRequestMatcher(smsCodeLoginAuthenticationProperties.getLoginProcessingUrlMobile(), SecurityConstants.POST_METHOD));
        this.validateCodeProperties = validateCodeProperties;
        this.mobileParameter = validateCodeProperties.getSms().getRequestParamMobileName();
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
     * @param mobileParameter the parameter name. Defaults to "username".
     */
    public void setMobileParameter(String mobileParameter) {
        Assert.hasText(mobileParameter, "Username parameter must not be empty or null");
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

