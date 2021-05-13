package top.dcenter.ums.security.core.oauth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.oauth.properties.OneClickLoginProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpMethod.POST;
import static top.dcenter.ums.security.common.bean.UriHttpMethodTuple.tuple;

/**
 * 一键登录配置
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.5.13 17:06
 */
@Configuration
@ConditionalOnProperty(prefix = "ums.one-click-login", name = "enable", havingValue = "true")
@AutoConfigureAfter({OneClickLoginAutoConfigurer.class})
@Slf4j
public class OneClickLoginHttpSecurityAware implements HttpSecurityAware {

    private final OneClickLoginAutoConfigurer oneClickLoginAutoConfigurer;
    private final OneClickLoginProperties oneClickLoginProperties;

    public OneClickLoginHttpSecurityAware(OneClickLoginAutoConfigurer oneClickLoginAutoConfigurer,
                                          OneClickLoginProperties oneClickLoginProperties) {
        this.oneClickLoginAutoConfigurer = oneClickLoginAutoConfigurer;
        this.oneClickLoginProperties = oneClickLoginProperties;
    }

    @Override
    public void configure(WebSecurity web) {
        // dto nothing
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // dto nothing
    }

    @Override
    public void postConfigure(HttpSecurity http) {
        // dto nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // 一键登录配置
        if (oneClickLoginAutoConfigurer != null)
        {
            http.apply(oneClickLoginAutoConfigurer);
        }
    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        final Map<UriHttpMethodTuple, Set<String>> permitAllMap = new HashMap<>(16);

        permitAllMap.put(tuple(POST, oneClickLoginProperties.getLoginProcessingUrl()), null);

        Map<String, Map<UriHttpMethodTuple, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.PERMIT_ALL, permitAllMap);

        return resultMap;
    }
}
