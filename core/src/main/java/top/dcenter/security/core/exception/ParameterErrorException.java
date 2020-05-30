package top.dcenter.security.core.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * 参数错误异常
 * @author zyw
 * @version V1.0  Created by 2020/5/5 22:16
 */
public class ParameterErrorException extends AuthenticationException {

    private static final long serialVersionUID = -7950185017387731913L;
    @Getter
    private ErrorCodeEnum errorCodeEnum;
    @Getter
    private String data;

    public ParameterErrorException(ErrorCodeEnum errorCodeEnum, String data) {
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
        this.data = data;
    }

    public ParameterErrorException(ErrorCodeEnum errorCodeEnum, Throwable cause, String data) {
        super(errorCodeEnum.getMsg(), cause);
        this.errorCodeEnum = errorCodeEnum;
        this.data = data;
    }
}
