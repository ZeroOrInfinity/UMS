package top.dcenter.security.social.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.api.config.SocialWebSecurityConfigurerAware;
import top.dcenter.security.social.properties.SocialProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * social 第三方授权登录注册相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/15 22:22
 */
@Configuration
@ConditionalOnProperty(prefix = "security.social", name = "social-sign-in-is-open", havingValue = "true")
@AutoConfigureAfter({SocialAuthenticationSignUpConfig.class})
@Slf4j
public class SocialAuthenticationSignUpConfigurerAware implements SocialWebSecurityConfigurerAware {

    private final SocialAuthenticationSignUpConfig socialAuthenticationSignUpConfig;
    private final SocialProperties socialProperties;

    public SocialAuthenticationSignUpConfigurerAware(SocialProperties socialProperties, SocialAuthenticationSignUpConfig socialAuthenticationSignUpConfig) {
        this.socialProperties = socialProperties;
        this.socialAuthenticationSignUpConfig = socialAuthenticationSignUpConfig;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // 短信验证码登录配置
        if (this.socialAuthenticationSignUpConfig != null)
        {
            http.apply(this.socialAuthenticationSignUpConfig);
        }
    }

    @Override
    public Map<String, Set<String>> getAuthorizeRequestMap() {
        Set<String> uriSet = new HashSet<>();
        uriSet.add(this.socialProperties.getSignUpUrl());
        uriSet.add(this.socialProperties.getFailureUrl());
        uriSet.add(this.socialProperties.getSignInUrl());

        Map<String, Set<String>> authorizeRequestMap = new HashMap<>(1);
        authorizeRequestMap.put(permitAll, uriSet);
        return authorizeRequestMap;
    }
}


