package demo.service;

import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.common.api.userdetails.converter.AuthenticationToUserDetailsConverter;


/**
 * {@link JwtAuthenticationToken} to {@link User}
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.2.25 15:07
 */
@Component
public class DemoOauth2TokenAuthenticationTokenToUserConverter implements AuthenticationToUserDetailsConverter {

    @NonNull
    @Override
    public UserDetails convert(@NonNull AbstractOAuth2TokenAuthenticationToken<OAuth2AccessToken> token) {
        User user = new User(token.getName(), "", token.getAuthorities());
        user.eraseCredentials();
        return user;
    }
}