package top.dcenter.ums.security.core.permission.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.dcenter.ums.security.core.permission.interceptor.UriAuthorizationAnnotationInterceptor;

/**
 * 注册 的 uri 访问权限控制拦截器
 * @author zyw
 * @version V1.0  Created by 2020/9/8 11:21
 */
@Configuration
@AutoConfigureAfter({UriAuthorizeInterceptorAutoConfiguration.class})
public class UriAuthorizeWebMvcAutoConfigurer implements WebMvcConfigurer {
    
    private UriAuthorizationAnnotationInterceptor uriAuthorizationAnnotationInterceptor;

    public UriAuthorizeWebMvcAutoConfigurer(UriAuthorizationAnnotationInterceptor uriAuthorizationAnnotationInterceptor) {
        this.uriAuthorizationAnnotationInterceptor = uriAuthorizationAnnotationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(uriAuthorizationAnnotationInterceptor);

    }
}
