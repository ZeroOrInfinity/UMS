package top.dcenter.security.core.social;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;

import javax.sql.DataSource;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/8 22:21
 */
@Configuration
@EnableSocial
@Slf4j
public class SocialConfig extends SocialConfigurerAdapter {
    private final DataSource dataSource;
    // TODO 更改为有效的加密方法
    private TextEncryptor textEncryptor = Encryptors.noOpText();

    public SocialConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        // TODO jdbcUsersConnectionRepository.setConnectionSignUp(), 默认的数据库表 user_connection 只是通用表，不适合生产，做一个 spi
        //  接口适配一下
        JdbcUsersConnectionRepository jdbcUsersConnectionRepository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, textEncryptor);
        jdbcUsersConnectionRepository.setTablePrefix("social_");
        return jdbcUsersConnectionRepository;
    }
}
