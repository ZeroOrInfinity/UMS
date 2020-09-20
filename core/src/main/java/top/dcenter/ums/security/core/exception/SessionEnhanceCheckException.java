package top.dcenter.ums.security.core.exception;

import top.dcenter.ums.security.core.enums.ErrorCodeEnum;

/**
 * session 增强检测异常
 * @author zyw
 * @version V1.0  Created by 2020/6/2 10:58
 */
public class SessionEnhanceCheckException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = 2776218597485877301L;

    public SessionEnhanceCheckException(ErrorCodeEnum errorCodeEnum, Throwable t, String sessionId, String data) {
        super(errorCodeEnum, t, data, sessionId);
    }

    public SessionEnhanceCheckException(ErrorCodeEnum errorCodeEnum, String sessionId, String data) {
        super(errorCodeEnum, data, sessionId);
    }
}
