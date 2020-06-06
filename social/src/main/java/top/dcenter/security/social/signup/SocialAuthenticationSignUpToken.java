package top.dcenter.security.social.signup;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Collection;

/**
 * social注册登录 Token
 * @author zyw
 * @version V1.0  Created by 2020/5/7 15:25
 */
public class SocialAuthenticationSignUpToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    // ~ Instance fields
    // ================================================================================================

    private final Object principal;
    private Object credentials;

    private ServletWebRequest request;


    // ~ Constructors
    // ===================================================================================================
    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>UsernamePasswordAuthenticationToken</codes>, as the {@link #isAuthenticated()}
     * will return <codes>false</codes>.
     *
     */
    public SocialAuthenticationSignUpToken(Object principal, Object credentials) {
        this(principal, credentials, (ServletWebRequest) null);
    }

    /**
     * This constructor can be safely used by any codes that wishes to create a
     * <codes>UsernamePasswordAuthenticationToken</codes>, as the {@link #isAuthenticated()}
     * will return <codes>false</codes>.
     *
     */
    public SocialAuthenticationSignUpToken(Object principal, Object credentials, ServletWebRequest request) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
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
     * @param authorities   authorities
     */
    public SocialAuthenticationSignUpToken(Object principal, Object credentials,
                                           Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.request = null;
        // must use super, as we override
        super.setAuthenticated(true);
    }


    // ~ Methods
    // ========================================================================================================

    @Override
    public Object getCredentials() {
        return this.credentials;
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
