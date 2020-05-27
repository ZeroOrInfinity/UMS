package top.dcenter.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.consts.RegexConst;
import top.dcenter.security.core.exception.ValidateCodeParamErrorException;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.api.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.security.core.validate.code.ValidateCode;
import top.dcenter.security.core.api.validate.code.ValidateCodeGenerator;
import top.dcenter.security.core.api.validate.code.SmsCodeSender;
import top.dcenter.security.core.validate.code.smscode.SmsValidateCodeProcessor;

import java.util.Map;
import java.util.regex.PatternSyntaxException;

/**
 * 自定义短信校验码处理器
 * @author zyw
 * @createrDate 2020-05-14 22:26
 */
@Component
@Slf4j
public class DemoSmsValidateCodeProcessor extends SmsValidateCodeProcessor {

    private final SmsCodeSender smsCodeSender;
    private final ValidateCodeProperties validateCodeProperties;

    public DemoSmsValidateCodeProcessor(SmsCodeSender smsCodeSender,
                                        ValidateCodeProperties validateCodeProperties,
                                        Map<String, ValidateCodeGenerator> validateCodeGenerators) {
        super(smsCodeSender, validateCodeProperties, validateCodeGenerators);
        this.smsCodeSender = smsCodeSender;
        this.validateCodeProperties = validateCodeProperties;
    }

    /**
     * @see  AbstractValidateCodeProcessor
     * @param request   ServletWebRequest
     * @param validateCode  校验码对象
     * @return
     * @exception ValidateCodeParamErrorException
     */
    @Override
    public boolean sent(ServletWebRequest request, ValidateCode validateCode) {
        String mobile;
        try
        {
            mobile = ServletRequestUtils.getRequiredStringParameter(request.getRequest(),
                                                                    validateCodeProperties.getSms().getRequestParamMobileName());
            if (StringUtils.isNotBlank(mobile) && mobile.matches(RegexConst.MOBILE_PATTERN))
            {
                log.info("Demo =======>: {} = {}", mobile, validateCode.getCode());
                return smsCodeSender.sendSms(mobile, validateCode.getCode());
            }
        }
        catch (ServletRequestBindingException e)
        {
            throw new ValidateCodeParamErrorException("不能获取接收验证码手机号");
        }
        catch (PatternSyntaxException e) { }

        throw new ValidateCodeParamErrorException("手机号格式错误，请检查你的手机号码");
    }
}