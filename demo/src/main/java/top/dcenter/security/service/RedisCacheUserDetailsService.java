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
import top.dcenter.security.social.api.service.SocialCacheUserDetailsService;

/**
 * 从缓存中查询用户信息(包含第三方登录用户信息), 包用户信息保持到缓存中
 * @author zyw
 * @version V1.0  Created by 2020/5/31 15:40
 */
@Component
@Slf4j
public class RedisCacheUserDetailsService implements SocialCacheUserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public RedisCacheUserDetailsService(PasswordEncoder passwordEncoder, ObjectMapper objectMapper) {
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        log.info("Demo ======>: cache 中登录, 用户名：{}, 登录成功", userId);
        return new SocialUser(userId,
                                   passwordEncoder.encode("admin"),
                                   true,
                                   true,
                                   true,
                                   true,
                                   AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Demo ======>: cache 中登录, 用户名：{}, 登录成功", username);
        return new User(username,
                        passwordEncoder.encode("admin"),
                        true,
                        true,
                        true,
                        true,
                        AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }

    @Override
    public boolean saveUserInCache(String principal, UserDetails user) {

        try
        {
            String userAsString = this.objectMapper.writeValueAsString(user);
            log.info("Demo ======>: 用户名：{}, 缓存成功: {}", principal, userAsString);

        }
        catch (JsonProcessingException e)
        {
            log.error(e.getMessage(), e);
        }

        return true;
    }

}
