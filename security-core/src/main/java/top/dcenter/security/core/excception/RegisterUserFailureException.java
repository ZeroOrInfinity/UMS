package top.dcenter.security.core.excception;

import org.springframework.security.core.AuthenticationException;

/**
 * 注册用户失败
 * @author zyw
 * @version V1.0  Created by 2020/5/4 9:40
 */
public class RegisterUserFailureException extends AuthenticationException {

    public RegisterUserFailureException(String msg, Throwable t) {
        super(msg, t);
    }

    public RegisterUserFailureException(String msg) {
        super(msg);
    }
}
