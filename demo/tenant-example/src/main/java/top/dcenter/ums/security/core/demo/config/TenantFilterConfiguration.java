package top.dcenter.ums.security.core.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.core.demo.filter.TenantContextFilter;
import top.dcenter.ums.security.core.demo.tenant.UmsTenantContextHolder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.3.20 17:19
 */
@Configuration
@RequiredArgsConstructor
public class TenantFilterConfiguration implements HttpSecurityAware {

    private final UmsTenantContextHolder tenantContextHolder;

    @Override
    public void configure(WebSecurity web) {
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new TenantContextFilter(this.tenantContextHolder),
                             WebAsyncManagerIntegrationFilter.class);
    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        return Collections.emptyMap();
    }
}
