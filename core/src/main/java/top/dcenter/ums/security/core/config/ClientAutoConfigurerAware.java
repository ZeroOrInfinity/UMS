package top.dcenter.ums.security.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.dcenter.ums.security.core.api.config.HttpSecurityAware;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.properties.ClientProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static top.dcenter.ums.security.core.bean.UriHttpMethodTuple.tuple;

/**
 * 客户端安全配置
 * @author zyw
 * @version V1.0  Created by 2020/6/2 17:30
 */
@Configuration
@AutoConfigureAfter(value = {PropertiesAutoConfiguration.class})
public class ClientAutoConfigurerAware implements HttpSecurityAware {

    /**
     *  网站图标
     */
    public static final String FAVICON = "/**/favicon.ico";
    /**
     *  js
     */
    public static final String JS = "/**/*.js";
    /**
     *  css
     */
    public static final String CSS = "/**/*.css";
    /**
     *  html
     */
    public static final String  HTML = "/**/*.html";

    private final ClientProperties clientProperties;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired(required = false)
    private UmsUserDetailsService umsUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    public ClientAutoConfigurerAware(ClientProperties clientProperties, PasswordEncoder passwordEncoder) {
        this.clientProperties = clientProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configure(WebSecurity web) {
        String[] ignoringUrls = clientProperties.getIgnoringUrls();
        web.ignoring()
                .antMatchers(Objects.requireNonNullElseGet(ignoringUrls, () -> new String[0]));
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (umsUserDetailsService == null)
        {
            throw new RuntimeException("必须实现 UmsUserDetailsService 或 top.dcenter.security.social.api.service.UmsSocialUserDetailsService 抽象类");
        }
        auth.userDetailsService(umsUserDetailsService).passwordEncoder(passwordEncoder);
        auth.eraseCredentials(true);
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

        final Map<UriHttpMethodTuple, Set<String>> permitAllMap = new HashMap<>(16);


        permitAllMap.put(tuple(GET, FAVICON), null);
        permitAllMap.put(tuple(GET, JS), null);
        permitAllMap.put(tuple(GET, CSS), null);
        permitAllMap.put(tuple(GET, HTML), null);
        permitAllMap.put(tuple(GET, clientProperties.getFailureUrl()), null);
        permitAllMap.put(tuple(GET, clientProperties.getLoginPage()), null);
        permitAllMap.put(tuple(GET, clientProperties.getLoginUnAuthenticationRoutingUrl()), null);
        permitAllMap.put(tuple(POST, clientProperties.getLoginProcessingUrl()), null);
        permitAllMap.put(tuple(GET, clientProperties.getErrorUrl()), null);
        permitAllMap.put(tuple(GET, clientProperties.getError4Url()), null);
        permitAllMap.put(tuple(GET, clientProperties.getError5Url()), null);

        Set<String> permitUrls = clientProperties.getPermitUrls();
        permitUrlsFillingPermitAllMap(permitUrls, permitAllMap);

        Map<String, Map<UriHttpMethodTuple, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.PERMIT_ALL, permitAllMap);

        return resultMap;
    }


}
