package top.dcenter.ums.security.core.exception;

import top.dcenter.ums.security.common.enums.ErrorCodeEnum;

/**
 * 解绑(第三方)异常
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.2.24 14:29
 */
public class UnBindingExcepition extends BusinessException {

    public UnBindingExcepition(ErrorCodeEnum errorCodeEnum, Object data) {
        super(errorCodeEnum, data);
    }

    public UnBindingExcepition(ErrorCodeEnum errorCodeEnum, Object data, Throwable cause) {
        super(errorCodeEnum, data, cause);
    }

}
