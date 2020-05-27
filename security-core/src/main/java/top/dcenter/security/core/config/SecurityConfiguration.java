package top.dcenter.security.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.properties.SmsCodeAuthenticationProperties;
import top.dcenter.security.core.properties.ValidateCodeProperties;

/**
 * security 配置
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 19:59
 */
@Configuration
@EnableConfigurationProperties({BrowserProperties.class, ValidateCodeProperties.class,
        SmsCodeAuthenticationProperties.class})
public class SecurityConfiguration {

}
