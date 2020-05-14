package top.dcenter.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.WebSecurityPostConfigurer;
import top.dcenter.security.social.SocialCoreConfigurer;
import top.dcenter.security.social.SocialProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义 WebSecurityPostConfigurer 接口
 * @see top.dcenter.security.core.WebSecurityPostConfigurer
 * @author zyw
 * @version V1.0  Created by 2020/5/12 12:02
 */
@Configuration
@Slf4j
public class DemoSocialSecurityPostConfigurer implements WebSecurityPostConfigurer {

    private final SocialProperties socialProperties;

    private final SocialCoreConfigurer socialCoreConfigurer;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public DemoSocialSecurityPostConfigurer(SocialProperties socialProperties,
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
        log.info("Demo ======>: DemoSocialSecurityPostConfigurer.getAuthorizeRequestMap");
        return authorizeRequestMap;
    }
}