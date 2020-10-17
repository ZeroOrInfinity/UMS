package top.dcenter.ums.security.core.exception;

import top.dcenter.ums.security.common.enums.ErrorCodeEnum;

/**
 * session 失效异常
 * @author zyw
 * @version V1.0  Created by 2020/6/6 20:37
 */
public class ExpiredSessionDetectedException extends AbstractResponseJsonAuthenticationException {
    private static final long serialVersionUID = -395493939092758754L;

    public ExpiredSessionDetectedException(ErrorCodeEnum errorCodeEnum, Throwable t, String sessionId) {
        super(errorCodeEnum, t, null, sessionId);
    }

    public ExpiredSessionDetectedException(ErrorCodeEnum errorCodeEnum, String sessionId) {
        super(errorCodeEnum, null, sessionId);
    }
}
