package top.dcenter.security.browser.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import top.dcenter.security.core.config.PropertiesConfiguration;

/**
 * spring session 相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/28 21:44
 */
@Configuration
@AutoConfigureAfter({PropertiesConfiguration.class})
@Slf4j
public class SecuritySessionConfiguration {

}
