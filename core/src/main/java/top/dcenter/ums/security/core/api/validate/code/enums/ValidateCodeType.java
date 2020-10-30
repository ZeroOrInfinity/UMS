/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.api.validate.code.enums;

import static top.dcenter.ums.security.common.consts.SecurityConstants.SESSION_KEY_CUSTOMIZE;
import static top.dcenter.ums.security.common.consts.SecurityConstants.SESSION_KEY_IMAGE;
import static top.dcenter.ums.security.common.consts.SecurityConstants.SESSION_KEY_SELECTION;
import static top.dcenter.ums.security.common.consts.SecurityConstants.SESSION_KEY_SLIDER;
import static top.dcenter.ums.security.common.consts.SecurityConstants.SESSION_KEY_SMS;
import static top.dcenter.ums.security.common.consts.SecurityConstants.SESSION_KEY_TRACK;

/**
 * 验证码类型
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/7 9:35
 */
public enum ValidateCodeType {
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