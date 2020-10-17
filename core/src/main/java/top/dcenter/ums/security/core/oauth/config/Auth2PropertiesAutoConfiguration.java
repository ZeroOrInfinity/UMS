package top.dcenter.ums.security.core.oauth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;
import top.dcenter.ums.security.core.oauth.properties.ExecutorProperties;
import top.dcenter.ums.security.core.oauth.properties.RedisCacheProperties;
import top.dcenter.ums.security.core.oauth.properties.RepositoryProperties;

/**
 * Properties 配置
 * @author zyw
 * @version V1.0  Created by 2020/5/29 14:42
 */
@Configuration()
@Order(98)
@EnableConfigurationProperties({
        Auth2Properties.class, RepositoryProperties.class,
        RedisCacheProperties.class, ExecutorProperties.class})
public class Auth2PropertiesAutoConfiguration {
}
