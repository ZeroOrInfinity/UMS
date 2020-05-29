package top.dcenter.security.core.validate.code.smscode;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.api.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.security.core.api.validate.code.SmsCodeSender;
import top.dcenter.security.core.api.validate.code.ValidateCodeGenerator;
import top.dcenter.security.core.consts.RegexConst;
import top.dcenter.security.core.exception.ValidateCodeParamErrorException;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.validate.code.ValidateCode;
import top.dcenter.security.core.validate.code.ValidateCodeType;

import java.util.Map;
import java.util.regex.PatternSyntaxException;


/**
 * 短信验证码处理器。如要自定义短信验证码处理器，请继承此类并重写 sent 方法且注入 IOC 容器即可
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/6 15:09
 */
@Slf4j
public class SmsValidateCodeProcessor extends AbstractValidateCodeProcessor {

    protected final SmsCodeSender smsCodeSender;
    protected final ValidateCodeProperties validateCodeProperties;

    public SmsValidateCodeProcessor(SmsCodeSender smsCodeSender,
                                    ValidateCodeProperties validateCodeProperties,
                                    Map<String, ValidateCodeGenerator<?>> validateCodeGenerators) {
        super(validateCodeGenerators);
        this.smsCodeSender = smsCodeSender;
        this.validateCodeProperties = validateCodeProperties;
    }

    /**
     * @see  AbstractValidateCodeProcessor
     * @param request   ServletWebRequest
     * @param validateCode  校验码对象
     * @return boolean
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

    @Override
    public String getValidateCodeType() {
        return ValidateCodeType.SMS.name().toLowerCase();
    }
}
