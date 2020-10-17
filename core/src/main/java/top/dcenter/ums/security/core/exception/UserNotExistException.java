package top.dcenter.ums.security.core.exception;

import top.dcenter.ums.security.common.enums.ErrorCodeEnum;


/**
 * 用户不存在异常
 * @author zhailiang
 * @author  zyw
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
