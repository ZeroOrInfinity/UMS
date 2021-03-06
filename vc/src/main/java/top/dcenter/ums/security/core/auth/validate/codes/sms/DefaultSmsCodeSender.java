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

package top.dcenter.ums.security.core.auth.validate.codes.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.dcenter.ums.security.core.api.validate.code.ValidateCode;
import top.dcenter.ums.security.core.api.validate.code.sms.SmsCodeSender;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.util.ValidateCodeUtil;

import static java.util.Objects.nonNull;

/**
 * 默认短信发送器，无任何实现, 建议自己自定义 {@link SmsCodeSender} , 并注入 IOC 容器, 会替代此类
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/5 21:36
 */
@Slf4j
public class DefaultSmsCodeSender implements SmsCodeSender {

    private final ValidateCodeProperties validateCodeProperties;

    public DefaultSmsCodeSender(ValidateCodeProperties validateCodeProperties) {
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public boolean sendSms(String mobile, ValidateCode validateCode) {
        log.warn("你正在通过默认实现的发送短信验证码, 请实现 SmsCodeSender 接口: 验证码={}, {} 秒后失效",
                 validateCode.getCode(),
                 validateCode.getExpireIn());
        return true;
    }

    @Override
    public ValidateCode getCode() {
        ValidateCodeProperties.SmsCodeProperties smsCodeProp = this.validateCodeProperties.getSms();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String mobile = null;
        if (nonNull(requestAttributes)) {
            mobile = requestAttributes.getRequest().getParameter(smsCodeProp.getRequestParamMobileName());
        }

        int expireIn = smsCodeProp.getExpire();
        int codeLength = smsCodeProp.getLength();

        String code = ValidateCodeUtil.generateNumberVerifyCode(codeLength);

        return new ValidateCode(mobile + SMS_CODE_SEPARATOR + code, expireIn);
    }
}