package top.dcenter.security.core.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import top.dcenter.security.core.api.config.HttpSecurityAware;
import top.dcenter.security.core.properties.ClientProperties;

import java.util.Map;
import java.util.Set;

/**
 * csrf 配置
 * @author zyw
 * @version V1.0  Created by 2020/6/7 23:51
 */
@Configuration
@AutoConfigureAfter(value = {SecurityCsrfConfiguration.class, SecurityConfiguration.class})
public class CsrfConfigurerAware implements HttpSecurityAware {

    private final ClientProperties clientProperties;

    public CsrfConfigurerAware(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // dto nothing

    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        ClientProperties.CsrfProperties csrfProperties = clientProperties.getCsrf();
        if (csrfProperties.getCsrfIsOpen())
        {
            CsrfConfigurer<HttpSecurity> csrf = http.csrf();

            Set<String> ignoringAntMatcherUrls = csrfProperties.getIgnoringAntMatcherUrls();
            String[] urls = new String[ignoringAntMatcherUrls.size()];
            ignoringAntMatcherUrls.toArray(urls);

            csrf.ignoringAntMatchers(urls);

        } else
        {
            http.csrf().disable();
        }

    }

    @Override
    public Map<String, Map<String, Set<String>>> getAuthorizeRequestMap() {
        return null;
    }
}
