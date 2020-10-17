package top.dcenter.ums.security.core.auth.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/7 23:52
 */
@Configuration
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
public class SecurityCsrfAutoConfiguration {

}
