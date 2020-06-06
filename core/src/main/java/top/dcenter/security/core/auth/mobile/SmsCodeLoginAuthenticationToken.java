package top.dcenter.security.core.auth.mobile;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Collection;

/**
 * 短信登录 Token
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/7 15:25
 */
public class SmsCodeLoginAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    // ~ Instance fields
    // ================================================================================================

    private final Object principal;

    private ServletWebRequest request;


    // ~ Constructors
    // ===================================================================================================
    /**
     * This constructor can be safely used by any codes that wishes to create a
     * <codes>UsernamePasswordAuthenticationToken</codes>, as the {@link #isAuthenticated()}
     * will return <codes>false</codes>.
     *
     */
    public SmsCodeLoginAuthenticationToken(String mobile) {
        this(mobile, (ServletWebRequest) null);
    }

    public SmsCodeLoginAuthenticationToken(String mobile, ServletWebRequest request) {
        super(null);
        this.principal = mobile;
        this.request = request;
        setAuthenticated(false);
    }

    /**
     * This constructor should only be used by <codes>AuthenticationManager</codes> or
     * <codes>AuthenticationProvider</codes> implementations that are satisfied with
     * producing a trusted (i.e. {@link #isAuthenticated()} = <codes>true</codes>)
     * auth token.
     *
     * @param principal principal
     * @param authorities authorities
     */
    public SmsCodeLoginAuthenticationToken(Object principal,
                                           Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.request = null;
        // must use super, as we override
        super.setAuthenticated(true);
    }


    // ~ Methods
    // ========================================================================================================

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }

    public ServletWebRequest getRequest() {
        return request;
    }

}
