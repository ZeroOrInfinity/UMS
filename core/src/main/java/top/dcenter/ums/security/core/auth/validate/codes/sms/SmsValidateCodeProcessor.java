package top.dcenter.ums.security.core.auth.validate.codes.sms;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.api.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.ums.security.core.api.validate.code.sms.SmsCodeSender;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;
import top.dcenter.ums.security.core.consts.RegexConstants;
import top.dcenter.ums.security.core.exception.ValidateCodeParamErrorException;
import top.dcenter.ums.security.core.properties.ValidateCodeProperties;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.PatternSyntaxException;

import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.MOBILE_FORMAT_ERROR;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.MOBILE_PARAMETER_ERROR;


/**
 * 短信验证码处理器。如要自定义短信验证码处理器，请继承此类并重写 sent 方法且注入 IOC 容器即可
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/6 15:09
 */
@Slf4j
public class SmsValidateCodeProcessor extends AbstractValidateCodeProcessor {

    @Autowired
    protected SmsCodeSender smsCodeSender;
    @Autowired
    protected ValidateCodeProperties validateCodeProperties;

    public SmsValidateCodeProcessor(ValidateCodeGeneratorHolder validateCodeGeneratorHolder) {
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
        HttpServletRequest req = request.getRequest();
        String ip = req.getRemoteAddr();
        String sid = request.getSessionId();
        String uri = req.getRequestURI();
        try
        {
            mobile = ServletRequestUtils.getRequiredStringParameter(req,
                                                                    validateCodeProperties.getSms().getRequestParamMobileName());
            if (StringUtils.isNotBlank(mobile) && mobile.matches(RegexConstants.MOBILE_PATTERN))
            {
                return smsCodeSender.sendSms(mobile, validateCode.getCode());
            }
        }
        catch (ServletRequestBindingException e)
        {
            String msg = String.format("发送验证码失败-手机号参数错误: error=%s, ip=%s, sid=%s, uri=%s, validateCode=%s",
                                          e.getMessage(), ip, sid, uri, validateCode.toString());
            log.error(msg, e);
            throw new ValidateCodeParamErrorException(MOBILE_PARAMETER_ERROR,
                                                      validateCodeProperties.getSms().getRequestParamMobileName(),
                                                      ip);
        }
        catch (PatternSyntaxException e) {
            String msg = String.format("发送验证码失败-手机号格式不正确: error=%s, ip=%s, sid=%s, uri=%s, validateCode=%s",
                                          e.getMessage(), ip, sid, uri, validateCode.toString());
            log.error(msg, e);
        }

        throw new ValidateCodeParamErrorException(MOBILE_FORMAT_ERROR, mobile, ip);
    }

    @Override
    public ValidateCodeType getValidateCodeType() {
        return ValidateCodeType.SMS;
    }
}
