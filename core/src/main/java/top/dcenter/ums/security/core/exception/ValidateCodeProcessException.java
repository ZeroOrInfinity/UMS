package top.dcenter.ums.security.core.exception;

import top.dcenter.ums.security.common.enums.ErrorCodeEnum;

/**
 * 验证码处理异常
 *
 * @author zyw
 * @version V1.0  Created by 2020/5/6 16:04
 */
public class ValidateCodeProcessException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = -1186543966394757028L;

    public ValidateCodeProcessException(ErrorCodeEnum errorCodeEnum, String ip, String data) {
        super(errorCodeEnum, data, ip);
    }

    public ValidateCodeProcessException(ErrorCodeEnum errorCodeEnum, Throwable cause, String ip, String data) {
        super(errorCodeEnum, cause, data, ip);
    }
}
