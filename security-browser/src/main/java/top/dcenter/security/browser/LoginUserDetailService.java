package top.dcenter.security.browser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/3 14:15
 */
@Slf4j
@Component("userDetailService")
public class LoginUserDetailService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    public LoginUserDetailService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // 根据用户名获取用户信息 TODO 生成中这里从数据库获取用户信息，这里做一个通用的扩展接口
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
}
