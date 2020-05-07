package top.dcenter.security.core.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.dcenter.security.core.validate.code.ValidateCodeProperties;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/3 19:59
 */
@Configuration
@EnableConfigurationProperties({BrowserProperties.class, ValidateCodeProperties.class})
public class SecurityConfig {
}
