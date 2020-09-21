package top.dcenter.ums.security.social.api.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.security.SocialUserDetails;

/**
 * 从缓存中查询用户信息, 把用户信息存入缓存, 移除缓存.
 * 只是对 {@link UserCache} 的 copy, 防止 IOC 容器中的其他 UserCache 注入本应用
 * 用法可以参考 demo 模块的 <pre> demo.security.service.LoginSocialUserDetailsService</pre>
 * @see UserCache
 * @author zyw
 * @version V1.0
 * Created by 2020/5/31 15:30
 */
@SuppressWarnings("JavadocReference")
public interface SocialUserCache extends UserCache {

    /**
     * 只是对 {@link #getUserFromCache(String)} 返回结果转换为 {@link SocialUserDetails}
     * Obtains a {@link UserDetails} from the cache.
     *
     * @param userId the {@link User#getUsername()} used to place the user in the cache
     *
     * @return the populated <codes>UserDetails</codes> or <codes>null</codes> if the user
     * could not be found or if the cache entry has expired
     */
    SocialUserDetails getSocialUserFromCache(String userId);
}
