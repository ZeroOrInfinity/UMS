package top.dcenter.security.core.api.repository;

import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * UserTokenRepository 工厂, 将用于 {@link PersistentTokenBasedRememberMeServices} 中，用户session持久化
 * @author zyw23
 * @version V1.0
 * Created by 2020/5/24 21:15
 */
public interface UserTokenRepositoryFactory {
    /**
     * 获取 JdbcTokenRepository
     * @return T 实现 {@link PersistentTokenRepository} 实例
     */
    PersistentTokenRepository getJdbcTokenRepository();
}
