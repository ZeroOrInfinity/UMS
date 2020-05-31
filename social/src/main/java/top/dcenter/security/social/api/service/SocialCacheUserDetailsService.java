package top.dcenter.security.social.api.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import top.dcenter.security.core.api.service.CacheUserDetailsService;

/**
 * 从缓存中查询用户信息, 把用户信息存入缓存. <br>
 *     用法可以参考 {@link top.dcenter.security.service.LoginSocialUserDetailService}.
 * @author zyw23
 * @version V1.0
 * Created by 2020/5/31 15:37
 */
@SuppressWarnings("JavadocReference")
public interface SocialCacheUserDetailsService extends CacheUserDetailsService {
    /**
     * 从缓存中查询用户信息
     * @see UserDetailsService#loadUserByUsername(String)
     * @param userId the user ID used to lookup the user details
     * @return the SocialUserDetails requested
     */
    SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException;
}
