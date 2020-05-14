package top.dcenter.security.core.validate.code.smscode;

import lombok.extern.slf4j.Slf4j;
import top.dcenter.security.core.util.CodeUtil;
import top.dcenter.security.core.validate.code.ValidateCode;
import top.dcenter.security.core.validate.code.ValidateCodeGenerator;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.validate.code.ValidateCodeType;

import javax.servlet.ServletRequest;

/**
 * 短信验证码生成器。如要自定义短信验证码生成器，请继承此类并重写 generate 方法。注意：实现类注册 ioc 容器 bean 的名称必须是 smsCodeGenerator
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 23:44
 */
@Slf4j
public class SmsCodeGenerator implements ValidateCodeGenerator<ValidateCode> {

    private final ValidateCodeProperties validateCodeProperties;

    public SmsCodeGenerator(ValidateCodeProperties validateCodeProperties) {
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public ValidateCode generate(ServletRequest request) {
        ValidateCodeProperties.SmsCodeProperties smsCodeProp = this.validateCodeProperties.getSms();
        int expireIn = smsCodeProp.getExpire();
        int codeLength = smsCodeProp.getLength();

        String code = CodeUtil.generateNumberVerifyCode(codeLength);
        if (log.isDebugEnabled())
        {
            log.debug("{} = {}", smsCodeProp.getRequestParamSmsCodeName(), code);
        }
        return new ValidateCode(code, expireIn);
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
