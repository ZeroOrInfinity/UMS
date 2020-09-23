package top.dcenter.ums.security.social.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.dcenter.ums.security.core.config.SecurityAutoConfiguration;
import top.dcenter.ums.security.social.properties.SocialProperties;

/**
 * @author zyw
 * @version V1.0  Created by 2020/9/23 21:16
 */
@Configuration
@AutoConfigureAfter({SecurityAutoConfiguration.class})
@EnableConfigurationProperties({SocialProperties.class})
@Slf4j
public class PropertiesAutoConfiguration {

}
