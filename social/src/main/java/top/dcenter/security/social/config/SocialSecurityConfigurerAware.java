package top.dcenter.security.social.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.api.config.WebSecurityConfigurerAware;
import top.dcenter.security.social.api.config.SocialCoreConfig;
import top.dcenter.security.social.properties.SocialProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 把 social 第三方授权登录相关配置添加到 HttpSecurity 中。
 * @see WebSecurityConfigurerAware
 * @author zyw
 * @version V1.0  Created by 2020/5/12 12:02
 */
@Configuration
@AutoConfigureAfter({SocialConfiguration.class})
@Slf4j
public class SocialSecurityConfigurerAware implements WebSecurityConfigurerAware {

    private final SocialProperties socialProperties;

    private final SocialCoreConfig socialCoreConfig;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SocialSecurityConfigurerAware(SocialProperties socialProperties,
                                         SocialCoreConfig socialCoreConfig) {
        this.socialProperties = socialProperties;
        this.socialCoreConfig = socialCoreConfig;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        http.apply(socialCoreConfig);

    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // dto nothing
    }

    @Override
    public Map<String, Set<String>> getAuthorizeRequestMap() {
        Set<String> uriSet = new HashSet<>();
        uriSet.add(socialProperties.getCallbackUrl());
        uriSet.add(socialProperties.getCallbackUrl() + "/*");
        uriSet.add(socialProperties.getSocialUserRegisterUrl());
        Map<String, Set<String>> authorizeRequestMap = new HashMap<>(1);
        authorizeRequestMap.put(permitAll, uriSet);
        return authorizeRequestMap;
    }
}
