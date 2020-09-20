package top.dcenter.ums.security.social.api.service;

import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.exception.RegisterUserFailureException;

/**
 * OAuth 登录的用户注册接口.<br><br>
 * 推荐通过继承来实现 {@link AbstractSocialUserDetailsService} 此接口的功能
 * @author zyw
 * @version V1.0
 * Created by 2020/5/16 10:48
 */
public interface SocialUserDetailsRegisterService {

    /**
     * 第三方登录的用户注册
     * @param request   request
     * @param providerSignInUtils   requested
     * @return  注册后的 UserDetails 信息
     * @throws  RegisterUserFailureException  RegisterUserFailureException
     */
    SocialUserDetails registerUser(ServletWebRequest request, ProviderSignInUtils providerSignInUtils) throws RegisterUserFailureException;

}