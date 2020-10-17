package top.dcenter.ums.security.core.permission.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import top.dcenter.ums.security.core.api.permission.service.UriAuthorizeService;
import top.dcenter.ums.security.core.auth.config.PropertiesAutoConfiguration;
import top.dcenter.ums.security.core.permission.annotation.UriAuthorize;
import top.dcenter.ums.security.core.permission.interceptor.UriAuthorizationAnnotationInterceptor;

/**
 * 使用 注解({@link UriAuthorize} 和 {@link org.springframework.security.access.prepost.PreAuthorize}) 的方式进行 uri 访问权限控制,
 * request 的 uri 访问权限控制配置类
 * @author zyw
 * @version V1.0  Created by 2020/9/8 11:21
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class UriAuthorizeInterceptorAutoConfiguration {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public UriAuthorizationAnnotationInterceptor uriAuthorizationAnnotationInterceptor(UriAuthorizeService uriAuthorizeService) {
        return new UriAuthorizationAnnotationInterceptor(uriAuthorizeService);
    }

}
