package top.dcenter.ums.security.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.dcenter.ums.security.core.properties.ClientProperties;
import top.dcenter.ums.security.core.properties.SignProperties;
import top.dcenter.ums.security.core.properties.SmsCodeLoginAuthenticationProperties;
import top.dcenter.ums.security.core.properties.ValidateCodeProperties;

/**
 * Properties 配置
 * @author zyw
 * @version V1.0  Created by 2020/5/29 14:42
 */
@Configuration()
@EnableConfigurationProperties({ClientProperties.class, ValidateCodeProperties.class,
        SmsCodeLoginAuthenticationProperties.class, SignProperties.class})
public class PropertiesAutoConfiguration {
}
