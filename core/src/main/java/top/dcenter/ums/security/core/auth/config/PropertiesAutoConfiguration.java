package top.dcenter.ums.security.core.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;
import top.dcenter.ums.security.core.oauth.properties.ExecutorProperties;
import top.dcenter.ums.security.core.oauth.properties.RedisCacheProperties;
import top.dcenter.ums.security.core.oauth.properties.RepositoryProperties;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.sign.properties.SignProperties;
import top.dcenter.ums.security.core.auth.properties.SmsCodeLoginAuthenticationProperties;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;

/**
 * Properties 配置
 * @author zyw
 * @version V1.0  Created by 2020/5/29 14:42
 */
@Configuration()
@Order(98)
@EnableConfigurationProperties({ClientProperties.class, ValidateCodeProperties.class,
        SmsCodeLoginAuthenticationProperties.class, SignProperties.class,
        Auth2Properties.class, RepositoryProperties.class,
        RedisCacheProperties.class, ExecutorProperties.class})
public class PropertiesAutoConfiguration {
}
