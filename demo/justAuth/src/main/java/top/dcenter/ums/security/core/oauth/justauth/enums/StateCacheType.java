package top.dcenter.ums.security.core.oauth.justauth.enums;

/**
 * state cache type
 * @author zyw
 * @version V1.0  Created by 2020/10/6 20:09
 */
public enum StateCacheType {
    /**
     * 本地内存, 适用单机
     */
    DEFAULT,
    /**
     * session, 根据 session 的缓存模式是否适用分布式来决定是否适用单机与分布式
     */
    SESSION,
    /**
     * redis, 适用单机与分布式
     */
    REDIS

}
