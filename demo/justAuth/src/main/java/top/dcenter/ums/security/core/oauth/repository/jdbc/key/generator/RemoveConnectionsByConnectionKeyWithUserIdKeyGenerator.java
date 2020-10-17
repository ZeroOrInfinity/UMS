package top.dcenter.ums.security.core.oauth.repository.jdbc.key.generator;


import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.oauth.entity.ConnectionKey;
import top.dcenter.ums.security.core.oauth.config.RedisCacheAutoConfiguration;

import java.lang.reflect.Method;

/**
 * @author zyw
 * @version V2.0  Created by 2020/6/14 21:07
 */
public class RemoveConnectionsByConnectionKeyWithUserIdKeyGenerator implements KeyGenerator {

    @NonNull
    @Override
    public Object generate(@NonNull Object target, @NonNull Method method, Object... params) {
        String userId = (String) params[0];
        ConnectionKey key = (ConnectionKey) params[1];
        return "h:" + userId + RedisCacheAutoConfiguration.REDIS_CACHE_KEY_SEPARATE +
                key.getProviderId() + RedisCacheAutoConfiguration.REDIS_CACHE_HASH_KEY_SEPARATE + key.getProviderUserId();
    }
}
