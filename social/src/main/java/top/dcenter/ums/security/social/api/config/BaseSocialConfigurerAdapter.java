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

package top.dcenter.ums.security.social.api.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.Getter;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import top.dcenter.ums.security.social.api.repository.UsersConnectionRepositoryFactory;
import top.dcenter.ums.security.social.properties.SocialProperties;

import javax.sql.DataSource;

/**
 * 第三方登录通用配置，实现第三方授权登录时继承此类。
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/19 18:33
 */
public class BaseSocialConfigurerAdapter extends SocialConfigurerAdapter {

    private final DataSource dataSource;
    private final ConnectionSignUp connectionSignUp;
    private final UsersConnectionRepositoryFactory usersConnectionRepositoryFactory;
    private final TextEncryptor socialTextEncryptor;

    protected final SocialProperties socialProperties;
    @Getter
    protected final ObjectMapper objectMapper;


    public BaseSocialConfigurerAdapter(SocialProperties socialProperties,
                                       ConnectionSignUp connectionSignUp,
                                       DataSource dataSource,
                                       UsersConnectionRepositoryFactory usersConnectionRepositoryFactory,
                                       TextEncryptor socialTextEncryptor) {
        this.socialProperties = socialProperties;
        this.connectionSignUp = connectionSignUp;
        this.dataSource = dataSource;
        this.usersConnectionRepositoryFactory = usersConnectionRepositoryFactory;
        this.socialTextEncryptor = socialTextEncryptor;

        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {

        return usersConnectionRepositoryFactory
                .getUsersConnectionRepository(dataSource,
                                              connectionFactoryLocator,
                                              socialTextEncryptor,
                                              socialProperties,
                                              connectionSignUp,
                                              socialProperties.getAutoSignIn());
    }
}