package top.dcenter.ums.security.core.auth.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.csrf.CsrfFilter;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.auth.filter.AjaxOrFormRequestFilter;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;

import java.util.Map;
import java.util.Set;

/**
 * csrf 配置
 * @author zyw
 * @version V1.0  Created by 2020/6/7 23:51
 */
@Configuration
@AutoConfigureAfter(value = {SecurityCsrfAutoConfiguration.class, PropertiesAutoConfiguration.class})
public class CsrfAutoConfigurerAware implements HttpSecurityAware {

    private final ClientProperties clientProperties;
    private final ObjectMapper objectMapper;

    public CsrfAutoConfigurerAware(ClientProperties clientProperties, ObjectMapper objectMapper) {
        this.clientProperties = clientProperties;
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

    @SuppressWarnings("RedundantThrows")
    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // 添加 AjaxOrFormRequestFilter 增加对 Ajax 格式与 form 格式的解析,
        http.addFilterBefore(new AjaxOrFormRequestFilter(objectMapper), CsrfFilter.class);
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
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        return null;
    }
}
