package top.dcenter.security.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.util.ValidateCodeUtil;
import top.dcenter.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.security.core.api.validate.code.SmsCodeSender;

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

        String code = ValidateCodeUtil.generateNumberVerifyCode(codeLength);
        if (log.isDebugEnabled())
        {
            log.debug("Demo =======>: {} = {}", this.validateCodeProperties.getSms().getRequestParamSmsCodeName(),
                      code);
        }
        return new ValidateCode(code, expireIn);
    }
}