package top.dcenter.ums.security.social.repository.jdbc.key.generator;

import org.springframework.social.connect.ConnectionKey;
import top.dcenter.ums.security.social.config.RedisCacheAutoConfig;

import java.lang.reflect.Method;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/14 21:07
 */
public class RemoveConnectionsByConnectionKeyKeyGenerator extends BaseKeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String userId = (String) super.generate(target, method, params);
        StringBuilder sb = new StringBuilder();
        sb.append(userId).append(RedisCacheAutoConfig.REDIS_CACHE_HASH_KEY_SEPARATE);
        ConnectionKey connectionKey = (ConnectionKey) params[0];
        return sb.append(connectionKey.getProviderId()).toString();
    }
}
