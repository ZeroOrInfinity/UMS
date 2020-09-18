package top.dcenter.ums.security.social.repository.jdbc.cache;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.support.NullValue;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.concurrent.Callable;

import static top.dcenter.ums.security.social.config.RedisCacheAutoConfig.REDIS_CACHE_HASH_KEY_SEPARATE;

/**
 * 对 {@link org.springframework.data.redis.cache.RedisCache} 进行了扩展, 添加了对 Hash类型的缓存的支持
 * @author zyw
 * @version V1.0  Created by 2020/6/13 14:15
 *
 * {@link org.springframework.cache.Cache} implementation using for Redis as underlying store.
 * <p/>
 * Use {@link RedisCacheManager} to create {@link RedisHashCache} instances.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @see RedisCacheConfiguration
 * @see RedisHashCacheWriter
 * @since 2.0
 */
public class RedisHashCache extends RedisCache {

    private static final byte[] BINARY_NULL_VALUE = RedisSerializer.java().serialize(NullValue.INSTANCE);

    private static final int HASH_KEY_ARRAY_LENGTH = 2;

    private final String name;
    private final RedisHashCacheWriter cacheWriter;
    private final RedisCacheConfiguration cacheConfig;
    private final ConversionService conversionService;

    /**
     * Create new {@link RedisHashCache}.
     *
     * @param name        must not be {@literal null}.
     * @param cacheWriter must not be {@literal null}.
     * @param cacheConfig must not be {@literal null}.
     */
    protected RedisHashCache(String name, RedisHashCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {

        super(name, cacheWriter, cacheConfig);

        Assert.notNull(name, "Name must not be null!");
        Assert.notNull(cacheWriter, "CacheWriter must not be null!");
        Assert.notNull(cacheConfig, "CacheConfig must not be null!");

        this.name = name;
        this.cacheWriter = cacheWriter;
        this.cacheConfig = cacheConfig;
        this.conversionService = cacheConfig.getConversionService();
    }

    @Override
    protected Object lookup(Object key) {
        Object[] keyPair = parsingKey(key);
        byte[] value;
        if (keyPair.length == HASH_KEY_ARRAY_LENGTH)
        {
            value = cacheWriter.hGet(name, createAndConvertCacheKey(keyPair[0]),
                                     createAndConvertCacheField(keyPair[1]));
        }
        else
        {
            value = cacheWriter.get(name, createAndConvertCacheKey(key));
        }

        if (value == null)
        {
            return null;
        }

        return deserializeCacheValue(value);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RedisCacheWriter getNativeCache() {
        return this.cacheWriter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <T> T get(Object key, Callable<T> valueLoader) {

        ValueWrapper result = get(key);

        if (result != null)
        {
            return (T) result.get();
        }

        T value = valueFromLoader(key, valueLoader);
        put(key, value);
        return value;
    }

    @Override
    public void put(Object key, @Nullable Object value) {

        Object cacheValue = preProcessCacheValue(value);

        if (!isAllowNullValues() && cacheValue == null)
        {

            throw new IllegalArgumentException(String.format(
                    "Cache '%s' does not allow 'null' values. Avoid storing null via '@Cacheable(unless=\"#result == null\")' or configure RedisCache to allow 'null' via RedisCacheConfiguration.",
                    name));
        }

        Object[] keyPair = parsingKey(key);
        if (keyPair.length == HASH_KEY_ARRAY_LENGTH)
        {
            cacheWriter.hPut(name,
                             createAndConvertCacheKey(keyPair[0]),
                             createAndConvertCacheField(keyPair[1]),
                             serializeCacheValue(cacheValue),
                             cacheConfig.getTtl());
        }
        else
        {
            cacheWriter.put(name, createAndConvertCacheKey(key), serializeCacheValue(cacheValue), cacheConfig.getTtl());
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {

        Object cacheValue = preProcessCacheValue(value);

        if (!isAllowNullValues() && cacheValue == null)
        {
            return get(key);
        }

        Object[] keyPair = parsingKey(key);
        byte[] result;
        if (keyPair.length == HASH_KEY_ARRAY_LENGTH)
        {
            result = cacheWriter.hPutIfAbsent(name,
                             createAndConvertCacheKey(keyPair[0]),
                             createAndConvertCacheField(keyPair[1]),
                             serializeCacheValue(cacheValue),
                             cacheConfig.getTtl());
        }
        else
        {
            result = cacheWriter.putIfAbsent(name, createAndConvertCacheKey(key), serializeCacheValue(cacheValue),
                                                    cacheConfig.getTtl());
        }

        if (result == null)
        {
            return null;
        }

        return new SimpleValueWrapper(fromStoreValue(deserializeCacheValue(result)));
    }

    @Override
    public void evict(Object key) {
        Object[] keyPair = parsingKey(key);
        if (keyPair.length == HASH_KEY_ARRAY_LENGTH)
        {
            cacheWriter.hRemove(name, createAndConvertCacheKey(keyPair[0]),createAndConvertCacheField(keyPair[1]));
        }
        cacheWriter.remove(name, createAndConvertCacheKey(key));
    }

    @Override
    public void clear() {
        byte[] pattern = conversionService.convert(createCacheKey("*"), byte[].class);
        cacheWriter.clean(name, pattern);
    }

    /**
     * Get {@link RedisCacheConfiguration} used.
     *
     * @return immutable {@link RedisCacheConfiguration}. Never {@literal null}.
     */
    @Override
    public RedisCacheConfiguration getCacheConfiguration() {
        return cacheConfig;
    }

    /**
     * Customization hook called before passing object to
     * {@link org.springframework.data.redis.serializer.RedisSerializer}.
     *
     * @param value can be {@literal null}.
     * @return preprocessed value. Can be {@literal null}.
     */
    @Override
    @Nullable
    protected Object preProcessCacheValue(@Nullable Object value) {

        if (value != null)
        {
            return value;
        }

        return isAllowNullValues() ? NullValue.INSTANCE : null;
    }

    /**
     * Serialize the key.
     *
     * @param cacheKey must not be {@literal null}.
     * @return never {@literal null}.
     */
    @Override
    @NotNull
    protected byte[] serializeCacheKey(String cacheKey) {
        return ByteUtils.getBytes(cacheConfig.getKeySerializationPair().write(cacheKey));
    }

    /**
     * Serialize the value to cache.
     *
     * @param value must not be {@literal null}.
     * @return never {@literal null}.
     */
    @Override
    @NotNull
    protected byte[] serializeCacheValue(Object value) {

        if (isAllowNullValues() && value instanceof NullValue)
        {
            return BINARY_NULL_VALUE;
        }

        return ByteUtils.getBytes(cacheConfig.getValueSerializationPair().write(value));
    }

    /**
     * Deserialize the given value to the actual cache value.
     *
     * @param value must not be {@literal null}.
     * @return can be {@literal null}.
     */
    @Override
    @Nullable
    protected Object deserializeCacheValue(byte[] value) {

        if (isAllowNullValues() && ObjectUtils.nullSafeEquals(value, BINARY_NULL_VALUE))
        {
            return NullValue.INSTANCE;
        }

        return cacheConfig.getValueSerializationPair().read(ByteBuffer.wrap(value));
    }

    /**
     * Customization hook for creating cache key before it gets serialized.
     *
     * @param key will never be {@literal null}.
     * @return never {@literal null}.
     */
    @Override
    protected String createCacheKey(Object key) {

        String convertedKey = convertKey(key);

        if (!cacheConfig.usePrefix())
        {
            return convertedKey;
        }

        return prefixCacheKey(convertedKey);
    }

    /**
     * Convert {@code key} to a {@link String} representation used for cache key creation.
     *
     * @param key will never be {@literal null}.
     * @return never {@literal null}.
     * @throws IllegalStateException if {@code key} cannot be converted to {@link String}.
     */
    @Override
    protected String convertKey(Object key) {

        if (key instanceof String)
        {
            return (String) key;
        }

        TypeDescriptor source = TypeDescriptor.forObject(key);

        if (conversionService.canConvert(source, TypeDescriptor.valueOf(String.class)))
        {
            try
            {
                return conversionService.convert(key, String.class);
            }
            catch (ConversionFailedException e)
            {

                // may fail if the given key is a collection
                if (isCollectionLikeOrMap(source))
                {
                    return convertCollectionLikeOrMapKey(key, source);
                }

                throw e;
            }
        }

        Method toString = ReflectionUtils.findMethod(key.getClass(), "toString");

        if (toString != null && !Object.class.equals(toString.getDeclaringClass()))
        {
            return key.toString();
        }

        throw new IllegalStateException(String.format(
                "Cannot convert cache key %s to String. Please register a suitable Converter via 'RedisCacheConfiguration.configureKeyConverters(...)' or override '%s.toString()'.",
                source, key.getClass().getSimpleName()));
    }

    private Object[] parsingKey(Object key) {
        if (key instanceof String)
        {
            String[] keyPair = ((String) key).split(REDIS_CACHE_HASH_KEY_SEPARATE);
            //noinspection AlibabaUndefineMagicConstant
            return keyPair;
        }
        return new Object[]{key};
    }

    private String convertCollectionLikeOrMapKey(Object key, TypeDescriptor source) {

        if (source.isMap())
        {

            StringBuilder target = new StringBuilder("{");

            for (Entry<?, ?> entry : ((Map<?, ?>) key).entrySet())
            {
                target.append(convertKey(entry.getKey())).append("=").append(convertKey(entry.getValue()));
            }
            target.append("}");

            return target.toString();
        }
        else if (source.isCollection() || source.isArray())
        {

            StringJoiner sj = new StringJoiner(",");

            Collection<?> collection = source.isCollection() ? (Collection<?>) key
                    : Arrays.asList(ObjectUtils.toObjectArray(key));

            for (Object val : collection)
            {
                sj.add(convertKey(val));
            }
            return "[" + sj.toString() + "]";
        }

        throw new IllegalArgumentException(String.format("Cannot convert cache key %s to String.", key));
    }

    private boolean isCollectionLikeOrMap(TypeDescriptor source) {
        return source.isArray() || source.isCollection() || source.isMap();
    }

    private byte[] createAndConvertCacheKey(Object key) {
        return serializeCacheKey(createCacheKey(key));
    }

    private byte[] createAndConvertCacheField(Object field) {
        return serializeCacheKey(convertKey(field));
    }

    private String prefixCacheKey(String key) {

        // allow contextual cache names by computing the key prefix on every call.
        return cacheConfig.getKeyPrefixFor(name) + key;
    }

    private static <T> T valueFromLoader(Object key, Callable<T> valueLoader) {

        try
        {
            return valueLoader.call();
        }
        catch (Exception e)
        {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }
}