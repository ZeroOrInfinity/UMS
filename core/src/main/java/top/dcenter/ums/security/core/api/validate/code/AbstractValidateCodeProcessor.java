package top.dcenter.ums.security.core.api.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Transient;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;
import top.dcenter.ums.security.core.exception.ValidateCodeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;

import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.GET_VALIDATE_CODE_FAILURE;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.ILLEGAL_VALIDATE_CODE_TYPE;

/**
 * 验证码处理逻辑的默认实现抽象类
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/6 10:14
 * @author zyw
 */
@Slf4j
public abstract class AbstractValidateCodeProcessor implements ValidateCodeProcessor {

    protected ValidateCodeGeneratorHolder validateCodeGeneratorHolder;

    /**
     * 验证码处理逻辑的默认实现抽象类.<br><br>
     *
     * @param validateCodeGeneratorHolder   validateCodeGeneratorHolder
     */
    public AbstractValidateCodeProcessor(ValidateCodeGeneratorHolder validateCodeGeneratorHolder) {

        this.validateCodeGeneratorHolder = validateCodeGeneratorHolder;

    }

    @Override
    public final boolean produce(ServletWebRequest request) throws ValidateCodeException {

        ValidateCode validateCode;
        HttpServletRequest req = request.getRequest();
        HttpSession session = req.getSession();
        String ip = req.getRemoteAddr();
        String sid = request.getSessionId();
        String uri = req.getRequestURI();
        try
        {
            validateCode = generate(request);
            boolean validateStatus = sent(request, validateCode);
            if (!validateStatus)
            {
                log.warn("发送验证码失败: ip={}, sid={}, uri={}, validateCode={}",
                         ip, sid, uri, validateCode.toString());
                return false;
            }
            saveSession(request, validateCode);
        }
        catch (Exception e)
        {
            session.removeAttribute(getValidateCodeType().getSessionKey());
            if (e instanceof ValidateCodeException)
            {
                ValidateCodeException exception = (ValidateCodeException) e;
                String msg = String.format("生成验证码失败: error=%s, ip=%s, uid=%s, sid=%s, uri=%s, data=%s",
                                              exception.getMessage(), ip, exception.getUid(), sid, uri, exception.getData());
                log.warn(msg, exception);
                throw exception;
            }
            else
            {
                String msg = String.format("生成验证码失败: error=%s, ip=%s, sid=%s, uri=%s",
                                              e.getMessage(), ip, sid, uri);
                log.warn(msg, e);
                throw new ValidateCodeException(GET_VALIDATE_CODE_FAILURE, e, ip, uri);
            }
        }
        return true;
    }

    @Override
    public final ValidateCode generate(ServletWebRequest request) {
        try
        {
            ValidateCodeGenerator<?> validateCodeGenerator = getValidateCodeGenerator(getValidateCodeType());
            return (ValidateCode) validateCodeGenerator.generate(request.getRequest());
        }
        catch (ValidateCodeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ValidateCodeException(GET_VALIDATE_CODE_FAILURE, e, request.getRequest().getRemoteAddr(), request.getRequest().getRequestURI());
        }
    }

    @Override
    public boolean saveSession(ServletWebRequest request, ValidateCode validateCode) {


        HttpServletRequest req = request.getRequest();
        try
        {
            ValidateCodeType validateCodeType = getValidateCodeType();
            if (validateCodeType == null)
            {
                return false;
            }

            // 移除不必要的属性值(图片等)
            removeUnnecessaryFieldValue(validateCode);

            req.getSession().setAttribute(validateCodeType.getSessionKey(), validateCode);
        }
        catch (Exception e)
        {
            String msg = String.format("验证码保存到Session失败: error=%s, ip=%s, code=%s",
                                       e.getMessage(),
                                       req.getRemoteAddr(),
                                       validateCode);
            log.error(msg, e);
            return false;
        }
        return true;
    }

    /**
     * 移除不必要的属性值
     * @param validateCode  验证码
     * @throws IllegalAccessException   IllegalAccessException
     */
    private void removeUnnecessaryFieldValue(ValidateCode validateCode) throws IllegalAccessException {

        Field[] fields = validateCode.getClass().getDeclaredFields();

        for (Field field : fields)
        {
            field.setAccessible(true);
            Transient aTransient = field.getDeclaredAnnotation(Transient.class);
            if (aTransient != null)
            {
                field.set(validateCode, null);
            }
        }
    }

    /**
     * 发送验证码，由子类实现,
     * 发送失败，必须清除 sessionKey 的缓存，示例代码: <br><br>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;sessionStrategy.removeAttribute(request, sessionKey); </p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;sessionKey 获取方式：</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * 如果不清楚是哪种类型 sessionKey，用如下方式：</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * ValidateCodeType validateCodeType = getValidateCodeType();</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * String sessionKey = validateCodeType.getSessionKey();</p>
     *
     * @param request      ServletWebRequest
     * @param validateCode 验证码对象
     * @return 是否发送成功的状态
     */
    @Override
    public abstract boolean sent(ServletWebRequest request, ValidateCode validateCode);


    /**
     * 校验验证码
     * @param request   {@link ServletWebRequest}
     * @throws ValidateCodeException 验证码异常
     */
    @Override
    public void validate(ServletWebRequest request) throws ValidateCodeException {

        ValidateCodeType validateCodeType = getValidateCodeType();
        ValidateCodeGenerator<?> validateCodeGenerator = getValidateCodeGenerator(validateCodeType);
        defaultValidate(request, validateCodeGenerator.getRequestParamValidateCodeName());

    }



    /**
     * 获取验证码类型
     * @return {@link ValidateCodeType}
     */
    @Override
    public abstract ValidateCodeType getValidateCodeType();

    /**
     * 获取验证码生成器
     * @param type 验证码类型
     * @return 验证码生成器
     */
    protected ValidateCodeGenerator<?> getValidateCodeGenerator(ValidateCodeType type) throws ValidateCodeException {
        try
        {
            ValidateCodeGenerator<?> validateCodeGenerator = validateCodeGeneratorHolder.findValidateCodeGenerator(type);
            if (validateCodeGenerator != null)
            {
                return validateCodeGenerator;
            }
            throw new ValidateCodeException(ILLEGAL_VALIDATE_CODE_TYPE, null, type.name());
        }
        catch (Exception e)
        {
            throw new ValidateCodeException(ILLEGAL_VALIDATE_CODE_TYPE, e, null, type.name());
        }
    }

}
