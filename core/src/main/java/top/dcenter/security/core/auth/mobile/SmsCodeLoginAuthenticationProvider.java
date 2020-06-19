package top.dcenter.security.core.auth.mobile;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import top.dcenter.security.core.api.service.AbstractUserDetailsService;

/**
 * 短信登录 Provider
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/7 22:51
 */
public class SmsCodeLoginAuthenticationProvider implements AuthenticationProvider {

    private AbstractUserDetailsService userDetailsService;

    public SmsCodeLoginAuthenticationProvider(AbstractUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        SmsCodeLoginAuthenticationToken authenticationToken = (SmsCodeLoginAuthenticationToken) authentication;

        if (authentication.isAuthenticated())
        {
            return authentication;
        }
        UserDetails user = this.userDetailsService.loadUserByUsername((String) authenticationToken.getPrincipal());
        if (user == null)
        {
            user = this.userDetailsService.registerUser((String) authenticationToken.getPrincipal());

        }
        SmsCodeLoginAuthenticationToken authenticationResult = new SmsCodeLoginAuthenticationToken(user, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
