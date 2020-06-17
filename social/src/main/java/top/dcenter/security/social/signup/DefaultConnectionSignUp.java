package top.dcenter.security.social.signup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.security.SocialUserDetails;
import top.dcenter.security.social.api.service.AbstractSocialUserDetailService;
import top.dcenter.security.social.api.signup.BaseConnectionSignUp;

/**
 * 默认的第三方授权登录时自动注册处理器。<br><br>
 * {@link ConnectionSignUp#execute(Connection)} 功能：第三方登录自动注册时, 从第三方的 connection 中获取用户唯一标识。<br><br>
 * 当实现 {@link BaseConnectionSignUp} 接口且 security.social.autoSignIn=true 则第三方授权登录时自动注册.<br><br>
 *     注意：要替换此类，实现 {@link BaseConnectionSignUp} 接口且注册 IOC 容器
 * @see ConnectionSignUp
 * @author zyw
 * @version V1.0  Created by 2020/5/14 22:32
 */
@Slf4j
public class DefaultConnectionSignUp implements BaseConnectionSignUp {

    private final AbstractSocialUserDetailService userDetailsService;

    public DefaultConnectionSignUp(AbstractSocialUserDetailService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public String execute(Connection<?> connection) {
        // 这里为第三方登录自动注册时调用，所以这里不需要实现对用户信息的注册，可以在用户登录完成后提示用户修改用户信息。
        try {
            // 重名检查
            SocialUserDetails socialUserDetails = userDetailsService.loadUserByUserId(connection.getDisplayName());
            if (socialUserDetails == null)
            {
                return connection.getDisplayName();
            }
            return null;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
