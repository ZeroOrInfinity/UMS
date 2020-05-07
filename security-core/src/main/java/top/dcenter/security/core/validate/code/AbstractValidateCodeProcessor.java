package top.dcenter.security.core.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.enums.ValidateStatus;
import top.dcenter.security.core.excception.ParameterErrorException;
import top.dcenter.security.core.excception.ValidateCodeException;

import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.VALIDATE_CODE_GENERATOR_SUFFIX;
import static top.dcenter.security.core.consts.SecurityConstants.VALIDATE_CODE_PROCESSOR_SUFFIX;

/**
 * 校验码处理逻辑的默认实现
 * @author zyw
 * @version V1.0  Created by 2020/5/6 10:14
 */
@Slf4j
public abstract class AbstractValidateCodeProcessor implements ValidateCodeProcessor {
    /**
     * 操作 session 的工具类
     */
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    /**
     * 收集系统中所有的 {@link ValidateCodeGenerator} 接口的实现, spring 自动注入
     */
    @Autowired
    private Map<String, ValidateCodeGenerator> validateCodeGenerators;

    @Override
    public ValidateStatus produce(ServletWebRequest request) throws ValidateCodeException {
        ValidateStatus validateStatus;
        try
        {
            ValidateCode validateCode = generate(request);
            ValidateStatus status = save(request, validateCode);
            if (status.equals(ValidateStatus.FAILURE))
            {
                return ValidateStatus.FAILURE;
            }
            validateStatus = sent(request, validateCode);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new ValidateCodeException(e.getMessage(), e);
        }
        return validateStatus;
    }

    @Override
    public ValidateCode generate(ServletWebRequest request) {
        try {
            ValidateCodeGenerator validateCodeGenerator = getValidateCodeGenerator(getValidateCodeType());
            return (ValidateCode) validateCodeGenerator.generate(request.getRequest());
        }
        catch (ValidateCodeException e) {
            throw new ValidateCodeException(e.getMessage(), e);
        }
        catch (Exception e) {
            throw new ValidateCodeException("获取验证码失败，请重试", e);
        }
    }

    @Override
    public ValidateStatus save(ServletWebRequest request, ValidateCode validateCode) {
        try {
            ValidateCodeType validateCodeType = getValidateCodeType();
            if (validateCodeType == null)
            {
                return ValidateStatus.FAILURE;
            }
            sessionStrategy.setAttribute(request, validateCodeType.getSessionKey(), validateCode);
        }
        catch (Exception e) {
            log.warn(e.getMessage(), e);
            return ValidateStatus.FAILURE;
        }
        return ValidateStatus.SUCCESS;
    }

    /**
     * 发送验证码，由子类实现,
     * 发送失败，必须清除 sessionKey 的缓存，示例代码: <br>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;sessionStrategy.removeAttribute(request, sessionKey); </p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;sessionKey 获取方式：</p>
     *        <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     *        如果不清楚是哪种类型 sessionKey，用如下方式：</p>
     *        <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     *        ValidateCodeType validateCodeType = getValidateCodeType();</p>
     *        <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     *        String sessionKey = validateCodeType.getSessionKey();</p>
     * @param request   ServletWebRequest
     * @param validateCode  校验码对象
     * @return  是否发送成功的状态
     */
    @Override
    public abstract ValidateStatus sent(ServletWebRequest request, ValidateCode validateCode);

    @Override
    public void validate(ServletWebRequest request) throws ServletRequestBindingException, ValidateCodeException {
        ValidateCodeType validateCodeType = getValidateCodeType();
        String sessionKey = validateCodeType.getSessionKey();

        String requestParamValidateCodeName = getValidateCodeGenerator(validateCodeType).getRequestParamValidateCodeName();

        ValidateCode codeInSession = (ValidateCode) sessionStrategy.getAttribute(request, sessionKey);
        String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), requestParamValidateCodeName);
        if (!StringUtils.isNotBlank(codeInRequest))
        {
            throw new ValidateCodeException("验证码的值不能为空");
        }

        if (codeInSession == null)
        {
            throw new ValidateCodeException("验证码不存在");
        }

        if (codeInSession.isExpired())
        {
            sessionStrategy.removeAttribute(request, sessionKey);
            throw new ValidateCodeException("验证码已过期");
        }

        if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), codeInRequest))
        {
            throw new ValidateCodeException("验证码不匹配");
        }
        sessionStrategy.removeAttribute(request, sessionKey);

    }

    /**
     * 根据 request 获取校验码的类型,
     * 如果不存在，返回 null
     * @return  如果不存在，返回 null
     */
    private ValidateCodeType getValidateCodeType() {
        String type = StringUtils.substringBefore(getClass().getSimpleName(), VALIDATE_CODE_PROCESSOR_SUFFIX);
        if (StringUtils.isNotBlank(type))
        {
            try {
                return ValidateCodeType.valueOf(type.toUpperCase());
            }
            catch (IllegalArgumentException e) { }
        }
        return null;
    }

    /**
     * 获取校验码生成器
     * @param type
     * @return
     */
    private ValidateCodeGenerator getValidateCodeGenerator(ValidateCodeType type) {
        try {
            return validateCodeGenerators.get(type.name().toLowerCase() + VALIDATE_CODE_GENERATOR_SUFFIX);
        }
        catch (Exception e) {
            throw new ValidateCodeException("校验码类型错误", e);
        }
    }

}
