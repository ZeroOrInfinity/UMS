package top.dcenter.ums.security.social.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * 对第三方授权登录、绑定与解绑功能的持久化（jdbc）数据是否缓存到 redis 的配置
 * @author zyw
 * @version V1.0  Created by 2020/6/15 19:29
 */
@SuppressWarnings("jol")
@Getter
@Setter
@ConfigurationProperties("redis")
public class RedisCacheProperties {

    @NestedConfigurationProperty
    private Cache cache = new Cache();

    /**
     * Redis cache is open, 默认 false
     */
    private Boolean open = false;

    /**
     * 是否使用 spring IOC 容器中的 RedisConnectionFactory， 默认： false. <br>
     * 如果使用 spring IOC 容器中的 RedisConnectionFactory，则要注意 cache.database-index 要与 spring.redis.database 一样。
     */
    private Boolean useIocRedisConnectionFactory = false;



    @Getter
    @Setter
    public static class Cache {

        /**
         * redis cache 存放的 database index, 默认: 0
         */
        private Integer databaseIndex = 0;
        /**
         * 设置缓存管理器管理的缓存的默认过期时间, 默认: 200s
         */
        private Duration defaultExpireTime = Duration.ofSeconds(200);
        /**
         * Set the ttl to apply for cache entries. Use Duration.ZERO to declare an eternal cache. 默认: 180s
         */
        private Duration entryTtl = Duration.ofSeconds(180);
        /**
         * Names of the default caches to consider for caching operations defined
         * in the annotated class.
         */
        private Set<String> cacheNames = new HashSet<>();

    }

}
