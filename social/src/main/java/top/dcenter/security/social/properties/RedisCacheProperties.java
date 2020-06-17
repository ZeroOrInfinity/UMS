package top.dcenter.security.social.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/15 19:29
 */
@SuppressWarnings("jol")
@Getter
@Setter
@ConfigurationProperties("redis")
public class RedisCacheProperties {

    @NestedConfigurationProperty
    private Lettuce lettuce = new Lettuce();
    @NestedConfigurationProperty
    private Cache cache = new Cache();

    /**
     * Redis host, 默认 127.0.0.1
     */
    private String host = "127.0.0.1";
    /**
     * Redis cache is open, 默认 false
     */
    private Boolean isOpen = false;
    /**
     * Redis port, 默认 6379
     */
    private Integer port = 6379;
    /**
     * Redis password, 默认 null
     */
    private String password;

    /**
     * 连接超时的时间, 默认: 10000
     */
    private Long timeOut = 10000L;


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

    @Getter
    @Setter
    public static class Lettuce {

        private Pool pool = new Pool();

        /**
         * shutdown timeout. 默认为 5s
         */
        private Duration shutdownTimeout = Duration.ofMillis(5000L);
    }

    @Getter
    @Setter
    public static class Pool {

        /**
         * Maximum number of "idle" connections in the pool. Use a negative value to
         * indicate an unlimited number of idle connections. 默认为 4
         */
        private Integer maxIdle = 4;
        /**
         * Target for the minimum number of idle connections to maintain in the pool. This
         * setting only has an effect if both it and time between eviction runs are
         * positive. 默认为 0
         */
        private Integer minIdle = 0;
        /**
         * Maximum number of connections that can be allocated by the pool at a given
         * time. Use a negative value for no limit. 默认为 8
         */
        private Integer maxActive = 8;
        /**
         * Maximum amount of time a connection allocation should block before throwing an
         * exception when the pool is exhausted. Use a negative value to block
         * indefinitely. 默认为 -1
         */
        private Long maxWait = -1L;
    }
}
