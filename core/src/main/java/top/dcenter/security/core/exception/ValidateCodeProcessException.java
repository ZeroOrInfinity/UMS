package top.dcenter.security.core.exception;

import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * 验证码处理异常
 *
 * @author zyw
 * @version V1.0  Created by 2020/5/6 16:04
 */
public class ValidateCodeProcessException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = -1186543966394757028L;

    public ValidateCodeProcessException(ErrorCodeEnum errorCodeEnum, String ip) {
        super(errorCodeEnum, null, ip);
    }

    public ValidateCodeProcessException(ErrorCodeEnum errorCodeEnum, Throwable cause, String ip) {
        super(errorCodeEnum, cause, null, ip);
    }
}