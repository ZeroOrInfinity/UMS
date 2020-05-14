package top.dcenter.security.social;

import lombok.extern.slf4j.Slf4j;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

/**
 * 默认的第三方授权登录时自动注册处理器。<br>
 * social 第三方登录：从第三方的 connection 中获取用户唯一标识。<br>
 * 当实现 ConnectionSignUp 接口且 security.social.autoSignIn=true 则第三方授权登录时自动注册
 * @see ConnectionSignUp
 * @author zyw
 * @version V1.0  Created by 2020/5/14 22:32
 */
@Slf4j
public class DefaultConnectionSignUp implements ConnectionSignUp {
    @Override
    public String execute(Connection<?> connection) {
        // TODO 根据社交用户信息默认创建用户并返回用户唯一标识
        log.info("connection.displayName = {}", connection.getDisplayName());
        return connection.getDisplayName();
    }
}
