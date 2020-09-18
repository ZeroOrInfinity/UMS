package top.dcenter.ums.security.core.exception;

import top.dcenter.ums.security.core.enums.ErrorCodeEnum;

/**
 * 注册用户失败
 * @author zyw
 * @version V1.0  Created by 2020/5/4 9:40
 */
public class RegisterUserFailureException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = 9180897671726519378L;

    public RegisterUserFailureException(ErrorCodeEnum errorCodeEnum, Throwable t, String userId) {
        super(errorCodeEnum, t, null, userId);
    }

    public RegisterUserFailureException(ErrorCodeEnum errorCodeEnum, String userId) {
        super(errorCodeEnum, null, userId);
    }
}
