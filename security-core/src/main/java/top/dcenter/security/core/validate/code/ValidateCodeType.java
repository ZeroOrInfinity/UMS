package top.dcenter.security.core.validate.code;

import static top.dcenter.security.core.consts.SecurityConstants.SESSION_KEY_IMAGE;
import static top.dcenter.security.core.consts.SecurityConstants.SESSION_KEY_SMS;

/**
 * 校验码类型
 * @author zyw
 * @version V1.0  Created by 2020/5/7 9:35
 */
public enum   ValidateCodeType {
    /**
     * 图片验证码
     */
    IMAGE {
        @Override
        public String getSessionKey() {
            return SESSION_KEY_IMAGE;
        }
    },
    /**
     * 短信验证码
     */
    SMS {
        @Override
        public String getSessionKey() {
            return SESSION_KEY_SMS;
        }
    };

    /**
     * 返回相应的 SessionKey
     * @return SessionKey
     */
    public abstract String getSessionKey();

}
