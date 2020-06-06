package top.dcenter.security.core.exception;

import top.dcenter.security.core.enums.ErrorCodeEnum;


/**
 * 用户不存在异常
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/2 15:28
 */

public class UserNotExistException extends AbstractResponseJsonAuthenticationException {
    private static final long serialVersionUID = 3042211783958201322L;

    public UserNotExistException(ErrorCodeEnum errorCodeEnum, String userId) {
        super(errorCodeEnum, null, userId);
    }

    public UserNotExistException(ErrorCodeEnum errorCodeEnum, Throwable cause, String userId) {
        super(errorCodeEnum, cause, null, userId);
    }
}
