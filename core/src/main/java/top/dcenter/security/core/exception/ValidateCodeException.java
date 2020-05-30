package top.dcenter.security.core.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 9:40
 */
public class ValidateCodeException extends AuthenticationException {

    private static final long serialVersionUID = -7608813150135647861L;
    @Getter
    private ErrorCodeEnum errorCodeEnum;

    public ValidateCodeException(ErrorCodeEnum errorCodeEnum, Throwable t) {
        super(errorCodeEnum.getMsg(), t);
        this.errorCodeEnum = errorCodeEnum;
    }

    public ValidateCodeException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
    }
}
