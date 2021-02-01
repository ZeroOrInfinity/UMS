/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.oauth2.client.jackson2.OAuth2ClientJackson2Module;
import org.springframework.security.web.jackson2.WebJackson2Module;
import org.springframework.security.web.jackson2.WebServletJackson2Module;
import org.springframework.security.web.server.jackson2.WebServerJackson2Module;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.core.redis.cache.RedisHashCacheManager;
import top.dcenter.ums.security.core.redis.jackson2.Auth2Jackson2Module;
import top.dcenter.ums.security.core.redis.key.generator.RemoveConnectionsByConnectionKeyWithUserIdKeyGenerator;
import top.dcenter.ums.security.core.redis.properties.RedisCacheProperties;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static top.dcenter.ums.security.common.consts.RedisCacheConstants.USER_CONNECTION_CACHE_NAME;
import static top.dcenter.ums.security.common.consts.RedisCacheConstants.USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME;
import static top.dcenter.ums.security.common.consts.RedisCacheConstants.USER_CONNECTION_HASH_CACHE_NAME;

/**
 * 简单的实现 Redis cache 自定义配置 {@link CacheManager}, 向 IOC 容器中注入 beanName=auth2RedisHashCacheManager 的实例. <br><br>
 * 此 redis Cache 对 {@link org.springframework.data.redis.cache.RedisCache} 进行了修改, 把缓存的 KV 格式该成了 Hash 格式.<br>
 * 1. Cacheable 操作异常处理: <br>
 *     异常处理在日志中打印出错误信息，但是放行，保证redis服务器出现连接等问题的时候不影响程序的正常运行，使得能够出问题时不用缓存. <br>
 * Cacheable 操作自定义异常处理: 实现 {@link CacheErrorHandler } 注入 IOC 容器即可自动注入 {@link CachingConfigurerSupport},
 * 当然也可自定义 {@link CachingConfigurerSupport} .<br>
 * 2. 缓存穿透: 对查询结果 null 值进行缓存, 添加时更新缓存 null 值, 或者 删除此缓存.<br>
 * 3. 取缓存 TTL 的 20% 作为动态的随机变量上下浮动, 防止同时缓存失效而缓存击穿.<br>
 * @author Mark Paluch
 * @author Andy Wilkinson
 * @author YongWu zheng
 * @version V2.0  Created by  2020-06-11 22:57
 */
@Configuration
@ConditionalOnProperty(prefix = "ums.cache.redis", name = "open", havingValue = "true")
@EnableCaching
@Slf4j
public class RedisCacheAutoConfiguration {

    /**
     * redis cache 解析Key：根据分隔符 "__" 来判断是否是 hash 类型
     */
    public static final String REDIS_CACHE_HASH_KEY_SEPARATE = "__";
    public static final String REDIS_CACHE_KEY_SEPARATE = ":";

    private final RedisCacheProperties redisCacheProperties;
    private final RedisProperties properties;
    private final RedisSentinelConfiguration sentinelConfiguration;
    private final RedisClusterConfiguration clusterConfiguration;

