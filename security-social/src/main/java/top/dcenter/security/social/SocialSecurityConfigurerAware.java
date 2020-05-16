package top.dcenter.security.social;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.SocialWebSecurityConfigurerAware;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * social 第三方授权登录相关配置
 * @see SocialWebSecurityConfigurerAware
 * @author zyw
 * @version V1.0  Created by 2020/5/12 12:02
 */
@Configuration
@Slf4j
public class SocialSecurityConfigurerAware implements SocialWebSecurityConfigurerAware {

    private final SocialProperties socialProperties;

    private final SocialCoreConfigurer socialCoreConfigurer;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SocialSecurityConfigurerAware(SocialProperties socialProperties,
                                         SocialCoreConfigurer socialCoreConfigurer) {
        this.socialProperties = socialProperties;
        this.socialCoreConfigurer = socialCoreConfigurer;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        http.apply(socialCoreConfigurer);

    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public Map<String, Set<String>> getAuthorizeRequestMap() {
        Set<String> uriSet = new HashSet<>();
        uriSet.add(socialProperties.getFilterProcessesUrl() + "/*");
        uriSet.add(socialProperties.getSocialUserInfo());
        Map<String, Set<String>> authorizeRequestMap = new HashMap<>(1);
        authorizeRequestMap.put(permitAll, uriSet);
        return authorizeRequestMap;
    }
}
