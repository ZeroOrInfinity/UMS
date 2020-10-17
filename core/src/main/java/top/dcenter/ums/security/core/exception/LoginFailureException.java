package top.dcenter.ums.security.core.exception;

import top.dcenter.ums.security.common.enums.ErrorCodeEnum;

/**
 * 登录失败异常
 * @author zyw
 * @version V1.0  Created by 2020/6/6 18:54
 */
public class LoginFailureException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = -3482409811605527239L;

    public LoginFailureException(ErrorCodeEnum errorCodeEnum, Throwable t, Object data, String userIdOrSessionId) {
        super(errorCodeEnum, t, data, userIdOrSessionId);
    }

    public LoginFailureException(ErrorCodeEnum errorCodeEnum, Object data, String userIdOrSessionId) {
        super(errorCodeEnum, data, userIdOrSessionId);
    }
}
