package top.dcenter.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.util.CodeUtil;
import top.dcenter.security.core.validate.code.ValidateCode;
import top.dcenter.security.core.api.validateCode.SmsCodeSender;

/**
 * 自定义发送短信验证码
 * @author zyw
 * @createrDate 2020-05-14 22:26
 */
@Component
@Slf4j
public class DemoSmsCodeSender implements SmsCodeSender {
    public DemoSmsCodeSender(ValidateCodeProperties validateCodeProperties) {
        this.validateCodeProperties = validateCodeProperties;
    }

    private ValidateCodeProperties validateCodeProperties;

    @Override
    public boolean sendSms(String mobile, String validateCode) {
        log.info("Demo =====>: 短信验证码发送成功：{}", validateCode);
        return true;
    }

    @Override
    public ValidateCode getCode() {
        ValidateCodeProperties.SmsCodeProperties smsCodeProp = this.validateCodeProperties.getSms();
        int expireIn = smsCodeProp.getExpire();
        int codeLength = smsCodeProp.getLength();

        String code = CodeUtil.generateNumberVerifyCode(codeLength);

        return new ValidateCode(code, expireIn);
    }
}