    public RedisCacheAutoConfiguration(RedisCacheProperties redisCacheProperties,
                                       RedisProperties properties,
                                       ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
                                       ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {
        this.redisCacheProperties = redisCacheProperties;
        Set<String> cacheNames = redisCacheProperties.getCache().getCacheNames();
        cacheNames.add(USER_CONNECTION_CACHE_NAME);
        cacheNames.add(USER_CONNECTION_HASH_CACHE_NAME);
        cacheNames.add(USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME);
        this.properties = properties;
        this.sentinelConfiguration = sentinelConfigurationProvider.getIfAvailable();
        this.clusterConfiguration = clusterConfigurationProvider.getIfAvailable();
    }

    /**
     * 配置 Jackson2JsonRedisSerializer 序列化器，在配置 redisTemplate需要用来做k,v的
     * 序列化器
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Jackson2JsonRedisSerializer getJackson2JsonRedisSerializer(){
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer;
        jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                                 ObjectMapper.DefaultTyping.NON_FINAL);
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.registerModules(new CoreJackson2Module(), new WebJackson2Module(), new WebServletJackson2Module(),
                           new JavaTimeModule(), new WebServerJackson2Module(),
                           new OAuth2ClientJackson2Module(), new Auth2Jackson2Module());
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }

    @SuppressWarnings({"FieldMayBeFinal", "rawtypes"})
    private static Jackson2JsonRedisSerializer jackson2JsonRedisSerializer =
            getJackson2JsonRedisSerializer();

    /**
     * 缓存管理器, 当 IOC 容器中有 beanName=auth2RedisHashCacheManager 时会替换此实例
     * @return CacheManager
     */
    @Bean("auth2RedisHashCacheManager")
    @ConditionalOnMissingBean(name = "auth2RedisHashCacheManager")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
                                          ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
                                          ClientResources clientResources) {

        RedisCacheProperties.Cache cache = redisCacheProperties.getCache();

        // 判断是否使用 spring IOC 容器中的 LettuceConnectionFactory
        // 如果使用 spring IOC 容器中的 LettuceConnectionFactory，则要注意 cache.database-index 要与 spring.redis.database 一样。
        LettuceConnectionFactory lettuceConnectionFactory;
        if (redisCacheProperties.getUseIocRedisConnectionFactory() && redisConnectionFactory instanceof LettuceConnectionFactory)
        {
            lettuceConnectionFactory = (LettuceConnectionFactory) redisConnectionFactory;
        } else
        {
            // 自定义 redis
            LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(builderCustomizers, clientResources,
                                                                                    getProperties().getLettuce().getPool());
            lettuceConnectionFactory =  createLettuceConnectionFactory(clientConfig);

        }

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        // 设置缓存管理器管理的缓存的默认过期时间
        //noinspection unchecked
        defaultCacheConfig = defaultCacheConfig.entryTtl(cache.getDefaultExpireTime())
                // 设置 key为string序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value为json序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
                // 不缓存空值
                //.disableCachingNullValues()

        Set<String> cacheNames = cache.getCacheNames();

        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>(cacheNames.size());
        for (String cacheName : cacheNames)
        {
            configMap.put(cacheName,
                          defaultCacheConfig.entryTtl(cache.getEntryTtl()));
        }

        //noinspection UnnecessaryLocalVariable
        RedisHashCacheManager cacheManager = RedisHashCacheManager.builder(lettuceConnectionFactory)
                                                                  .cacheDefaults(defaultCacheConfig)
                                                                  .initialCacheNames(cacheNames)
                                                                  .withInitialCacheConfigurations(configMap)
                                                                  .build();
        return cacheManager;
    }

    @Bean("removeConnectionsByConnectionKeyWithUserIdKeyGenerator")
    public RemoveConnectionsByConnectionKeyWithUserIdKeyGenerator removeConnectionsByConnectionKeyWithUserIdKeyGenerator() {
        return new RemoveConnectionsByConnectionKeyWithUserIdKeyGenerator();
    }

    @Bean
    @ConditionalOnMissingBean(type = {"org.springframework.cache.interceptor.CacheErrorHandler"})
    public CacheErrorHandler cacheErrorHandler() {
        /*
         * Cacheable 操作异常处理:
         * 异常处理在日志中打印出错误信息，但是放行，保证redis服务器出现连接等问题的时候不影响程序的正常运行，使得能够出问题时不用缓存
         */
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key) {
                log.error("redis异常：cacheName=[{}], key=[{}]", cache.getName(), key, e);
            }

