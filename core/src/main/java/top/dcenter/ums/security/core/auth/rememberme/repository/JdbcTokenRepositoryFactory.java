package top.dcenter.ums.security.core.auth.rememberme.repository;

import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.ums.security.core.api.rememberme.repository.BasedRememberMeTokenRepositoryFactory;

import javax.sql.DataSource;

/**
 * JdbcTokenRepository 工厂，, 将用于 {@link PersistentTokenBasedRememberMeServices} 中，用户session持久化.<br>
 * @author zyw
 * @version V1.0  Created by 2020/5/24 21:47
 */
public class JdbcTokenRepositoryFactory implements BasedRememberMeTokenRepositoryFactory {

    private final DataSource dataSource;

    public JdbcTokenRepositoryFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public PersistentTokenRepository getPersistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(this.dataSource);
        return tokenRepository;
    }
}
