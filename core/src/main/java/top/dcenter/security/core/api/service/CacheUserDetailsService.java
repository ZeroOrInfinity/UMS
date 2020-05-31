package top.dcenter.security.core.api.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 从缓存中查询用户信息, 把用户信息存入缓存
 * @author zyw23
 * @version V1.0
 * Created by 2020/5/31 15:30
 */
public interface CacheUserDetailsService{

    /**
     * 从缓存中查询用户信息
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(String)
     * @param principal the principal identifying the user whose data is required.
     *
     * @return a fully populated user record (never <code>null</code>)
     *
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     * GrantedAuthority
     */
    UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException;

    /**
     * 保存用户信息到缓存
     * @param principal  the principal identifying the user whose data is required.
     * @param user  用户信息
     * @return  返回是否保存成功
     */
    boolean saveUserInCache(String principal, UserDetails user);
}
