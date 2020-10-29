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

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Collection;

/**
 * 短信登录 Token
 * @author zhailiang
 * @author  YongWu zheng
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
     * <codes>SmsCodeLoginAuthenticationToken</codes>, as the {@link #isAuthenticated()}
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