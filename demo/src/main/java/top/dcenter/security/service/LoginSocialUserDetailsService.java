package top.dcenter.security.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
import top.dcenter.test.entity.UserInfo;
import top.dcenter.security.core.api.service.CacheUserDetailsService;
import top.dcenter.security.core.enums.ErrorCodeEnum;
import top.dcenter.security.core.exception.RegisterUserFailureException;
import top.dcenter.security.core.exception.UserNotExistException;
import top.dcenter.security.core.util.RequestUtil;
import top.dcenter.security.social.api.service.AbstractSocialUserDetailsService;
import top.dcenter.security.social.properties.SocialProperties;

import java.util.List;

/**
 * 用户密码与手机短信登录与 OAuth 登录与注册服务：<br><br>
 * 1. 用于第三方登录与手机短信登录逻辑。<br><br>
 * 2. 用于用户密码登录逻辑。<br><br>
 * 3. 用于 OAuth 用户注册逻辑。<br><br>
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 14:15
 * @author zyw
 */
@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
@Slf4j
@Component
@EnableWebSecurity
public class LoginSocialUserDetailsService extends AbstractSocialUserDetailsService {

    /**
     * 用户名
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * 密码
     */
    public static final String PARAM_PASSWORD = "password";

    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private CacheUserDetailsService cacheUserDetailsService;

    private SocialProperties socialProperties;

    public LoginSocialUserDetailsService(ApplicationContext applicationContext, JdbcTemplate jdbcTemplate, SocialProperties socialProperties) {
        super(applicationContext);
        this.jdbcTemplate = jdbcTemplate;
        this.socialProperties = socialProperties;
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SuppressWarnings("AlibabaUndefineMagicConstant")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try
        {
            // 从缓存中查询用户信息:
            // 从缓存中查询用户信息
            if (this.cacheUserDetailsService != null)
            {
                UserDetails userDetails = this.cacheUserDetailsService.getUserFromCache(username);
                if (userDetails != null)
                {
                    return userDetails;
                }
            }

            // 根据用户名获取用户信息

            // 获取用户信息逻辑。。。
            // ...

            // 示例：只是从用户登录日志表中提取的信息，
            List<String> list = jdbcTemplate.queryForList("select username from persistent_logins where username = ?", String.class, username);
            if (list.contains(username))
            {
                for (String name : list)
                {
                    if (name.equals(username))
                    {
                        log.info("Demo ======>: 登录用户名：{}, 登录成功", username);
                        return new User(username,
                                        passwordEncoder.encode("admin"),
                                        true,
                                        true,
                                        true,
                                        true,
                                        AuthorityUtils.commaSeparatedStringToAuthorityList("admin, ROLE_USER"));

                    }
                }
            }
            log.warn("Demo ======>: 登录用户名：{}, 登录失败", username);
            return null;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new UserNotExistException(ErrorCodeEnum.QUERY_USER_INFO_ERROR, e, username);
        }
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        try
        {
            // 从缓存中查询用户信息
            if (this.cacheUserDetailsService != null)
            {
                SocialUserDetails userDetails = this.cacheUserDetailsService.getSocialUserFromCache(userId);
                if (userDetails != null)
                {
                    return null;
                }
            }

            // 根据用户名获取用户信息。

            // 获取用户信息逻辑。。。
            // ...

            // 示例：只是从 OAuth2 用户登录日志表中提取的信息，
            List<String> list = jdbcTemplate.queryForList("select userId from social_UserConnection " +
                                                                  "where userId = ?",
                                                          String.class, userId);
            if (list.contains(userId))
            {
                for (String username : list)
                {
                    if (username.equals(userId))
                    {
                        log.info("Demo ======>: 登录用户名：{}, 登录成功", userId);
                        return new SocialUser(username,
                                              "",
                                              true,
                                              true,
                                              true,
                                              true,
                                              AuthorityUtils.commaSeparatedStringToAuthorityList("admin, ROLE_USER"));

                    }

                }

            }
            log.info("Demo ======>: 登录用户名：{}, 登录失败", userId);
            return null;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new UserNotExistException(ErrorCodeEnum.QUERY_USER_INFO_ERROR, e, userId);
        }
    }

    @Override
    public UserDetails registerUser(String mobile) throws RegisterUserFailureException {

        if (mobile == null)
        {
            throw new RegisterUserFailureException(ErrorCodeEnum.MOBILE_NOT_EMPTY, null);
        }

        // 用户信息持久化逻辑。。。
        // ...

        log.info("Demo ======>: 手机短信登录用户 {}：注册成功", mobile);
        User user = new User(mobile,
                             passwordEncoder.encode("admin"),
                             true,
                             true,
                             true,
                             true,
                             AuthorityUtils.commaSeparatedStringToAuthorityList("admin, ROLE_USER")
        );

        // 把用户信息存入缓存
        cacheUserDetailsService.putUserInCache(user);

        return user;
    }

    @Override
    public UserDetails registerUser(ServletWebRequest request) throws RegisterUserFailureException {

        String username = getValueOfRequest(request, PARAM_USERNAME, ErrorCodeEnum.USERNAME_NOT_EMPTY);
        String password = getValueOfRequest(request, PARAM_PASSWORD, ErrorCodeEnum.PASSWORD_NOT_EMPTY);
        // ...

        // UserInfo userInfo = getUserInfo(request)

        // 用户信息持久化逻辑。。。
        // ...

        String encodedPassword = passwordEncoder.encode(password);

        log.info("Demo ======>: 用户名：{}, 注册成功", username);
        User user = new User(username,
                              encodedPassword,
                              true,
                              true,
                              true,
                              true,
                              AuthorityUtils.commaSeparatedStringToAuthorityList("admin, ROLE_USER")
        );

        // 把用户信息存入缓存
        cacheUserDetailsService.putUserInCache(user);

        return user;

    }

    @Override
    public UserDetails registerUser(ServletWebRequest request, ProviderSignInUtils providerSignInUtils) throws RegisterUserFailureException {

        UserInfo userInfo = RequestUtil.extractRequest2Object(request.getRequest(), objectMapper, UserInfo.class);

        try
        {
            Connection<?> connectionFromSession = providerSignInUtils.getConnectionFromSession(request);
            log.info("Demo ======>: connectionFromSession = {}", connectionFromSession);

            // 用户信息持久化逻辑。。。
            // ...

            String encodedPassword = passwordEncoder.encode(userInfo.getPassword());
            // OAuth 信息存储
            providerSignInUtils.doPostSignUp(userInfo.getUserId(), request);
            log.info("Demo ======>: 第三方登录用户：{}, 注册成功", userInfo.getUserId());
            User user = new User(userInfo.getUserId(),
                                  encodedPassword,
                                  true,
                                  true,
                                  true,
                                  true,
                                  AuthorityUtils.commaSeparatedStringToAuthorityList("admin, ROLE_USER")
            );

            // 把用户信息存入缓存
            cacheUserDetailsService.putUserInCache(user);

            return user;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new RegisterUserFailureException(ErrorCodeEnum.USER_REGISTER_FAILURE, e, userInfo.getUserId());
        }
    }

    private String getValueOfRequest(ServletWebRequest request, String paramName, ErrorCodeEnum usernameNotEmpty) throws RegisterUserFailureException {
        String result = request.getParameter(paramName);
        if (result == null)
        {
            throw new RegisterUserFailureException(usernameNotEmpty, request.getSessionId());
        }
        return result;
    }

}
