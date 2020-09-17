package top.dcenter.security.social.repository.jdbc.key.generator;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static top.dcenter.security.social.config.RedisCacheAutoConfig.REDIS_CACHE_HASH_KEY_SEPARATE;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/14 21:07
 */
@Component("removeConnectionsByProviderIdKeyGenerator")
public class RemoveConnectionsByProviderIdKeyGenerator extends BaseKeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String userId = (String) super.generate(target, method, params);
        StringBuilder sb = new StringBuilder();
        sb.append(userId).append(REDIS_CACHE_HASH_KEY_SEPARATE);
        String providerId = (String) params[0];
        return sb.append(providerId).toString();
    }
}
