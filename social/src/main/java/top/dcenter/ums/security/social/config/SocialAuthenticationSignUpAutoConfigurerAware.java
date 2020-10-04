package top.dcenter.ums.security.social.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.ums.security.core.api.config.HttpSecurityAware;
import top.dcenter.ums.security.core.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.social.properties.SocialProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static top.dcenter.ums.security.core.bean.UriHttpMethodTuple.tuple;

/**
 * social 第三方授权登录注册相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/15 22:22
 */
@Configuration
@ConditionalOnProperty(prefix = "ums.social", name = "social-sign-up-is-open", havingValue = "true")
@AutoConfigureAfter({SocialAuthenticationSignUpAutoConfig.class})
@Slf4j
public class SocialAuthenticationSignUpAutoConfigurerAware implements HttpSecurityAware {

    private final SocialAuthenticationSignUpAutoConfig socialAuthenticationSignUpAutoConfig;
    private final SocialProperties socialProperties;

    public SocialAuthenticationSignUpAutoConfigurerAware(SocialProperties socialProperties, SocialAuthenticationSignUpAutoConfig socialAuthenticationSignUpAutoConfig) {
        this.socialProperties = socialProperties;
        this.socialAuthenticationSignUpAutoConfig = socialAuthenticationSignUpAutoConfig;
    }

    @Override
    public void postConfigure(HttpSecurity http) {
        // dto nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // 短信验证码登录配置
        if (this.socialAuthenticationSignUpAutoConfig != null)
        {
            http.apply(this.socialAuthenticationSignUpAutoConfig);
        }
    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        final Map<UriHttpMethodTuple, Set<String>> permitAllMap = new HashMap<>(16);

        permitAllMap.put(tuple(GET, this.socialProperties.getSignUpUrl()), null);
        permitAllMap.put(tuple(GET, this.socialProperties.getFailureUrl()), null);
        permitAllMap.put(tuple(GET, this.socialProperties.getSignInUrl()), null);

        Map<String, Map<UriHttpMethodTuple, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.PERMIT_ALL, permitAllMap);

        return resultMap;

    }
}


