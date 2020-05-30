package top.dcenter.security.core.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * 验证码处理异常
 *
 * @author zyw
 * @version V1.0  Created by 2020/5/6 16:04
 */
public class ValidateCodeProcessException extends AuthenticationException {

    private static final long serialVersionUID = -1186543966394757028L;
    @Getter
    private ErrorCodeEnum errorCodeEnum;

    public ValidateCodeProcessException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
    }

    public ValidateCodeProcessException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(errorCodeEnum.getMsg(), cause);
        this.errorCodeEnum = errorCodeEnum;
    }
}
