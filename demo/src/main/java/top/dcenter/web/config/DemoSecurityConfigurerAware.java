package top.dcenter.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.api.config.HttpSecurityAware;
import top.dcenter.security.social.api.config.SocialCoreConfig;
import top.dcenter.security.social.properties.SocialProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 自定义 HttpSecurityAware 接口
 * @see HttpSecurityAware
 * @author zyw
 * @version V1.0  Created by 2020/5/12 12:02
 */
@Configuration
@Slf4j
public class DemoSecurityConfigurerAware implements HttpSecurityAware {

    private final SocialProperties socialProperties;

    private final SocialCoreConfig socialCoreConfig;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public DemoSecurityConfigurerAware(SocialProperties socialProperties,
                                       SocialCoreConfig socialCoreConfig) {
        this.socialProperties = socialProperties;
        this.socialCoreConfig = socialCoreConfig;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // dto nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // dto nothing
    }

    @Override
    public Map<String, Map<String, Set<String>>> getAuthorizeRequestMap() {
        final Map<String, Set<String>> permitAllMap = new HashMap<>(16);

        permitAllMap.put("/user/testWebSecurityPostConfigurer", null);

        Map<String, Map<String, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.permitAll, permitAllMap);
        log.info("Demo ======>: DemoSocialSecurityConfigurerSocial.getAuthorizeRequestMap");

        return resultMap;
    }
}