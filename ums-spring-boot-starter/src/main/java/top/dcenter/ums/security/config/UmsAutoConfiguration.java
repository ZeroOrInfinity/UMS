package top.dcenter.ums.security.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.auth.config.SecurityAutoConfiguration;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.handler.UmsAuthenticationSuccessHandler;
import top.dcenter.ums.security.properties.UmsProperties;

/**
 * ums 配置
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.1.10 12:10
 */
@Configuration
@Order(99)
@AutoConfigureAfter({UmsPropertiesAutoConfiguration.class})
@AutoConfigureBefore({SecurityAutoConfiguration.class})
public class UmsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler")
    public BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler(ClientProperties clientProperties,
                                                                             RedisConnectionFactory redisConnectionFactory,
                                                                             UmsProperties umsProperties) {
        return new UmsAuthenticationSuccessHandler(clientProperties, null,
                                                   redisConnectionFactory,
                                                   umsProperties);
    }
}
