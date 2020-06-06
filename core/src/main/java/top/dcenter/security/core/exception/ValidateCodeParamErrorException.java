package top.dcenter.security.core.exception;

import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * 验证码参数异常
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/7 13:19
 */
public class ValidateCodeParamErrorException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = 5071331297299386304L;

    public ValidateCodeParamErrorException(ErrorCodeEnum errorCodeEnum, String data, String ip) {
        super(errorCodeEnum, data, ip);
    }

    public ValidateCodeParamErrorException(ErrorCodeEnum errorCodeEnum, Throwable cause, String data,
                                           String ip) {
        super(errorCodeEnum, cause, data, ip);
    }
}
