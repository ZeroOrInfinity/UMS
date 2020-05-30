package top.dcenter.security.core.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * 非法访问 URL
 * @author zyw
 * @version V1.0  Created by 2020/5/9 21:05
 */
public class IllegalAccessUrlException extends AuthenticationException {

    private static final long serialVersionUID = 5168157568196656844L;
    @Getter
    private ErrorCodeEnum errorCodeEnum;

    public IllegalAccessUrlException(ErrorCodeEnum errorCodeEnum, Throwable t) {
        super(errorCodeEnum.getMsg(), t);
        this.errorCodeEnum = errorCodeEnum;
    }

    public IllegalAccessUrlException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
    }
}
