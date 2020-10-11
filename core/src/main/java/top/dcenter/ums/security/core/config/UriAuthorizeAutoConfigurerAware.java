package top.dcenter.ums.security.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import top.dcenter.ums.security.core.api.config.HttpSecurityAware;
import top.dcenter.ums.security.core.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.properties.ClientProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 验证码相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/15 21:59
 */
@Configuration
@AutoConfigureAfter({ValidateCodeBeanAutoConfiguration.class})
@ConditionalOnMissingBean(type = {"org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration"})
@Slf4j
public class UriAuthorizeAutoConfigurerAware implements HttpSecurityAware {

    private final ClientProperties  clientProperties;

    public UriAuthorizeAutoConfigurerAware(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    @Override
    public void configure(WebSecurity web) {
        // dto nothing
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // dto nothing
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

        final Map<UriHttpMethodTuple, Set<String>> accessMap = new HashMap<>(16);
        String accessExp = clientProperties.getAccessExp();
        accessMap.put(UriHttpMethodTuple.tuple(null, accessExp), null);

        Map<String, Map<UriHttpMethodTuple, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.ACCESS, accessMap);

        return resultMap;
    }
}
