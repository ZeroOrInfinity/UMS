package top.dcenter.ums.security.social.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.ums.security.core.api.config.HttpSecurityAware;
import top.dcenter.ums.security.social.properties.SocialProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 把 social 第三方授权登录相关配置添加到 HttpSecurity 中。
 * @see HttpSecurityAware
 * @author zyw
 * @version V1.0  Created by 2020/5/12 12:02
 */
@Configuration
@AutoConfigureAfter({SocialAutoConfiguration.class})
@Slf4j
public class SocialSecurityAutoConfigurerAware implements HttpSecurityAware {

    private final SocialProperties socialProperties;

    private final SocialCoreConfig socialCoreConfig;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SocialSecurityAutoConfigurerAware(SocialProperties socialProperties,
                                             SocialCoreConfig socialCoreConfig) {
        this.socialProperties = socialProperties;
        this.socialCoreConfig = socialCoreConfig;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        http.apply(socialCoreConfig);

    }

    @Override
    public void preConfigure(HttpSecurity http) {
        // dto nothing
    }

    @Override
    public Map<String, Map<String, Set<String>>> getAuthorizeRequestMap() {
        final Map<String, Set<String>> permitAllMap = new HashMap<>(16);

        permitAllMap.put(socialProperties.getCallbackUrl(), null);
        permitAllMap.put(socialProperties.getCallbackUrl() + "/*", null);
        permitAllMap.put(socialProperties.getSocialUserRegisterUrl(), null);

        Map<String, Map<String, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.PERMIT_ALL, permitAllMap);

        return resultMap;
    }
}
