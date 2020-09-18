package top.dcenter.ums.security.core.exception;

import top.dcenter.ums.security.core.enums.ErrorCodeEnum;

/**
 * 非法访问
 * @author zyw
 * @version V1.0  Created by 2020/5/9 21:05
 */
public class IllegalAccessUrlException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = 5168157568196656844L;

    public IllegalAccessUrlException(ErrorCodeEnum errorCodeEnum, Throwable t, String uri, String ip) {
        super(errorCodeEnum, t, uri, ip);
    }

    public IllegalAccessUrlException(ErrorCodeEnum errorCodeEnum, String uri, String ip) {
        super(errorCodeEnum, uri, ip);
    }
}
