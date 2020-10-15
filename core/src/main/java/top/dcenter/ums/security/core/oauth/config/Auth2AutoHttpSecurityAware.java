package top.dcenter.ums.security.core.oauth.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static top.dcenter.ums.security.common.bean.UriHttpMethodTuple.tuple;

/**
 * 第三方授权登录配置
 * @author zyw
 * @version V2.0  Created by 2020/10/5 10:54
 */
@Configuration
@ConditionalOnProperty(prefix = "ums.oauth", name = "enabled", havingValue = "true")
@AutoConfigureAfter({Auth2AutoConfiguration.class})
public class Auth2AutoHttpSecurityAware implements HttpSecurityAware {

    private final Auth2AutoConfigurer auth2AutoConfigurer;
    private final Auth2Properties auth2Properties;

    public Auth2AutoHttpSecurityAware(Auth2AutoConfigurer auth2AutoConfigurer, Auth2Properties auth2Properties) {
        this.auth2AutoConfigurer = auth2AutoConfigurer;
        this.auth2Properties = auth2Properties;
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // 第三方授权登录配置
        if (auth2AutoConfigurer != null)
        {
            http.apply(auth2AutoConfigurer);
        }
    }

    @Override
    public void configure(WebSecurity web) {
        // dto nothing
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // do nothing
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        final Map<UriHttpMethodTuple, Set<String>> permitAllMap = new HashMap<>(16);

        // registrationId = providerId
        permitAllMap.put(tuple(GET, auth2Properties.getRedirectUrlPrefix() + "/*"), null);
        permitAllMap.put(tuple(GET, auth2Properties.getAuthLoginUrlPrefix() + "/*"), null);


        Map<String, Map<UriHttpMethodTuple, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.PERMIT_ALL, permitAllMap);

        return resultMap;
    }

}
