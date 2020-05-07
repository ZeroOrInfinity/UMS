package top.dcenter.security.core.excception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/4 9:40
 */
public class ValidateCodeException extends AuthenticationException {

    public ValidateCodeException(String msg, Throwable t) {
        super(msg, t);
    }

    public ValidateCodeException(String msg) {
        super(msg);
    }
}
