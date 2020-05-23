package top.dcenter.security.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * security 配置
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 19:59
 */
@Configuration
@EnableConfigurationProperties({BrowserProperties.class, ValidateCodeProperties.class,
        SmsCodeAuthenticationProperties.class})
public class SecurityConfig {

}
