package top.dcenter.security.browser.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.api.config.WebSecurityConfigurerAware;
import top.dcenter.security.core.config.PropertiesConfiguration;
import top.dcenter.security.core.properties.BrowserProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 网页端安全配置
 * @author zyw
 * @version V1.0  Created by 2020/6/2 17:30
 */
@Configuration
@AutoConfigureAfter(value = {PropertiesConfiguration.class})
public class BrowserConfigurerAware implements WebSecurityConfigurerAware {

    private final BrowserProperties browserProperties;

    public BrowserConfigurerAware(BrowserProperties browserProperties) {
        this.browserProperties = browserProperties;
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

        permitAllSet.add(this.browserProperties.getLoginUnAuthenticationUrl());
        permitAllSet.add(this.browserProperties.getFailureUrl());
        permitAllSet.add(this.browserProperties.getLoginPage());
        permitAllSet.add(this.browserProperties.getSuccessUrl());
        permitAllSet.add(this.browserProperties.getErrorUrl());
        permitAllSet.add(this.browserProperties.getError4Url());
        permitAllSet.add(this.browserProperties.getError5Url());

        Map<String, Set<String>> permitAllMap = new HashMap<>(1);

        permitAllMap.put(WebSecurityConfigurerAware.permitAll, permitAllSet);

        return permitAllMap;
    }
}
