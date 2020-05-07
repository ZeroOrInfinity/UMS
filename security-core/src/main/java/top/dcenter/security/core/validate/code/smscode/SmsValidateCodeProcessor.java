package top.dcenter.security.core.validate.code.smscode;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.consts.RegexConst;
import top.dcenter.security.core.enums.ValidateStatus;
import top.dcenter.security.core.excception.ValidateCodeParamErrorException;
import top.dcenter.security.core.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.security.core.validate.code.ValidateCode;
import top.dcenter.security.core.validate.code.ValidateCodeProperties;

import java.util.regex.PatternSyntaxException;

import static top.dcenter.security.core.consts.SecurityConstants.SESSION_KEY_SMS;


/**
 * 短信验证码处理器
 * @author zyw
 * @version V1.0  Created by 2020/5/6 15:09
 */
@Component
@Slf4j
public class SmsValidateCodeProcessor extends AbstractValidateCodeProcessor {

    private final SessionStrategy sessionStrategy;
    private final SmsCodeSender smsCodeSender;
    private final ValidateCodeProperties validateCodeProperties;

    public SmsValidateCodeProcessor(SmsCodeSender smsCodeSender, ValidateCodeProperties validateCodeProperties) {
        this.sessionStrategy = new HttpSessionSessionStrategy();
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
    public ValidateStatus sent(ServletWebRequest request, ValidateCode validateCode) {
        String mobile;
        try
        {
            mobile = ServletRequestUtils.getRequiredStringParameter(request.getRequest(),
                                                            validateCodeProperties.getSms().getRequestParamMobileName());
            if (StringUtils.isNotBlank(mobile) && mobile.matches(RegexConst.MOBILE_PATTERN))
            {
                ValidateStatus sendStatus = smsCodeSender.sendSms(mobile, validateCode.getCode());
                if (ValidateStatus.FAILURE.equals(sendStatus))
                {
                    sessionStrategy.removeAttribute(request, SESSION_KEY_SMS);
                    log.error("手机号：{}，发送验证码失败", mobile);
                }
                return sendStatus;
            }
        }
        catch (ServletRequestBindingException e)
        {
            sessionStrategy.removeAttribute(request, SESSION_KEY_SMS);
            throw new ValidateCodeParamErrorException("不能获取接收验证码手机号");
        }
        catch (PatternSyntaxException e) { }

        sessionStrategy.removeAttribute(request, SESSION_KEY_SMS);
        throw new ValidateCodeParamErrorException("手机号格式错误，请检查你的手机号码");
    }
}
