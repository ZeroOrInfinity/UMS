package top.dcenter.security.social.repository.jdbc;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/14 21:07
 */
public abstract class BaseKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {

        JdbcConnectionRepository jdbcConnectionRepository = (JdbcConnectionRepository) target;
        return jdbcConnectionRepository.getUserId();
    }
}
