package top.dcenter.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.api.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.security.core.api.validate.code.SmsCodeSender;
import top.dcenter.security.core.api.validate.code.ValidateCodeGenerator;
import top.dcenter.security.core.consts.RegexConst;
import top.dcenter.security.core.enums.ValidateCodeType;
import top.dcenter.security.core.exception.ValidateCodeParamErrorException;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.validate.code.ValidateCode;
import top.dcenter.security.core.validate.code.smscode.SmsValidateCodeProcessor;

import java.util.Map;
import java.util.regex.PatternSyntaxException;

import static top.dcenter.security.core.enums.ErrorCodeEnum.MOBILE_FORMAT_ERROR;
import static top.dcenter.security.core.enums.ErrorCodeEnum.MOBILE_PARAMETER_ERROR;

/**
 * 自定义短信验证码处理器
 * @author zyw
 * @version V1.0 Created by 2020-05-14 22:26
 */
@Component
@Slf4j
public class DemoSmsValidateCodeProcessor extends SmsValidateCodeProcessor {

    protected final SmsCodeSender smsCodeSender;
    protected final ValidateCodeProperties validateCodeProperties;

    public DemoSmsValidateCodeProcessor(SmsCodeSender smsCodeSender,
                                        ValidateCodeProperties validateCodeProperties,
                                        Map<String, ValidateCodeGenerator<?>> validateCodeGenerators) {
        super(smsCodeSender, validateCodeProperties,validateCodeGenerators);
        this.smsCodeSender = smsCodeSender;
        this.validateCodeProperties = validateCodeProperties;
    }

    /**
     * @see  AbstractValidateCodeProcessor
     * @param request   ServletWebRequest
     * @param validateCode  验证码对象
     * @return boolean
     */
    @Override
    public boolean sent(ServletWebRequest request, ValidateCode validateCode) {
        String mobile = null;
        try
        {
            mobile = ServletRequestUtils.getRequiredStringParameter(request.getRequest(),
                                                                    validateCodeProperties.getSms().getRequestParamMobileName());
            if (StringUtils.isNotBlank(mobile) && mobile.matches(RegexConst.MOBILE_PATTERN))
            {
                log.info("短信验证码发生成功：{} = {}", mobile, validateCode.getCode());
                return smsCodeSender.sendSms(mobile, validateCode.getCode());
            }
        }
        catch (ServletRequestBindingException e)
        {
            throw new ValidateCodeParamErrorException(MOBILE_PARAMETER_ERROR, validateCodeProperties.getSms().getRequestParamMobileName());
        }
        catch (PatternSyntaxException e) { }

        throw new ValidateCodeParamErrorException(MOBILE_FORMAT_ERROR, mobile);
    }

    @Override
    public ValidateCodeType getValidateCodeType() {
        return ValidateCodeType.SMS;
    }
}