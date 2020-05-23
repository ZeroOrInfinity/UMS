package top.dcenter.security.core.validate.code.smscode;

import lombok.extern.slf4j.Slf4j;
import top.dcenter.security.core.api.validateCode.SmsCodeSender;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.validate.code.ValidateCode;
import top.dcenter.security.core.api.validateCode.ValidateCodeGenerator;
import top.dcenter.security.core.validate.code.ValidateCodeType;

import javax.servlet.ServletRequest;

/**
 * 短信验证码生成器。如要自定义短信验证码生成器，请实现此接口 {@link SmsCodeSender} 。注意：实现类注册 ioc 容器 bean 即可
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 23:44
 */
@Slf4j
public class SmsCodeGenerator implements ValidateCodeGenerator<ValidateCode> {

    private final ValidateCodeProperties validateCodeProperties;
    private final SmsCodeSender smsCodeSender;

    public SmsCodeGenerator(ValidateCodeProperties validateCodeProperties, SmsCodeSender smsCodeSender) {
        this.validateCodeProperties = validateCodeProperties;
        this.smsCodeSender = smsCodeSender;
    }

    @Override
    public ValidateCode generate(ServletRequest request) {
        return smsCodeSender.getCode();
    }

    @Override
    public String getValidateCodeType() {
        return ValidateCodeType.SMS.name().toLowerCase();
    }

    @Override
    public String getRequestParamValidateCodeName() {
        return this.validateCodeProperties.getSms().getRequestParamSmsCodeName();
    }

}
