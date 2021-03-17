package top.dcenter.ums.security.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.common.config.SecurityCoreAutoConfigurer;
import top.dcenter.ums.security.filter.ErrorHandlerFilter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 错误过滤器自动配置
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.3.15 0:36
 */
@Configuration
@AutoConfigureBefore({SecurityCoreAutoConfigurer.class})
public class ErrorHttpSecurityAware implements HttpSecurityAware {

    @Override
    public void configure(WebSecurity web) {
        // nothing
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // nothing

    }

    @Override
    public void preConfigure(HttpSecurity http) {
        // nothing

    }

    @Override
    public void postConfigure(HttpSecurity http) {
        http.addFilterAfter(new ErrorHandlerFilter(), WebAsyncManagerIntegrationFilter.class);
    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {
        // nothing
        return Collections.emptyMap();
    }
}