package top.dcenter.security.social.repository.jdbc.key.generator;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.stereotype.Component;
import top.dcenter.security.social.repository.jdbc.key.generator.BaseKeyGenerator;

import java.lang.reflect.Method;

import static top.dcenter.security.social.config.RedisCacheConfig.REDIS_CACHE_HASH_KEY_SEPARATE;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/14 21:07
 */
@Component("updateConnectionByProviderIdKeyGenerator")
public class UpdateConnectionByProviderIdKeyGenerator extends BaseKeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String userId = (String) super.generate(target, method, params);
        StringBuilder sb = new StringBuilder();
        sb.append(userId).append(REDIS_CACHE_HASH_KEY_SEPARATE);
        Connection<?> connection = (Connection<?>) params[0];
        ConnectionData data = connection.createData();
        return sb.append(data.getProviderId()).toString();
    }
}
