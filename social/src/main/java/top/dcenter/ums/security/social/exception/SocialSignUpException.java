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

package top.dcenter.ums.security.social.exception;

import top.dcenter.ums.security.core.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.exception.AbstractResponseJsonAuthenticationException;

/**
 * 第三方登录注册异常，如用户名重名等
 * @author YongWu zheng
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