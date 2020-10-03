package top.dcenter.ums.security.core.api.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.ServletWebRequest;
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
     * @throws RegisterUserFailureException RegisterUserFailureException
     */
    UserDetails registerUser(String mobile) throws RegisterUserFailureException;

    /**
     * 用户名密码注册
     * @param request request
     * @return  注册后的 UserDetails 信息
     * @throws RegisterUserFailureException RegisterUserFailureException
     */
    UserDetails registerUser(ServletWebRequest request) throws RegisterUserFailureException;

}
