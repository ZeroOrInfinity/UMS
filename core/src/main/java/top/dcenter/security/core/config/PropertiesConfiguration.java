package top.dcenter.security.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.properties.SmsCodeLoginAuthenticationProperties;
import top.dcenter.security.core.properties.ValidateCodeProperties;

/**
 * Properties 配置
 * @author zyw
 * @version V1.0  Created by 2020/5/29 14:42
 */
@Configuration()
@EnableConfigurationProperties({BrowserProperties.class, ValidateCodeProperties.class,
        SmsCodeLoginAuthenticationProperties.class})
public class PropertiesConfiguration {
}
