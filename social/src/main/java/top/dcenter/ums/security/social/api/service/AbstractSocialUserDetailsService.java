package top.dcenter.ums.security.social.api.service;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.api.service.AbstractUserDetailsService;
import top.dcenter.ums.security.core.exception.RegisterUserFailureException;

/**
 * 用户名密码、手机短信、 OAuth 的登录与注册服务：<br><br>
 *     1. 用于用户名密码、手机短信、 OAuth 的登录逻辑。<br><br>
 *     2. 用于用户名密码、手机短信、 OAuth 的注册逻辑。<br><br>
 *     3. 如果要使用缓存, 引入 {@link SocialCacheUserDetailsService}, 用法可以参考 {@link top.dcenter.security.service.LoginSocialUserDetailService}.
 * @author zyw
 * @version V1.0  Created by 2020/5/16 10:19
 */
@SuppressWarnings("JavadocReference")
public abstract class AbstractSocialUserDetailsService extends AbstractUserDetailsService implements SocialUserDetailsService {

    public AbstractSocialUserDetailsService(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    /**
     * 第三方登录的用户注册
     * @param request   request
     * @return  注册后的 UserDetails 信息
     * @throws  RegisterUserFailureException  RegisterUserFailureException
     */
    public abstract UserDetails registerUser(ServletWebRequest request, ProviderSignInUtils providerSignInUtils) throws RegisterUserFailureException;


}
