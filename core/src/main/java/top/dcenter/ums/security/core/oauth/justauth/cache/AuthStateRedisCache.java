package top.dcenter.ums.security.core.oauth.justauth.cache;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.dcenter.ums.security.core.oauth.justauth.enums.CacheKeyStrategy;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;
import top.dcenter.ums.security.core.oauth.properties.JustAuthProperties;

/**
 * auth state redis cache, 适用单机与分布式
 * @author zyw
 * @version V1.0  Created by 2020/10/6 19:22
 */
public class AuthStateRedisCache implements Auth2StateCache {

    private final JustAuthProperties justAuthProperties;
    private final StringRedisTemplate stringRedisTemplate;

    public AuthStateRedisCache(Auth2Properties auth2Properties, StringRedisTemplate stringRedisTemplate) {
        this.justAuthProperties = auth2Properties.getJustAuth();
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void cache(String key, String value) {
        this.cache(key, value, justAuthProperties.getTimeout().toMillis());
    }

    @Override
    public void cache(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(justAuthProperties.getCacheKeyPrefix() + key, value, timeout);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(justAuthProperties.getCacheKeyPrefix() + key);
    }

    @Override
    public boolean containsKey(String key) {
        return StringUtils.isNotBlank(stringRedisTemplate.opsForValue().get(key));
    }

    @Override
    public CacheKeyStrategy getCacheKeyStrategy() {
        return CacheKeyStrategy.UUID;
    }
}