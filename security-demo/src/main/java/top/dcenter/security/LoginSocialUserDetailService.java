package top.dcenter.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.excception.RegisterUserFailureException;
import top.dcenter.security.social.AbstractSocialUserDetailService;

/**
 * 用户密码与手机短信登录与 OAuth 登录与注册服务：<br>
 *     1. 用于第三方登录与手机短信登录逻辑。<br>
 *     2. 用于用户密码登录逻辑。<br>
 *     3. 用于 OAuth 用户注册逻辑。<br>
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 14:15
 */
@Slf4j
@Component("userDetailService")
public class LoginSocialUserDetailService extends AbstractSocialUserDetailService {

    private final JdbcTemplate jdbcTemplate;
    public LoginSocialUserDetailService(ApplicationContext applicationContext, JdbcTemplate jdbcTemplate) {
        super(applicationContext);
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("AlibabaUndefineMagicConstant")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // 根据用户名获取用户信息

            // 获取用户信息逻辑。。。
            // ...

            if ("要懂的舍得".equals(username) || "要懂得舍得".equals(username) || "admin".equals(username) || "13345678980".equals(username))
            {
                String encodedPassword = passwordEncoder.encode("admin");
                passwordEncoder.matches("admin", encodedPassword);
                log.info("Demo ======>: 登录用户名：{}, 登录成功", username);
                return new User(username,
                                encodedPassword,
                                true,
                                true,
                                true,
                                true ,
                                AuthorityUtils.commaSeparatedStringToAuthorityList("admin")
                );
            }
            log.info("Demo ======>: 登录用户名：{}, 登录失败", username);
            return null;
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("用户不存在", e);
        }
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        try {
            // 根据用户名获取用户信息。

            // 获取用户信息逻辑。。。
            // ...

            if ("要懂的舍得".equals(userId) || "要懂得舍得".equals(userId) || "zyw".equals(userId))
            {
                log.info("Demo ======>: 登录用户名：{}, 登录成功", userId);
                return new SocialUser(userId,
                                      passwordEncoder.encode("admin"),
                                      true,
                                      true,
                                      true,
                                      true,
                                      AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));

            }
            log.info("Demo ======>: 登录用户名：{}, 登录失败", userId);
            return null;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new UsernameNotFoundException("不能获取到用户信息，请重试", e);
        }
    }

    @Override
    public UserDetails registerUser(String mobile) throws RegisterUserFailureException {
        String username = null;
        try {
            // TODO 从 request中先获取注册类型，再根据不同类型进行注册逻辑

            if (mobile == null) {
                throw new RegisterUserFailureException("手机号不能为空");
            }

            // 用户信息持久化逻辑。。。
            // ...

            log.info("Demo ======>: 手机短信登录用户 {}：注册成功", mobile);
            return new User(mobile,
                            passwordEncoder.encode("admin"),
                            true,
                            true,
                            true,
                            true ,
                            AuthorityUtils.commaSeparatedStringToAuthorityList("admin")
            );
        }
        catch (Exception e) {
            if (e instanceof AuthenticationException)
            {
                throw new RegisterUserFailureException(e.getMessage(), e);
            }
            throw new RegisterUserFailureException(String.format("%s 注册失败", username),  e);
        }
    }

    @Override
    public UserDetails registerUser(ServletWebRequest request) throws RegisterUserFailureException{
        String username = null;
        try {
            // TODO 从 request中先获取注册类型，再根据不同类型进行注册逻辑
            username = request.getParameter("username");
            String password = request.getParameter("password");
            if (username == null) {
                throw new RegisterUserFailureException("用户名不能为空");
            }

            if (password == null) {
                throw new RegisterUserFailureException("密码不能为空");
            }

            // 用户信息持久化逻辑。。。
            // ...

            String encodedPassword = passwordEncoder.encode(password);

            log.info("Demo ======>: 用户名：{}, 注册成功", username);
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
            if (e instanceof AuthenticationException)
            {
                throw new RegisterUserFailureException(e.getMessage(), e);
            }
            throw new RegisterUserFailureException(String.format("%s 注册失败", username),  e);
        }
    }

    @Override
    public UserDetails registerUser(ServletWebRequest request, ProviderSignInUtils providerSignInUtils) throws RegisterUserFailureException {
        String username = null;
        try {
            // TODO 从 request中先获取注册类型，再根据不同类型进行注册逻辑
            username = request.getParameter("username");
            String password = request.getParameter("password");
            Connection<?> connectionFromSession = providerSignInUtils.getConnectionFromSession(request);
            log.info("Demo ======>: connectionFromSession = {}", connectionFromSession);
            if (username == null) {
                throw new RegisterUserFailureException("用户名不能为空");
            }

            if (password == null) {
                throw new RegisterUserFailureException("密码不能为空");
            }

            // 用户信息持久化逻辑。。。
            // ...

            String encodedPassword = passwordEncoder.encode(password);
            // OAuth 信息存储
            providerSignInUtils.doPostSignUp(username, request);
            log.info("Demo ======>: 第三方登录用户：{}, 注册成功", username);
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
            if (e instanceof AuthenticationException)
            {
                throw new RegisterUserFailureException(e.getMessage(), e);
            }
            throw new RegisterUserFailureException(String.format("%s 注册失败", username),  e);
        }
    }

}
