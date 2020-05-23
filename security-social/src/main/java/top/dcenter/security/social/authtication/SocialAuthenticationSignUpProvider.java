package top.dcenter.security.social.authtication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.web.ProviderSignInUtils;
import top.dcenter.security.core.excception.RegisterUserFailureException;
import top.dcenter.security.social.api.service.AbstractSocialUserDetailService;

/**
 * social 第三方授权登录注册 Provider
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/7 22:51
 */
public class SocialAuthenticationSignUpProvider implements AuthenticationProvider {

    private final ProviderSignInUtils providerSignInUtils;
    private final AbstractSocialUserDetailService userDetailsService;

    public SocialAuthenticationSignUpProvider(AbstractSocialUserDetailService userDetailsService, ProviderSignInUtils providerSignInUtils) {
        this.userDetailsService = userDetailsService;
        this.providerSignInUtils = providerSignInUtils;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        SocialAuthenticationSignUpToken authenticationToken = (SocialAuthenticationSignUpToken) authentication;
        UserDetails user = userDetailsService.loadUserByUserId((String) authenticationToken.getPrincipal());
        if (user == null)
        {
            user = userDetailsService.registerUser(authenticationToken.getRequest(), providerSignInUtils);
            SocialAuthenticationSignUpToken authenticationResult =
                    new SocialAuthenticationSignUpToken(user.getUsername(),
                                                                       user.getPassword(),
                                                                       user.getAuthorities());
            authenticationResult.setDetails(authenticationToken.getDetails());
            return authenticationResult;
        }
        throw new RegisterUserFailureException("Username used");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SocialAuthenticationSignUpToken.class.isAssignableFrom(authentication);
    }

}
