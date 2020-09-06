package top.dcenter.security.social.exception;

import top.dcenter.security.core.enums.ErrorCodeEnum;
import top.dcenter.security.core.exception.AbstractResponseJsonAuthenticationException;

/**
 * 第三方登录注册异常，如用户名重名等
 * @author zyw
 * @version V1.0  Created by 2020/5/21 23:06
 */
@SuppressWarnings("unused")
public class SocialSignUpException extends AbstractResponseJsonAuthenticationException {

    private static final long serialVersionUID = 1078063791016707032L;

    public SocialSignUpException(ErrorCodeEnum errorCodeEnum, Throwable t, String userId) {
        super(errorCodeEnum, t, null, userId);
    }

    public SocialSignUpException(ErrorCodeEnum errorCodeEnum, String userId) {
        super(errorCodeEnum, null, userId);
    }
}
