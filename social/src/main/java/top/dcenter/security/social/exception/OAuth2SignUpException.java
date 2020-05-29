package top.dcenter.security.social.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 第三方登录注册异常，如用户名重名等
 * @author zyw
 * @version V1.0  Created by 2020/5/21 23:06
 */
@SuppressWarnings("unused")
public class OAuth2SignUpException extends AuthenticationException {
    public OAuth2SignUpException(String msg, Throwable t) {
        super(msg, t);
    }

    public OAuth2SignUpException(String msg) {
        super(msg);
    }
}
