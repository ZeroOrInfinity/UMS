package top.dcenter.ums.security.core.permission.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.core.config.PropertiesAutoConfiguration;
import top.dcenter.ums.security.core.permission.interceptor.UriAuthorizationAnnotationInterceptor;
import top.dcenter.ums.security.core.permission.service.DefaultUriAuthorizeService;
import top.dcenter.ums.security.core.permission.annotation.UriAuthorize;

/**
 * 使用 注解({@link UriAuthorize} 和 {@link org.springframework.security.access.prepost.PreAuthorize}) 的方式进行 uri 访问权限控制,
 * request 的 uri 访问权限控制配置类
 * @author zyw
 * @version V1.0  Created by 2020/9/8 11:21
 */
@Configuration
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class UriAuthorizeInterceptorAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService")
    public AbstractUriAuthorizeService uriAuthorizeService() {
        return new DefaultUriAuthorizeService();
    }

    @Bean
    public UriAuthorizationAnnotationInterceptor uriAuthorizationAnnotationInterceptor(AbstractUriAuthorizeService uriAuthorizeService) {
        return new UriAuthorizationAnnotationInterceptor(uriAuthorizeService);
    }

}
