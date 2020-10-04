package top.dcenter.ums.security.core.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.ums.security.core.api.config.HttpSecurityAware;
import top.dcenter.ums.security.core.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.properties.ClientProperties;

import java.util.HashMap;
import java.util.Map;
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

    public ClientAutoConfigurerAware(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
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
        permitAllMap.put(tuple(GET, clientProperties.getSuccessUrl()), null);
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
