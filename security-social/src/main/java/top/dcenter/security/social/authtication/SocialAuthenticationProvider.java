package top.dcenter.security.social.authtication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * social 第三方授权登录注册 Provider
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/7 22:51
 */
public class SocialAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public SocialAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        SocialAuthenticationToken authenticationToken = (SocialAuthenticationToken) authentication;
        UserDetails user = userDetailsService.loadUserByUsername((String) authenticationToken.getPrincipal());
        if (user == null)
        {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }
        SocialAuthenticationToken authenticationResult = new SocialAuthenticationToken(user.getUsername(),
                                                                                       user.getPassword(),
                                                                                       user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SocialAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
