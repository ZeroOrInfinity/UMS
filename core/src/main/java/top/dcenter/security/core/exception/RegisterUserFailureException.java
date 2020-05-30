package top.dcenter.security.core.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * 注册用户失败
 * @author zyw
 * @version V1.0  Created by 2020/5/4 9:40
 */
public class RegisterUserFailureException extends AuthenticationException {

    private static final long serialVersionUID = 9180897671726519378L;
    @Getter
    private ErrorCodeEnum errorCodeEnum;

    public RegisterUserFailureException(ErrorCodeEnum errorCodeEnum, Throwable t) {
        super(errorCodeEnum.getMsg(), t);
        this.errorCodeEnum = errorCodeEnum;
    }

    public RegisterUserFailureException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
    }
}
