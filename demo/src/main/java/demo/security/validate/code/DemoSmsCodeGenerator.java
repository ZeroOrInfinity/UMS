package demo.security.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.core.api.validate.code.SmsCodeSender;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeTokenFactory;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.ums.security.core.auth.validate.codes.sms.SmsCodeGenerator;
import top.dcenter.ums.security.core.properties.ValidateCodeProperties;

import javax.servlet.ServletRequest;

/**
 * 推荐实现此接口 {@link SmsCodeSender}。
 * @author zyw
 * @version V1.0  Created by 2020-05-14 22:23
 */
@Component()
@Slf4j
public class DemoSmsCodeGenerator extends SmsCodeGenerator {

    public DemoSmsCodeGenerator(SmsCodeSender smsCodeSender, ValidateCodeProperties validateCodeProperties, ValidateCodeTokenFactory validateCodeTokenFactory) {
        super(validateCodeProperties, smsCodeSender, validateCodeTokenFactory);
    }

    @Override
    public ValidateCode generate(ServletRequest request) {
        ValidateCode validateCode = smsCodeSender.getCode(validateCodeTokenFactory);
        if (log.isDebugEnabled())
        {
            log.debug("Demo =======>: {} = {}", this.validateCodeProperties.getSms().getRequestParamSmsCodeName(),
                      validateCode.getCode());
        }
        return validateCode;
    }

}