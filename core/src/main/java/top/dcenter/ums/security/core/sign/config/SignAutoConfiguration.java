package top.dcenter.ums.security.core.sign.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import top.dcenter.ums.security.core.config.SecurityAutoConfiguration;
import top.dcenter.ums.security.core.properties.SignProperties;
import top.dcenter.ums.security.core.api.sign.service.SignService;
import top.dcenter.ums.security.core.sign.UserSignServiceImpl;

/**
 * 签到配置类
 * @author zyw
 * @version V1.0  Created by 2020/9/15 12:23
 */
@Configuration
@AutoConfigureAfter({SecurityAutoConfiguration.class})
public class SignAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.sign.service.SignService")
    public SignService signService(RedisConnectionFactory redisConnectionFactory, SignProperties signProperties) {
        return new UserSignServiceImpl(redisConnectionFactory, signProperties);
    }
}
