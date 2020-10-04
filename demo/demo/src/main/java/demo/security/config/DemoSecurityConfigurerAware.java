package demo.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.ums.security.core.api.config.HttpSecurityAware;
import top.dcenter.ums.security.core.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.social.config.SocialCoreConfig;
import top.dcenter.ums.security.social.properties.SocialProperties;

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
    public void postConfigure(HttpSecurity http) {
        // dto nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) {
        // dto nothing
    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        final Map<UriHttpMethodTuple, Set<String>> permitAllMap = new HashMap<>(16);

        permitAllMap.put(UriHttpMethodTuple.tuple(HttpMethod.GET, "/user/testWebSecurityPostConfigurer"), null);

        Map<String, Map<UriHttpMethodTuple, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.PERMIT_ALL, permitAllMap);
        log.info("Demo ======>: DemoSocialSecurityConfigurerSocial.getAuthorizeRequestMap");

        return resultMap;
    }
}