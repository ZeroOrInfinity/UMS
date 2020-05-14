package top.dcenter.security.core.excception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/7 13:19
 */
public class ValidateCodeParamErrorException extends AuthenticationException {
    public ValidateCodeParamErrorException(String message) {
        super(message);
    }

    public ValidateCodeParamErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
