package top.dcenter.ums.security.core.api.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import top.dcenter.ums.security.core.oauth2.details.Auth2UserDetails;

import java.util.List;
import java.util.UUID;

/**
 * 用户名密码、手机短信登录、第三方授权登录及自动注册、用户注册服务：<br><br>
 *     1. 用于用户名密码登录与手机短信登录逻辑。<br><br>
 *     2. 用于用户名密码注册与手机短信注册逻辑。<br><br>
 *     3. 用于第三方登录注册逻辑。<br><br>
 * @author zyw
 * @version V1.0  Created by 2020/5/16 11:48
 */
public interface UmsUserDetailsService extends UserDetailsService, UserDetailsRegisterService {

    /**
     * 用于第三方登录时查询服务, userId 为本地账户的 userId
     * @see UserDetailsService#loadUserByUsername(String)
     * @param userId    userId 为本地账户的 userId
     * @return the Auth2UserDetails requested
     * @throws UsernameNotFoundException    没有此 userId 的用户
     */
    Auth2UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException;

    /**
     * 在本地账户中检查是否存在 userIds, userIds 为本地账户的 userIds
     * @param userIds   userId 为本地账户的 userId
     * @return userIds 是否存在的列表(true 表示存在), 与传入的 userIds 顺序一一对应
     * @throws UsernameNotFoundException    没有此 userId 的用户
     */
    List<Boolean> existedByUserIds(String... userIds) throws UsernameNotFoundException;

    /**
     * 生成一个用户 id. 预留接口, 默认生成一个不带 "-" 的 UUID
     * @return  返回一个用户 Id
     */
    default String generateUserId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


}
