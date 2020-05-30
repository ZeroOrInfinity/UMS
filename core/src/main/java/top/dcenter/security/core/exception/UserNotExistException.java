package top.dcenter.security.core.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import top.dcenter.security.core.enums.ErrorCodeEnum;


/**
 * 用户不存在异常
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/2 15:28
 */

public class UserNotExistException extends AuthenticationException {
    private static final long serialVersionUID = 3042211783958201322L;
    @Getter
    private String id;
    @Getter
    private ErrorCodeEnum errorCodeEnum;
    public UserNotExistException(ErrorCodeEnum errorCodeEnum, String id) {
        super(errorCodeEnum.getMsg());
        this.id = id;
        this.errorCodeEnum = errorCodeEnum;
    }

    public UserNotExistException(ErrorCodeEnum errorCodeEnum, Throwable cause, String id) {
        super(errorCodeEnum.getMsg(), cause);
        this.id = id;
        this.errorCodeEnum = errorCodeEnum;
    }
}
