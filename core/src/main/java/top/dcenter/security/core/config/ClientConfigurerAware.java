package top.dcenter.security.core.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.api.config.WebSecurityConfigurerAware;
import top.dcenter.security.core.properties.ClientProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 客户端安全配置
 * @author zyw
 * @version V1.0  Created by 2020/6/2 17:30
 */
@Configuration
@AutoConfigureAfter(value = {SecurityConfiguration.class})
public class ClientConfigurerAware implements WebSecurityConfigurerAware {

    private final ClientProperties clientProperties;

    public ClientConfigurerAware(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
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
    public Map<String, Set<String>> getAuthorizeRequestMap() {

        Set<String> permitAllSet = new HashSet<>(16);

        permitAllSet.add(clientProperties.getLoginUnAuthenticationUrl());
        permitAllSet.add(clientProperties.getFailureUrl());
        permitAllSet.add(clientProperties.getLoginPage());
        permitAllSet.add(clientProperties.getSuccessUrl());
        permitAllSet.add(clientProperties.getErrorUrl());
        permitAllSet.add(clientProperties.getError4Url());
        permitAllSet.add(clientProperties.getError5Url());

        Map<String, Set<String>> permitAllMap = new HashMap<>(1);

        permitAllMap.put(WebSecurityConfigurerAware.permitAll, permitAllSet);

        return permitAllMap;
    }
}
