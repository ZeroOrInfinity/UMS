package top.dcenter.ums.security.core.api.service;

import me.zhyd.oauth.model.AuthUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.exception.RegisterUserFailureException;

/**
 * 用户名密码注册、手机短信登录与 OAuth 登录的用户注册接口.<br><br>
 * 推荐通过继承来实现 {@link UmsUserDetailsService} 此接口的功能
 * @author zyw
 * @version V1.0
 * Created by 2020/5/16 10:48
 */
public interface UserDetailsRegisterService {
    /**
     * 手机短信登录用户注册接口
     * @param mobile    手机号
     * @return  注册后的 UserDetails 信息
     * @throws RegisterUserFailureException 用户注册失败
     */
    UserDetails registerUser(String mobile) throws RegisterUserFailureException;

    /**
     * 用户名密码注册
     * @param request request
     * @return  注册后的 UserDetails 信息
     * @throws RegisterUserFailureException 用户注册失败
     */
    UserDetails registerUser(ServletWebRequest request) throws RegisterUserFailureException;

    /**
     * 第三方第一次登录成功后自动注册接口, 默认方法直接抛出 {@link RegisterUserFailureException}.
     * 这里是为了兼容不需要第三方授权登录功能的应用, 特意设置为默认方法.
     *
     * @param authUser         {@link AuthUser}
     * @param username         username(即本地系统的 userId), 通常情况下为 {@link AuthUser#getUsername()} 或
     *                         {@link AuthUser#getUsername()} + "_" + {@link AuthUser#getSource()}
     * @param defaultAuthority 第三方授权登录成功后的默认权限, 多个权限用逗号分开
     * @return 注册后的 UserDetails 信息
     * @throws RegisterUserFailureException 用户注册失败
     */
    default UserDetails registerUser(AuthUser authUser, String username, String defaultAuthority) throws RegisterUserFailureException {
        throw new RegisterUserFailureException(ErrorCodeEnum.USER_REGISTER_FAILURE, null);
    }

}
