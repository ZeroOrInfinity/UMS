/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.auth.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.ums.security.common.propertis.RememberMeProperties;
import top.dcenter.ums.security.core.api.rememberme.repository.BasedRememberMeTokenRepositoryFactory;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.auth.rememberme.repository.JdbcTokenRepositoryFactory;

import javax.sql.DataSource;

/**
 * RememberMe 相关配置
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/28 21:44
 */
@Configuration
@ConditionalOnProperty(prefix = "ums.client.remember-me", name = "enable", havingValue = "true")
@AutoConfigureAfter({PropertiesAutoConfiguration.class, SecurityAutoConfiguration.class})
public class SecurityRememberMeAutoConfiguration {

    private final DataSource dataSource;

    public SecurityRememberMeAutoConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Bean
    public RememberMeProperties rememberMeProperties(ClientProperties clientProperties) {
        return clientProperties.getRememberMe();
    }

    /**
     * 与 spring Security RememberMe 功能相关,
     * @return  {@link BasedRememberMeTokenRepositoryFactory}
     */
    @Bean
    @ConditionalOnMissingBean(type = {"top.dcenter.ums.security.core.api.rememberme.repository.BasedRememberMeTokenRepositoryFactory",
                                "org.springframework.security.web.authentication.RememberMeServices"})
    public BasedRememberMeTokenRepositoryFactory userTokenRepositoryFactory() {
        return new JdbcTokenRepositoryFactory(this.dataSource);
    }

    /**
     * 与 spring Security RememberMe 功能相关,
     * @return {@link PersistentTokenRepository}
     */
    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.security.web.authentication.RememberMeServices")
    public PersistentTokenRepository persistentTokenRepository(BasedRememberMeTokenRepositoryFactory userTokenRepositoryFactory) {
        return userTokenRepositoryFactory.getPersistentTokenRepository();
    }
}