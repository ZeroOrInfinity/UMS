package top.dcenter.security.social;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.WebSecurityPostConfigurer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see top.dcenter.security.core.WebSecurityPostConfigurer
 * @author zyw
 * @version V1.0  Created by 2020/5/12 12:02
 */
@Configuration
@Slf4j
public class SocialSecurityPostConfigurer implements WebSecurityPostConfigurer {

    private final SocialProperties socialProperties;

    private final SocialCoreConfigurer socialCoreConfigurer;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SocialSecurityPostConfigurer(SocialProperties socialProperties,
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
    public Map<String, List<String>> getAuthorizeRequestMap() {
        Map<String, List<String>> authorizeRequestMap = new HashMap<>();
        List<String> uriList = new ArrayList<>(authorizeRequestMap.size());
        uriList.add(socialProperties.getFilterProcessesUrl() + "/*");
        uriList.add(socialProperties.getSingUpUrl());
        uriList.add(socialProperties.getFailureUrl());
        uriList.add("/user/regist");
        authorizeRequestMap.put(permitAll, uriList);
        return authorizeRequestMap;
    }
}
