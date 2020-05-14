package top.dcenter.security;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;

/**
 * social 第三方登录：从第三方的 connection 中获取用户唯一标识。<br>
 *     需要用户自己实现
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/12 21:41
 */
@Component
public class DemoConnectionSignUp implements ConnectionSignUp {
    @Override
    public String execute(Connection<?> connection) {
        // TODO 根据社交用户信息默认创建用户并返回用户唯一标识

        return connection.getDisplayName();

    }
}
