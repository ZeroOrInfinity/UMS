package top.dcenter.ums.security.social.repository.jdbc.key.generator;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.social.config.RedisCacheAutoConfig;

import java.lang.reflect.Method;

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
        sb.append(userId).append(RedisCacheAutoConfig.REDIS_CACHE_HASH_KEY_SEPARATE);
        Connection<?> connection = (Connection<?>) params[0];
        ConnectionData data = connection.createData();
        return sb.append(data.getProviderId()).toString();
    }
}
