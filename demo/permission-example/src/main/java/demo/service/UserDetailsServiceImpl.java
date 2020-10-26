/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package demo.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.entity.SysRole;
import demo.entity.SysUser;
import demo.entity.SysUserRole;
import demo.enums.UserStatusType;
import demo.enums.UserType;
import demo.polo.UserDO;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.exception.RegisterUserFailureException;
import top.dcenter.ums.security.core.exception.UserNotExistException;

import java.util.ArrayList;
import java.util.List;

/**
 *  用户密码与手机短信登录与注册服务：<br><br>
 *  1. 用于第三方登录与手机短信登录逻辑。<br><br>
 *  2. 用于用户密码登录逻辑。<br><br>
 *  3. 用户注册逻辑。<br><br>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/20 11:06
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UmsUserDetailsService {

    private final ObjectMapper objectMapper;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private ClientProperties clientProperties;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private SysUserService sysUserService;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private SysUserRoleService sysUserRoleService;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private SysRoleService sysRoleService;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private UserCache userCache;
    /**
     * 用于密码加解密
     */
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SuppressWarnings("AlibabaUndefineMagicConstant")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try
        {
            // 从缓存中查询用户信息
            if (this.userCache != null)
            {
                UserDetails userDetails = this.userCache.getUserFromCache(username);
                if (userDetails != null)
                {
                    return userDetails;
                }
            }

            // 根据用户名获取用户信息
            UserDO userDO = sysUserService.findByUsername(username);
            if (userDO != null)
            {
                // 示例：只是从用户登录日志表中提取的信息，
                log.info("Demo ======>: 登录用户名：{}, 登录成功", username);
                return new User(userDO.getUsername(),
                                userDO.getPassword(),
                                true,
                                true,
                                true,
                                true,
                                AuthorityUtils.commaSeparatedStringToAuthorityList(userDO.getAuthorities()));
            }

        }
        catch (Exception e)
        {
            String msg = String.format("Demo ======>: 登录用户名：%s, 登录失败: %s", username, e.getMessage());
            log.error(msg, e);
        }

        throw new UserNotExistException(ErrorCodeEnum.QUERY_USER_INFO_ERROR, username);
    }


    @Transactional(rollbackFor = {Error.class, RegisterUserFailureException.class})
    @Override
    public UserDetails registerUser(String mobile) throws RegisterUserFailureException {

        if (mobile == null)
        {
            throw new RegisterUserFailureException(ErrorCodeEnum.MOBILE_NOT_EMPTY, null);
        }

        SysUser sysUser= new SysUser();
        sysUser.setAuthorities("admin,ROLE_USER");
        sysUser.setPassword(passwordEncoder.encode("admin"));
        sysUser.setMobile(mobile);
        sysUser.setUsername(mobile);
        sysUser.setUserType(UserType.USER.ordinal());
        sysUser.setStatus(UserStatusType.NORMAL.ordinal());

        User user;
        try {
            // 用户信息持久化逻辑。。。
            sysUser = sysUserService.save(sysUser);

            // 添加用户角色权限
            addUserRole(sysUser, "ROLE_USER");

            log.info("Demo ======>: 手机短信登录用户 {}：注册成功", mobile);

            user = new User(sysUser.getUsername(),
                                 sysUser.getPassword(),
                                 true,
                                 true,
                                 true,
                                 true,
                                 AuthorityUtils.commaSeparatedStringToAuthorityList(sysUser.getAuthorities()));
        }
        catch (Exception e) {
            String msg = String.format("Demo ======>: 手机号：%s, 注册失败: %s", mobile, e.getMessage());
            log.error(msg, e);
            throw new RegisterUserFailureException(ErrorCodeEnum.USERNAME_USED, mobile);
        }

        // 把用户信息存入缓存
        if (userCache != null)
        {
            userCache.putUserInCache(user);
        }

        return user;
    }

    @Transactional(rollbackFor = {Error.class, RegisterUserFailureException.class})
    @Override
    public UserDetails registerUser(ServletWebRequest request) throws RegisterUserFailureException {

        String username = getValueOfRequest(request, clientProperties.getUsernameParameter(), ErrorCodeEnum.USERNAME_NOT_EMPTY);
        String password = getValueOfRequest(request, clientProperties.getPasswordParameter(), ErrorCodeEnum.PASSWORD_NOT_EMPTY);

        // 用户信息持久化逻辑。。。

        String encodedPassword = passwordEncoder.encode(password);

        SysUser sysUser= new SysUser();
        sysUser.setAuthorities("ROLE_USER");
        sysUser.setPassword(encodedPassword);
        sysUser.setUsername(username);
        sysUser.setUserType(UserType.USER.ordinal());
        sysUser.setStatus(UserStatusType.NORMAL.ordinal());

        User user;
        try {
            sysUser = sysUserService.save(sysUser);
            // 添加用户角色权限
            addUserRole(sysUser, "ROLE_USER");

            log.info("Demo ======>: 用户名：{}, 注册成功", username);
            user = new User(sysUser.getUsername(),
                                 sysUser.getPassword(),
                                 true,
                                 true,
                                 true,
                                 true,
                                 AuthorityUtils.commaSeparatedStringToAuthorityList(sysUser.getAuthorities()));

        }
        catch (Exception e) {
            String msg = String.format("Demo ======>: 手机号：%s, 注册失败: %s", username, e.getMessage());
            log.error(msg, e);
            throw new RegisterUserFailureException(ErrorCodeEnum.MOBILE_NOT_EMPTY, username);
        }

        // 把用户信息存入缓存
        if (userCache != null)
        {
            userCache.putUserInCache(user);
        }

        return user;

    }

    @Override
    public UserDetails registerUser(@NonNull AuthUser authUser, @NonNull String username, @NonNull String defaultAuthority,
                                    String decodeState) throws RegisterUserFailureException {

        // 第三方授权登录不需要密码, 这里随便设置的, 生成环境按自己的逻辑
        String encodedPassword = passwordEncoder.encode(authUser.getUuid());

        // 这里的 decodeState 可以根据自己实现的 top.dcenter.ums.security.core.oauth.service.Auth2StateCoder 接口的逻辑来传递必要的参数.
        // 比如: 第三方登录成功后的跳转地址
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 假设 decodeState 就是 redirectUrl, 我们直接把 redirectUrl 设置到 request 上
        // 后续经过成功处理器时直接从 requestAttributes.getAttribute("redirectUrl", RequestAttributes.SCOPE_REQUEST) 获取并跳转
        if (requestAttributes != null) {
            requestAttributes.setAttribute("redirectUrl", decodeState, RequestAttributes.SCOPE_REQUEST);
        }
        // 当然 decodeState 也可以传递从前端传到后端的用户信息, 注册到本地用户

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(defaultAuthority);

        // ... 用户注册逻辑

        log.info("Demo ======>: 用户名：{}, 注册成功", username);

        // @formatter:off
        UserDetails user = User.builder()
                               .username(username)
                               .password(encodedPassword)
                               .disabled(false)
                               .accountExpired(false)
                               .accountLocked(false)
                               .credentialsExpired(false)
                               .authorities(grantedAuthorities)
                               .build();
        // @formatter:off

        // 把用户信息存入缓存
        if (userCache != null)
        {
            userCache.putUserInCache(user);
        }

        return user;
    }

    /**
     * 添加用户角色
     * @param sysUser   sysUser
     * @param roleName  role
     */
    private void addUserRole(SysUser sysUser, String roleName) {
        // 根据角色名称获取角色
        SysRole sysRole = sysRoleService.findByName(roleName);
        // 如果角色不存在则添加角色
        if (sysRole == null)
        {
            SysRole role = new SysRole();
            role.setAvailable(true);
            role.setDescription("普通用户角色");
            role.setName(roleName);
            sysRole = sysRoleService.save(role);
        }
        // 添加用户角色权限
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setRoleId(sysRole.getId());
        sysUserRole.setUserId(sysUser.getId());
        sysUserRoleService.save(sysUserRole);
    }

    private String getValueOfRequest(ServletWebRequest request, String paramName, ErrorCodeEnum usernameNotEmpty) throws RegisterUserFailureException {
        String result = request.getParameter(paramName);
        if (result == null)
        {
            throw new RegisterUserFailureException(usernameNotEmpty, request.getSessionId());
        }
        return result;
    }

    @Override
    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        UserDetails userDetails = loadUserByUsername(userId);
        User.withUserDetails(userDetails);
        return User.withUserDetails(userDetails).build();
    }

    @Override
    public List<Boolean> existedByUsernames(String... usernames) throws UsernameNotFoundException {
        // ... 在本地账户上查询 userIds 是否已被使用
        List<Boolean> list = new ArrayList<>();
        list.add(true);
        list.add(false);
        list.add(false);

        return list;
    }

}