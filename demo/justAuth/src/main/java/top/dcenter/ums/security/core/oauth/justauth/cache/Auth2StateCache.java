package top.dcenter.ums.security.core.oauth.justauth.cache;

import me.zhyd.oauth.cache.AuthStateCache;
import top.dcenter.ums.security.core.oauth.justauth.enums.CacheKeyStrategy;

/**
 * {@link AuthStateCache} 的扩展, 添加自定义 Cache key 的方法
 * @author zyw
 * @version V1.0  Created by 2020/10/6 19:42
 */
public interface Auth2StateCache extends AuthStateCache {
    /**
     * 获取 缓存 key 的策略
     * @return CacheKeyStrategy
     */
    CacheKeyStrategy getCacheKeyStrategy();
}