            @Override
            public void handleCachePutError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key, Object value) {
                log.error("redis异常：cacheName=[{}], key=[{}]", cache.getName(), key, e);
            }

            @Override
            public void handleCacheEvictError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key) {
                log.error("redis异常：cacheName=[{}], key=[{}]", cache.getName(), key, e);
            }

            @Override
            public void handleCacheClearError(@NonNull RuntimeException e, @NonNull Cache cache) {
                log.error("redis异常：cacheName=[{}], ", cache.getName(), e);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(type = {"org.springframework.cache.annotation.CachingConfigurerSupport"})
    public CachingConfigurerSupport cachingConfigurerSupport(CacheErrorHandler cacheErrorHandler) {
        return new CustomizeCachingConfigurerSupport(cacheErrorHandler);
    }

    private static class CustomizeCachingConfigurerSupport extends CachingConfigurerSupport {

        private final CacheErrorHandler cacheErrorHandler;

        public CustomizeCachingConfigurerSupport(CacheErrorHandler cacheErrorHandler) {
            this.cacheErrorHandler = cacheErrorHandler;
        }

        @Override
        public CacheManager cacheManager() {
            return super.cacheManager();
        }

        @Override
        public CacheResolver cacheResolver() {
            return super.cacheResolver();
        }

        @Override
        public KeyGenerator keyGenerator() {
            return super.keyGenerator();
        }

        /**
         * Cacheable 操作异常处理:
         * @return  CacheErrorHandler 自定义 Cacheable 操作异常处理
         */
        @Override
        public CacheErrorHandler errorHandler() {
            return this.cacheErrorHandler;
        }
    }

    private LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration) {
        final RedisSentinelConfiguration sentinelConfig = getSentinelConfig();
        if (sentinelConfig != null) {
            sentinelConfig.setDatabase(redisCacheProperties.getCache().getDatabaseIndex());
            return new LettuceConnectionFactory(sentinelConfig, clientConfiguration);
        }
        if (getClusterConfiguration() != null) {
            return new LettuceConnectionFactory(getClusterConfiguration(), clientConfiguration);
        }
        final RedisStandaloneConfiguration standaloneConfig = getStandaloneConfig();
        standaloneConfig.setDatabase(redisCacheProperties.getCache().getDatabaseIndex());
        return new LettuceConnectionFactory(standaloneConfig, clientConfiguration);
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
            ClientResources clientResources, RedisProperties.Pool pool) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = createBuilder(pool);
        applyProperties(builder);
        if (StringUtils.hasText(getProperties().getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builder.clientOptions(initializeClientOptionsBuilder().timeoutOptions(TimeoutOptions.enabled()).build());
        builder.clientResources(clientResources);
        builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return new PoolBuilderFactory().createBuilder(pool);
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder applyProperties(
            LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        if (getProperties().isSsl()) {
            builder.useSsl();
        }
        if (getProperties().getTimeout() != null) {
            builder.commandTimeout(getProperties().getTimeout());
        }
        if (getProperties().getLettuce() != null) {
            RedisProperties.Lettuce lettuce = getProperties().getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(getProperties().getLettuce().getShutdownTimeout());
            }
        }
        if (StringUtils.hasText(getProperties().getClientName())) {
            builder.clientName(getProperties().getClientName());
        }
        return builder;
    }

    private ClientOptions.Builder initializeClientOptionsBuilder() {
        if (getProperties().getCluster() != null) {
            ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
            RedisProperties.Lettuce.Cluster.Refresh refreshProperties = getProperties().getLettuce().getCluster().getRefresh();
            ClusterTopologyRefreshOptions.Builder refreshBuilder = ClusterTopologyRefreshOptions.builder();
            if (refreshProperties.getPeriod() != null) {
                refreshBuilder.enablePeriodicRefresh(refreshProperties.getPeriod());
            }
            if (refreshProperties.isAdaptive()) {
                refreshBuilder.enableAllAdaptiveRefreshTriggers();
            }
            return builder.topologyRefreshOptions(refreshBuilder.build());
        }
        return ClientOptions.builder();
    }

    private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        ConnectionInfo connectionInfo = parseUrl(getProperties().getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }

    /**
     * Inner class to allow optional commons-pool2 dependency.
     */
    private static class PoolBuilderFactory {

        LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool properties) {
            return LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(properties));
        }

        private GenericObjectPoolConfig<?> getPoolConfig(RedisProperties.Pool properties) {
            GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
            config.setMaxTotal(properties.getMaxActive());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMinIdle(properties.getMinIdle());
            if (properties.getTimeBetweenEvictionRuns() != null) {
                config.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRuns().toMillis());
            }
            if (properties.getMaxWait() != null) {
                config.setMaxWaitMillis(properties.getMaxWait().toMillis());
            }
            return config;
        }

    }

    protected final RedisStandaloneConfiguration getStandaloneConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(this.properties.getUrl())) {
            ConnectionInfo connectionInfo = parseUrl(this.properties.getUrl());
            config.setHostName(connectionInfo.getHostName());
            config.setPort(connectionInfo.getPort());
            config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
        }
        else {
            config.setHostName(this.properties.getHost());
            config.setPort(this.properties.getPort());
            config.setPassword(RedisPassword.of(this.properties.getPassword()));
        }
        config.setDatabase(this.properties.getDatabase());
        return config;
    }

    protected final RedisSentinelConfiguration getSentinelConfig() {
        if (this.sentinelConfiguration != null) {
            return this.sentinelConfiguration;
        }
        RedisProperties.Sentinel sentinelProperties = this.properties.getSentinel();
        if (sentinelProperties != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(sentinelProperties.getMaster());
            config.setSentinels(createSentinels(sentinelProperties));
            if (this.properties.getPassword() != null) {
                config.setPassword(RedisPassword.of(this.properties.getPassword()));
            }
            if (sentinelProperties.getPassword() != null) {
                config.setSentinelPassword(RedisPassword.of(sentinelProperties.getPassword()));
            }
            config.setDatabase(this.properties.getDatabase());
            return config;
        }
        return null;
    }

    /**
     * Create a {@link RedisClusterConfiguration} if necessary.
     * @return {@literal null} if no cluster settings are set.
     */
    protected final RedisClusterConfiguration getClusterConfiguration() {
        if (this.clusterConfiguration != null) {
            return this.clusterConfiguration;
        }
        if (this.properties.getCluster() == null) {
            return null;
        }
        RedisProperties.Cluster clusterProperties = this.properties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
        if (clusterProperties.getMaxRedirects() != null) {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        if (this.properties.getPassword() != null) {
            config.setPassword(RedisPassword.of(this.properties.getPassword()));
        }
        return config;
    }

    protected final RedisProperties getProperties() {
        return this.properties;
    }

    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<>();
        for (String node : sentinel.getNodes()) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            }
            catch (RuntimeException ex) {
                throw new IllegalStateException("Invalid redis sentinel property '" + node + "'", ex);
            }
        }
        return nodes;
    }

    protected ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
                throw new RedisUrlSyntaxException(url);
            }
            boolean useSsl = ("rediss".equals(scheme));
            String password = null;
            if (uri.getUserInfo() != null) {
                password = uri.getUserInfo();
                int index = password.indexOf(':');
                if (index >= 0) {
                    password = password.substring(index + 1);
                }
            }
            return new ConnectionInfo(uri, useSsl, password);
        }
        catch (URISyntaxException ex) {
            throw new RedisUrlSyntaxException(url, ex);
        }
    }

    static class ConnectionInfo {

        private final URI uri;

        private final boolean useSsl;

        private final String password;

        ConnectionInfo(URI uri, boolean useSsl, String password) {
            this.uri = uri;
            this.useSsl = useSsl;
            this.password = password;
        }

        boolean isUseSsl() {
            return this.useSsl;
        }

        String getHostName() {
            return this.uri.getHost();
        }

        int getPort() {
            return this.uri.getPort();
        }

        String getPassword() {
            return this.password;
        }

    }

    /**
     * Exception thrown when a Redis URL is malformed or invalid.
     *
     * @author Scott Frederick
     */
    static class RedisUrlSyntaxException extends RuntimeException {

        private static final long serialVersionUID = -525702723177852412L;
        private final String url;

        RedisUrlSyntaxException(String url, Exception cause) {
            super(buildMessage(url), cause);
            this.url = url;
        }

        RedisUrlSyntaxException(String url) {
            super(buildMessage(url));
            this.url = url;
        }

        String getUrl() {
            return this.url;
        }

        private static String buildMessage(String url) {
            return "Invalid Redis URL '" + url + "'";
        }

    }
}