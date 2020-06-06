package top.dcenter.security.core.exception;

import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * 非法访问 URL
 * @author zyw
 * @version V1.0  Created by 2020/5/9 21:05
 */
public class IllegalAccessUrlException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = 5168157568196656844L;

    public IllegalAccessUrlException(ErrorCodeEnum errorCodeEnum, Throwable t, String ip) {
        super(errorCodeEnum, t, null, ip);
    }

    public IllegalAccessUrlException(ErrorCodeEnum errorCodeEnum, String ip) {
        super(errorCodeEnum, null, ip);
    }
}
