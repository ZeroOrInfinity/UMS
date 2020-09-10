package top.dcenter.security.core.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.dcenter.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.security.core.permission.interceptor.UriAuthorizationAnnotationInterceptor;
import top.dcenter.security.core.permission.service.DefaultUriAuthorizeService;

/**
 * request 的 uri 访问权限控制配置类
 * @author zyw
 * @version V1.0  Created by 2020/9/8 11:21
 */
@Configuration
@AutoConfigureAfter({PropertiesConfiguration.class, SecurityConfiguration.class})
public class UriAuthorizeConfiguration {

    private UriAuthorizationAnnotationInterceptor uriAuthorizationAnnotationInterceptor;

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.api.permission.service.AbstractUriAuthorizeService")
    public AbstractUriAuthorizeService uriAuthorizeService() {
        return new DefaultUriAuthorizeService();
    }

    @Bean
    public UriAuthorizationAnnotationInterceptor uriAuthorizationAnnotationInterceptor(AbstractUriAuthorizeService uriAuthorizeService) {
        return new UriAuthorizationAnnotationInterceptor(uriAuthorizeService);
    }

}
