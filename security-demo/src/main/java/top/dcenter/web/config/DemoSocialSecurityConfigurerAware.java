package top.dcenter.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.SocialWebSecurityConfigurerAware;
import top.dcenter.security.social.SocialCoreConfigurer;
import top.dcenter.security.social.SocialProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义 SocialWebSecurityConfigurerAware 接口
 * @see SocialWebSecurityConfigurerAware
 * @author zyw
 * @version V1.0  Created by 2020/5/12 12:02
 */
@Configuration
@Slf4j
public class DemoSocialSecurityConfigurerAware implements SocialWebSecurityConfigurerAware {

    private final SocialProperties socialProperties;

    private final SocialCoreConfigurer socialCoreConfigurer;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public DemoSocialSecurityConfigurerAware(SocialProperties socialProperties,
                                             SocialCoreConfigurer socialCoreConfigurer) {
        this.socialProperties = socialProperties;
        this.socialCoreConfigurer = socialCoreConfigurer;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public Map<String, List<String>> getAuthorizeRequestMap() {
        Map<String, List<String>> authorizeRequestMap = new HashMap<>();
        List<String> uriList = new ArrayList<>(authorizeRequestMap.size());
        uriList.add("/user/testWebSecurityPostConfigurer");
        authorizeRequestMap.put(permitAll, uriList);
        log.info("Demo ======>: DemoSocialSecurityConfigurerSocial.getAuthorizeRequestMap");
        return authorizeRequestMap;
    }
}