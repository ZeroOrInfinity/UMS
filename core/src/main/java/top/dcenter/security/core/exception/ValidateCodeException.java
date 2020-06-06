package top.dcenter.security.core.exception;

import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 9:40
 */
public class ValidateCodeException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = -7608813150135647861L;

    public ValidateCodeException(ErrorCodeEnum errorCodeEnum, Throwable t, String ip) {
        super(errorCodeEnum, t, null, ip);
    }

    public ValidateCodeException(ErrorCodeEnum errorCodeEnum, String ip) {
        super(errorCodeEnum, null, ip);
    }
}
