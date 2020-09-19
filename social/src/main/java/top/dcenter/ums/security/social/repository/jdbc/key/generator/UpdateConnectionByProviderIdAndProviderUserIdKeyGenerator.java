package top.dcenter.ums.security.social.repository.jdbc.key.generator;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import top.dcenter.ums.security.social.config.RedisCacheAutoConfig;

import java.lang.reflect.Method;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/14 21:07
 */
public class UpdateConnectionByProviderIdAndProviderUserIdKeyGenerator extends BaseKeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String userId = (String) super.generate(target, method, params);
        StringBuilder sb = new StringBuilder();
        sb.append(userId).append(RedisCacheAutoConfig.REDIS_CACHE_KEY_SEPARATE);
        Connection<?> connection = (Connection<?>) params[0];
        ConnectionData data = connection.createData();
        return sb.append(data.getProviderId() + RedisCacheAutoConfig.REDIS_CACHE_HASH_KEY_SEPARATE + data.getProviderUserId()).toString();
    }
}
