package top.dcenter.ums.security.core.auth.validate.codes.sms;

import lombok.extern.slf4j.Slf4j;
import top.dcenter.ums.security.core.api.validate.code.sms.SmsCodeSender;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.util.ValidateCodeUtil;

/**
 * 默认短信发送器，无任何实现, 建议自己自定义 {@link SmsCodeSender} , 并注入 IOC 容器, 会替代此类
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/5 21:36
 */
@Slf4j
public class DefaultSmsCodeSender implements SmsCodeSender {

    private final ValidateCodeProperties validateCodeProperties;

    public DefaultSmsCodeSender(ValidateCodeProperties validateCodeProperties) {
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public boolean sendSms(String mobile, String validateCode) {
        log.warn("你正在通过默认实现的发送短信验证码, 请实现 SmsCodeSender 接口: 验证码={}", validateCode);
        return true;
    }

    @Override
    public ValidateCode getCode() {
        ValidateCodeProperties.SmsCodeProperties smsCodeProp = this.validateCodeProperties.getSms();
        int expireIn = smsCodeProp.getExpire();
        int codeLength = smsCodeProp.getLength();

        String code = ValidateCodeUtil.generateNumberVerifyCode(codeLength);

        return new ValidateCode(code, expireIn);
    }
}
