package top.dcenter.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.validate.code.ValidateCode;
import top.dcenter.security.core.validate.code.smscode.SmsCodeGenerator;
import top.dcenter.security.core.api.validateCode.SmsCodeSender;

import javax.servlet.ServletRequest;

/**
 * 这里 bean 的名称必须是 smsCodeGenerator
 * @author zyw
 * @createrDate 2020-05-14 22:23
 */
@Component("smsCodeGenerator")
@Slf4j
public class DemoSmsCodeGenerator extends SmsCodeGenerator {

    private final SmsCodeSender smsCodeSender;
    private final ValidateCodeProperties validateCodeProperties;

    public DemoSmsCodeGenerator(SmsCodeSender smsCodeSender, ValidateCodeProperties validateCodeProperties) {
        super(validateCodeProperties, smsCodeSender);
        this.smsCodeSender = smsCodeSender;
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public ValidateCode generate(ServletRequest request) {
        ValidateCode validateCode = smsCodeSender.getCode();
        if (log.isDebugEnabled())
        {
            log.debug("Demo =======>: {} = {}", this.validateCodeProperties.getSms().getRequestParamSmsCodeName(),
                      validateCode.getCode());
        }
        return validateCode;
    }

}