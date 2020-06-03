package top.dcenter.security.core.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * session 增强检测异常
 * @author zyw
 * @version V1.0  Created by 2020/6/2 10:58
 */
public class SessionEnhanceCheckException extends AuthenticationException {

    private static final long serialVersionUID = 2776218597485877301L;
    @Getter
    private ErrorCodeEnum errorCodeEnum;

    public SessionEnhanceCheckException(ErrorCodeEnum errorCodeEnum, Throwable t) {
        super(errorCodeEnum.getMsg(), t);
        this.errorCodeEnum = errorCodeEnum;
    }

    public SessionEnhanceCheckException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
    }
}
