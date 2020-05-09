package top.dcenter.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/3 14:15
 */
@Slf4j
@Component("userDetailService")
public class LoginUserDetailService implements UserDetailsService, SocialUserDetailsService {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // 根据用户名获取用户信息 TODO 生成中这里从数据库获取用户信息，这这里可以扩展 UserDetails
            String encodedPassword = passwordEncoder.encode("admin");
            boolean isSuccess = passwordEncoder.matches("admin", encodedPassword);
            log.info("登录用户名：{}, 登录是否成功：{}", username, isSuccess);
            return new User(username,
                            encodedPassword,
                            true,
                            true,
                            true,
                            true ,
                            AuthorityUtils.commaSeparatedStringToAuthorityList("admin")
            );
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("用户不存在", e);
        }
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        try {
            // 根据用户名获取用户信息 TODO 生成中这里从数据库获取用户信息，这里可以扩展 UserDetails
            String encodedPassword = passwordEncoder.encode("admin");
            boolean isSuccess = passwordEncoder.matches("admin", encodedPassword);
            log.info("登录用户名：{}, 登录是否成功：{}", userId, isSuccess);
            return new SocialUser(userId,
                                  encodedPassword,
                                  true,
                                  true,
                                  true,
                                  true,
                                  AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new UsernameNotFoundException("不能获取到用户信息，请重试", e);
        }
    }
}
