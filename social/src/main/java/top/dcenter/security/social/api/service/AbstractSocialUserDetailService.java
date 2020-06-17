package top.dcenter.security.social.api.service;

import org.springframework.context.ApplicationContext;
import org.springframework.social.security.SocialUserDetailsService;
import top.dcenter.security.core.api.service.AbstractUserDetailsService;

/**
 * 用户名密码、手机短信、 OAuth 的登录与注册服务：<br><br>
 *     1. 用于用户名密码、手机短信、 OAuth 的登录逻辑。<br><br>
 *     2. 用于用户名密码、手机短信、 OAuth 的注册逻辑。<br><br>
 *     3. 如果要使用缓存, 引入 {@link SocialCacheUserDetailsService}, 用法可以参考 {@link top.dcenter.security.service.LoginSocialUserDetailService}.
 * @author zyw
 * @version V1.0  Created by 2020/5/16 10:19
 */
@SuppressWarnings("JavadocReference")
public abstract class AbstractSocialUserDetailService extends AbstractUserDetailsService implements SocialUserDetailsService {

    public AbstractSocialUserDetailService(ApplicationContext applicationContext) {
        super(applicationContext);
    }

}
