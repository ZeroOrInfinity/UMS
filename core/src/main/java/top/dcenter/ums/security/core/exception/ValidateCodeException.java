package top.dcenter.ums.security.core.exception;

import top.dcenter.ums.security.core.enums.ErrorCodeEnum;

/**
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/4 9:40
 */
public class ValidateCodeException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = -7608813150135647861L;

    public ValidateCodeException(ErrorCodeEnum errorCodeEnum, Throwable t, String ip, String data) {
        super(errorCodeEnum, t, data, ip);
    }

    public ValidateCodeException(ErrorCodeEnum errorCodeEnum, String ip, String data) {
        super(errorCodeEnum, data, ip);
    }
}
