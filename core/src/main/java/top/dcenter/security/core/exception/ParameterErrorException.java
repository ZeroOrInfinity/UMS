package top.dcenter.security.core.exception;

import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * 参数错误异常
 * @author zyw
 * @version V1.0  Created by 2020/5/5 22:16
 */
public class ParameterErrorException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = -7950185017387731913L;

    public ParameterErrorException(ErrorCodeEnum errorCodeEnum, String data, String sessionId) {
        super(errorCodeEnum, data, sessionId);
    }

    public ParameterErrorException(ErrorCodeEnum errorCodeEnum, Throwable cause, String data, String sessionId) {
        super(errorCodeEnum, cause, data, sessionId);
    }
}
