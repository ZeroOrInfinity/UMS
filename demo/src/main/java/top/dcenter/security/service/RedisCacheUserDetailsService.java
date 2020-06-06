package top.dcenter.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.api.service.CacheUserDetailsService;

/**
 * 从缓存中查询用户信息(包含第三方登录用户信息), 包用户信息保持到缓存中
 * @author zyw
 * @version V1.0  Created by 2020/5/31 15:40
 */
@Component
@Slf4j
public class RedisCacheUserDetailsService implements CacheUserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public RedisCacheUserDetailsService(PasswordEncoder passwordEncoder, ObjectMapper objectMapper) {
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public UserDetails getUserFromCache(String username) throws UsernameNotFoundException {
        log.info("Demo ======>: 从 cache 获取到用户信息, 用户名：{}, 登录成功", username);
        return new User(username,
                        passwordEncoder.encode("admin"),
                        true,
                        true,
                        true,
                        true,
                        AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }


    @Override
    public SocialUserDetails getSocialUserFromCache(String userId) {
        UserDetails userFromCache = getUserFromCache(userId);
        log.info("Demo ======>: 从 cache 获取到用户信息, 用户名：{}, 登录成功", userId);
        return new SocialUser(userFromCache.getUsername(),
                              userFromCache.getPassword(),
                              userFromCache.isEnabled(),
                              userFromCache.isAccountNonExpired(),
                              userFromCache.isCredentialsNonExpired(),
                              userFromCache.isAccountNonLocked(),
                              userFromCache.getAuthorities());
    }

    @Override
    public void putUserInCache(UserDetails user) {

        try
        {
            String userAsString = this.objectMapper.writeValueAsString(user);
            log.info("Demo ======>: 用户名：{}, 缓存到 cache 成功: {}", user.getUsername(), userAsString);

        }
        catch (JsonProcessingException e)
        {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void removeUserFromCache(String username) {
        // do removeUserFromCache(String username);
        log.info("Demo ======>: 用户名：{}, 从 cache 中移除用户信息成功", username);
    }

}
