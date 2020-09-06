package top.dcenter.security.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/23 23:30
 */
@Configuration
@EnableResourceServer
public class AuthorizationResourcesConfig {
    // todo 增加一个 filter 自动添加 token 到 header Authorization; 格式: bearer {token}
}
