package top.dcenter.security.core.social.qq.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.security.SpringSocialConfigurer;
import top.dcenter.security.core.properties.SocialProperties;
import top.dcenter.security.core.social.SocialCoreConfigurer;
import top.dcenter.security.core.social.qq.connect.QqConnectionFactory;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/8 23:36
 */
@Configuration
@ConditionalOnProperty(prefix = "security.social.qq", name = "app-id")
public class QqAutoConfig extends SocialAutoConfigurerAdapter {

    private final SocialProperties socialProperties;

    public QqAutoConfig(SocialProperties socialProperties) {
        this.socialProperties = socialProperties;
    }

    @Override
    protected ConnectionFactory<?> createConnectionFactory() {
        SocialProperties.QqProperties qq = socialProperties.getQq();
        return new QqConnectionFactory(qq.getProviderId(), qq.getAppId(), qq.getAppSecret());
    }

    @Bean
    public SpringSocialConfigurer socialCoreConfigurer() {
        return new SocialCoreConfigurer(socialProperties.getFilterProcessesUrl());
    }

}
