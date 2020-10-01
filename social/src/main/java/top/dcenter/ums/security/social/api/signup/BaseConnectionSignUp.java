package top.dcenter.ums.security.social.api.signup;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

/**
 * 第三方授权登录时自动注册处理器接口。<br><br>
 * {@link ConnectionSignUp#execute(Connection)} 功能：第三方登录自动注册时，从第三方的 connection 中获取用户唯一标识。<br><br>
 * 当实现此接口且 ums.social.autoSignIn=true 则第三方授权登录时自动注册
 * @see ConnectionSignUp
 * @author zyw
 * @version V1.0  Created by 2020/5/14 22:32
 */
public interface BaseConnectionSignUp extends ConnectionSignUp {
}
