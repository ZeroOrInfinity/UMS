package top.dcenter.ums.security.social.api.service;

import org.springframework.social.security.SocialUserDetailsService;
import top.dcenter.ums.security.core.api.service.AbstractUserDetailsService;

/**
 * 用户名密码、手机短信、 OAuth 的登录与注册服务：<br><br>
 *     1. 用于用户名密码、手机短信、 OAuth 的登录逻辑。<br><br>
 *     2. 用于用户名密码、手机短信、 OAuth 的注册逻辑。<br><br>
 *     3. 如果要使用缓存, 引入 {@link SocialUserCache}, 用法可以参考
 *     <pre> demo.security.service.LoginSocialUserDetailService </pre>
 * @author zyw
 * @version V1.0  Created by 2020/5/16 10:19
 */
@SuppressWarnings("JavadocReference")
public abstract class AbstractSocialUserDetailsService extends AbstractUserDetailsService implements SocialUserDetailsService, SocialUserDetailsRegisterService {

}
