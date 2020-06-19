package top.dcenter.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.stereotype.Component;
import top.dcenter.security.social.api.service.AbstractSocialUserDetailService;

/**
 * social 第三方登录：从第三方的 connection 中获取用户唯一标识。<br><br>
 * 当实现 ConnectionSignUp 接口且 security.social.autoSignIn=true 则第三方授权登录时自动注册
 * @see ConnectionSignUp
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/12 21:41
 */
@Component
@Slf4j
public class DemoConnectionSignUp implements ConnectionSignUp {

    @Autowired
    private AbstractSocialUserDetailService userDetailsService;

    public DemoConnectionSignUp() {
    }

    @Override
    public String execute(Connection<?> connection) {
        // 这里为第三方登录自动注册时调用，所以这里不需要实现对用户信息的注册，可以在用户登录完成后提示用户修改用户信息。
        try {
            // 重名检查
            SocialUserDetails socialUserDetails = userDetailsService.loadUserByUserId(connection.getDisplayName());
            if (socialUserDetails == null)
            {
                if (log.isInfoEnabled())
                {
                    log.info("Demo ===> connection.displayName = {}", connection.getDisplayName());
                }
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
