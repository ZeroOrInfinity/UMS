package top.dcenter.ums.security.core.permission.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.core.config.PropertiesAutoConfiguration;
import top.dcenter.ums.security.core.permission.filter.UriAuthorizeFilter;
import top.dcenter.ums.security.core.permission.service.DefaultUriAuthorizeService;

/**
 * 使用 过滤器 的方式进行 uri 访问权限控制,
 * request 的 uri 访问权限控制配置
 * @author zyw
 * @version V1.0  Created by 2020/9/8 11:21
 */
@Configuration
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
public class UriAuthorizeFilterAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService")
    public AbstractUriAuthorizeService uriAuthorizeService() {
        return new DefaultUriAuthorizeService();
    }

    @Bean
    public UriAuthorizeFilter uriAuthorizeFilter(AbstractUriAuthorizeService uriAuthorizeService) {
        return new UriAuthorizeFilter(uriAuthorizeService);
    }

}
