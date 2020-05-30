package top.dcenter.security.core.enums;

import static top.dcenter.security.core.consts.SecurityConstants.SESSION_KEY_CUSTOMIZE;
import static top.dcenter.security.core.consts.SecurityConstants.SESSION_KEY_IMAGE;
import static top.dcenter.security.core.consts.SecurityConstants.SESSION_KEY_SELECTION;
import static top.dcenter.security.core.consts.SecurityConstants.SESSION_KEY_SLIDER;
import static top.dcenter.security.core.consts.SecurityConstants.SESSION_KEY_SMS;
import static top.dcenter.security.core.consts.SecurityConstants.SESSION_KEY_TRACK;

/**
 * 验证码类型
 * @author zhailiang
 * @medifiedBy  zyw
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
    },
    /**
     * 轨迹验证码
     */
    TRACK {
        @Override
        public String getSessionKey() {
            return SESSION_KEY_TRACK;
        }
    },
    /**
     * 滑块验证码
     */
    SLIDER {
        @Override
        public String getSessionKey() {
            return SESSION_KEY_SLIDER;
        }
    },
    /**
     * 从图片中选取内容的验证码
     */
    SELECTION {
        @Override
        public String getSessionKey() {
            return SESSION_KEY_SELECTION;
        }
    },
    /**
     * 自定义的验证码
     */
    CUSTOMIZE {
        @Override
        public String getSessionKey() {
            return SESSION_KEY_CUSTOMIZE;
        }
    };

    /**
     * 返回相应的 SessionKey
     * @return SessionKey
     */
    public abstract String getSessionKey();

}
