package top.dcenter.security.core.excception;

import org.springframework.security.core.AuthenticationException;

/**
 * 参数错误异常
 * @author zyw
 * @version V1.0  Created by 2020/5/5 22:16
 */
public class ParameterErrorException extends AuthenticationException {

    public ParameterErrorException(String message) {
        super(message);
    }

    public ParameterErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
