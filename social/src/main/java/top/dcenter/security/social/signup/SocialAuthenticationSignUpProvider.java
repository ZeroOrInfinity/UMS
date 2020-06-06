package top.dcenter.security.social.signup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.web.ProviderSignInUtils;
import top.dcenter.security.core.enums.ErrorCodeEnum;
import top.dcenter.security.core.exception.RegisterUserFailureException;
import top.dcenter.security.social.api.service.AbstractSocialUserDetailService;

/**
 * social 第三方授权登录注册 Provider
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/7 22:51
 */
@Slf4j
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
        if (authentication.isAuthenticated())
        {
            return authentication;
        }

        UserDetails user;
        try {
            user = userDetailsService.loadUserByUserId((String) authenticationToken.getPrincipal());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RegisterUserFailureException(ErrorCodeEnum.QUERY_USER_INFO_ERROR, e, authentication.getName());
        }

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

        throw new RegisterUserFailureException(ErrorCodeEnum.USERNAME_USED, authentication.getName());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SocialAuthenticationSignUpToken.class.isAssignableFrom(authentication);
    }

}
