package top.dcenter.security.social.api.config;

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
import top.dcenter.security.social.api.repository.UsersConnectionRepositoryFactory;
import top.dcenter.security.social.properties.SocialProperties;

import javax.sql.DataSource;

/**
 * 第三方登录通用配置，实现第三方授权登录时继承此类。
 * @author zyw
 * @version V1.0  Created by 2020/5/19 18:33
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class OAuth2ConfigurerAdapter extends SocialConfigurerAdapter {

    private final DataSource dataSource;
    private final ConnectionSignUp connectionSignUp;
    private final UsersConnectionRepositoryFactory usersConnectionRepositoryFactory;
    private final TextEncryptor socialTextEncryptor;

    protected final SocialProperties socialProperties;
    @Getter
    protected final ObjectMapper objectMapper;


    public OAuth2ConfigurerAdapter(SocialProperties socialProperties,
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
