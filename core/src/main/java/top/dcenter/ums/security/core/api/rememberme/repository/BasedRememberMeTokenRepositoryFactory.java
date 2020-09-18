package top.dcenter.ums.security.core.api.rememberme.repository;

import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.ums.security.core.auth.rememberme.repository.JdbcTokenRepositoryFactory;

/**
 * PersistentTokenRepository 工厂, 将用于 {@link PersistentTokenBasedRememberMeServices} 中，用户 RememberMe 持久化. 默认实现了
 * {@link JdbcTokenRepositoryFactory}, 如需持久化到 redis, 则实现此接口并注入
 * IOC 容器即可, 会替代默认的 jdbc 的持久化.
 *
 * UserTokenRepositoryFactory
 * @author zyw
 * @version V1.0
 * Created by 2020/5/24 21:15
 */
public interface BasedRememberMeTokenRepositoryFactory {
    /**
     * 获取 UserTokenRepository
     * @return T 实现 {@link PersistentTokenRepository} 实例
     */
    PersistentTokenRepository getPersistentTokenRepository();
}
