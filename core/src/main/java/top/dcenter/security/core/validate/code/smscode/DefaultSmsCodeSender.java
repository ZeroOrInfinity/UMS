package top.dcenter.security.core.validate.code.smscode;

import lombok.extern.slf4j.Slf4j;
import top.dcenter.security.core.api.validate.code.SmsCodeSender;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.util.ValidateCodeUtil;
import top.dcenter.security.core.validate.code.ValidateCode;

/**
 * 默认短信发送器，无任何实现
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/5 21:36
 */
@Slf4j
public class DefaultSmsCodeSender implements SmsCodeSender {
    private ValidateCodeProperties validateCodeProperties;

    public DefaultSmsCodeSender(ValidateCodeProperties validateCodeProperties) {
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public boolean sendSms(String mobile, String validateCode) {
        if (log.isDebugEnabled())
        {
            log.debug("短信验证码发送成功：{}", validateCode);
        }
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
