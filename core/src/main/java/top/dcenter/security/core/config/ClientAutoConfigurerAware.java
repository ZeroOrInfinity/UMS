package top.dcenter.security.core.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.api.config.HttpSecurityAware;
import top.dcenter.security.core.properties.ClientProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public static final String FAVICON = "/favicon.ico";
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

    public ClientAutoConfigurerAware(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // dto nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // dto nothing
    }

    @Override
    public Map<String, Map<String, Set<String>>> getAuthorizeRequestMap() {

        final Map<String, Set<String>> permitAllMap = new HashMap<>(16);

        permitAllMap.put(FAVICON, null);
        permitAllMap.put(JS, null);
        permitAllMap.put(CSS, null);
        permitAllMap.put(HTML, null);
        permitAllMap.put(clientProperties.getFailureUrl(), null);
        permitAllMap.put(clientProperties.getLoginPage(), null);
        permitAllMap.put(clientProperties.getLoginUnAuthenticationUrl(), null);
        permitAllMap.put(clientProperties.getSuccessUrl(), null);
        permitAllMap.put(clientProperties.getErrorUrl(), null);
        permitAllMap.put(clientProperties.getError4Url(), null);
        permitAllMap.put(clientProperties.getError5Url(), null);

        clientProperties.getPermitUrls().forEach(uri -> permitAllMap.put(uri, null));

        Map<String, Map<String, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.permitAll, permitAllMap);

        return resultMap;
    }
}
