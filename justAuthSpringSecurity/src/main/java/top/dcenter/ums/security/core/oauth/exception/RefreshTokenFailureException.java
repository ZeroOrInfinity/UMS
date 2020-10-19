package top.dcenter.ums.security.core.oauth.exception;


import top.dcenter.ums.security.core.oauth.enums.ErrorCodeEnum;

/**
 * refresh token failure exception
 * @author zyw
 * @version V2.0  Created by 2020/10/10 22:39
 */
public class RefreshTokenFailureException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = 6209232579710442552L;

    public RefreshTokenFailureException(ErrorCodeEnum errorCodeEnum, Throwable t, Object data, String uid) {
        super(errorCodeEnum, t, data, uid);
    }

    public RefreshTokenFailureException(ErrorCodeEnum errorCodeEnum, Object data, String uid) {
        super(errorCodeEnum, data, uid);
    }
}
