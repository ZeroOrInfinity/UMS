package top.dcenter.ums.security.core.auth.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.ums.security.core.api.rememberme.repository.BasedRememberMeTokenRepositoryFactory;
import top.dcenter.ums.security.core.auth.rememberme.repository.JdbcTokenRepositoryFactory;

import javax.sql.DataSource;

/**
 * RememberMe 相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/28 21:44
 */
@Configuration
@AutoConfigureAfter({PropertiesAutoConfiguration.class, SecurityAutoConfiguration.class})
public class SecurityRememberMeAutoConfiguration {

    private final DataSource dataSource;

    public SecurityRememberMeAutoConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * 与 spring Security RememberMe 功能相关,
     * @return  {@link BasedRememberMeTokenRepositoryFactory}
     */
    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.rememberme.repository.BasedRememberMeTokenRepositoryFactory")
    public BasedRememberMeTokenRepositoryFactory userTokenRepositoryFactory() {
        return new JdbcTokenRepositoryFactory(this.dataSource);
    }

    /**
     * 与 spring Security RememberMe 功能相关,
     * @return {@link PersistentTokenRepository}
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        return userTokenRepositoryFactory().getPersistentTokenRepository();
    }
}
