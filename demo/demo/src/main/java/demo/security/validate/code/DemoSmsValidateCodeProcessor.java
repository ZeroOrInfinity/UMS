package demo.security.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.api.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;
import top.dcenter.ums.security.core.auth.validate.codes.sms.SmsValidateCodeProcessor;
import top.dcenter.ums.security.core.consts.RegexConstants;
import top.dcenter.ums.security.core.exception.ValidateCodeParamErrorException;

import java.util.regex.PatternSyntaxException;

import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.MOBILE_FORMAT_ERROR;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.MOBILE_PARAMETER_ERROR;

/**
 * 自定义短信验证码处理器
 * @author zyw
 * @version V1.0 Created by 2020-05-14 22:26
 */
@Component
@Slf4j
public class DemoSmsValidateCodeProcessor extends SmsValidateCodeProcessor {

    public DemoSmsValidateCodeProcessor(ValidateCodeGeneratorHolder validateCodeGeneratorHolder) {
        super(validateCodeGeneratorHolder);
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
            if (StringUtils.isNotBlank(mobile) && mobile.matches(RegexConstants.MOBILE_PATTERN))
            {
                log.info("短信验证码发生成功：{} = {}", mobile, validateCode.getCode());
                return smsCodeSender.sendSms(mobile, validateCode.getCode());
            }
        }
        catch (ServletRequestBindingException e)
        {
            throw new ValidateCodeParamErrorException(MOBILE_PARAMETER_ERROR, validateCodeProperties.getSms().getRequestParamMobileName(), request.getRequest().getRemoteAddr());
        }
        catch (PatternSyntaxException e) {
            log.error(e.getMessage(), e);
        }

        throw new ValidateCodeParamErrorException(MOBILE_FORMAT_ERROR, mobile, request.getRequest().getRemoteAddr());
    }

    @Override
    public ValidateCodeType getValidateCodeType() {
        return ValidateCodeType.SMS;
    }
}