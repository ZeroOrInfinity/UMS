package top.dcenter.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.util.CodeUtil;
import top.dcenter.security.core.validate.code.ValidateCode;
import top.dcenter.security.core.validate.code.smscode.SmsCodeGenerator;

import javax.servlet.ServletRequest;

/**
 * 这里 bean 的名称必须是 smsCodeGenerator
 * @author zyw
 * @createrDate 2020-05-14 22:23
 */
@Component("smsCodeGenerator")
@Slf4j
public class DemoSmsCodeGenerator extends SmsCodeGenerator {

    private final ValidateCodeProperties validateCodeProperties;

    public DemoSmsCodeGenerator(ValidateCodeProperties validateCodeProperties) {
        super(validateCodeProperties);
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
            log.debug("Demo =======>: {} = {}", smsCodeProp.getRequestParamSmsCodeName(), code);
        }
        return new ValidateCode(code, expireIn);
    }

}