package top.dcenter.security.core.excception;

import org.springframework.security.core.AuthenticationException;

/**
 * 校验码处理异常
 *
 * @author zyw
 * @version V1.0  Created by 2020/5/6 16:04
 */
public class ValidateCodeProcessException extends AuthenticationException {
    public ValidateCodeProcessException(String message) {
        super(message);
    }

    public ValidateCodeProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
