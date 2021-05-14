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

package top.dcenter.ums.security.core.api.validate.code.sms;

import top.dcenter.ums.security.core.api.validate.code.ValidateCode;
import top.dcenter.ums.security.core.auth.validate.codes.sms.DefaultSmsCodeSender;

/**
 * 发送短信验证码接口，更改短息验证码实现此接口。<br><br>
 *      注意：自定义实现类注册 ioc 容器，会自动覆盖 {@link DefaultSmsCodeSender}
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0
 * Created by 2020/5/5 21:30
 */
public interface SmsCodeSender {

    /**
     * 用于分隔手机号与短信验证码的分隔符
     */
    String SMS_CODE_SEPARATOR = "::";

    /**
     * 通过第三方发送短信验证码接口
     * @param mobile     手机号码
     * @param validateCode  验证码
     * @return  发送验证码是否成功的状态
     */
    boolean sendSms(String mobile, ValidateCode validateCode);

    /**
     * 生成验证码
     * @return {@link ValidateCode}
     */
    ValidateCode getCode();
}