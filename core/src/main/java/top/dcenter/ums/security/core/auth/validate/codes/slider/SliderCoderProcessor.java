package top.dcenter.ums.security.core.auth.validate.codes.slider;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.api.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.util.AuthenticationUtil;
import top.dcenter.ums.security.core.util.ValidateCodeUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_EXPIRED;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_FAILURE;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_NOT_EMPTY;

/**
 * 滑块验证码处理器, 自定义处理器请继承此类且注入 IOC 容器即可
 * @author zyw
 * @version V1.0  Created by 2020/9/21 23:05
 */
@Slf4j
public class SliderCoderProcessor extends AbstractValidateCodeProcessor {

    private final ObjectMapper objectMapper;
    private final ValidateCodeProperties validateCodeProperties;
    /**
     * 验证码处理逻辑的默认实现抽象类.<br><br>
     *  @param validateCodeGeneratorHolder validateCodeGeneratorHolder
     * @param objectMapper  objectMapper
     * @param validateCodeProperties    validateCodeProperties
     */
    public SliderCoderProcessor(ValidateCodeGeneratorHolder validateCodeGeneratorHolder,
                                ObjectMapper objectMapper, ValidateCodeProperties validateCodeProperties) {
        super(validateCodeGeneratorHolder);
        this.objectMapper = objectMapper;
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public boolean sent(ServletWebRequest request, ValidateCode validateCode) {
        try
        {
            if (!(validateCode instanceof SliderCode))
            {
                return false;
            }
            SliderCode sliderCode = (SliderCode) validateCode;

            HttpServletResponse response = request.getResponse();
            if (response == null)
            {
                return false;
            }
            String resultJson = objectMapper.writeValueAsString(sliderCode);
            AuthenticationUtil.responseWithJson(response, HttpStatus.OK.value(), resultJson);
            if (log.isDebugEnabled())
            {
                log.debug("发送滑块验证码: sliderCode = {}", sliderCode.toString());
            }
            return true;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public ValidateCodeType getValidateCodeType() {
        return ValidateCodeType.SLIDER;
    }


    @SuppressWarnings({"ConstantConditions", "AlibabaLowerCamelCaseVariableNaming"})
    @Override
    public void validate(ServletWebRequest request) throws ValidateCodeException {
        // 获取 session 中的 SliderCode
        HttpServletRequest req = request.getRequest();
        ValidateCodeType sliderType = ValidateCodeType.SLIDER;
        String sessionKey = sliderType.getSessionKey();
        HttpSession session = req.getSession();
        SliderCode sliderCodeInSession = (SliderCode) session.getAttribute(sessionKey);

        // 检查 session 是否有值
        if (sliderCodeInSession == null)
        {
            throw new ValidateCodeException(VALIDATE_CODE_EXPIRED, req.getRemoteAddr(), request.getSessionId());
        }

        // 检测是否是第二此校验
        if (sliderCodeInSession.getSecondCheck())
        {
            defaultValidate(request, validateCodeProperties.getSlider().getTokenRequestParamName());
            return;
        }

        ValidateCodeProperties.SliderCodeProperties slider = validateCodeProperties.getSlider();
        String tokenRequestParamName = slider.getTokenRequestParamName();
        String xRequestParamName = slider.getXRequestParamName();
        String yRequestParamName = slider.getYRequestParamName();

        // 获取 request 中的验证码
        String token = request.getParameter(tokenRequestParamName);
        String x = request.getParameter(xRequestParamName);
        String y = request.getParameter(yRequestParamName);

        // 校验参数是否有效
        checkParam(sessionKey, session, req, !StringUtils.isNotBlank(token), VALIDATE_CODE_NOT_EMPTY, tokenRequestParamName);
        checkParam(sessionKey, session, req, !StringUtils.isNotBlank(x), VALIDATE_CODE_NOT_EMPTY, xRequestParamName);
        checkParam(sessionKey, session, req, !StringUtils.isNotBlank(y), VALIDATE_CODE_NOT_EMPTY, yRequestParamName);

        token = token.trim();
        Integer locationX = Integer.parseInt(x);
        Integer locationY = Integer.parseInt(y);


        // 校验是否过期
        checkParam(sessionKey, session, req, sliderCodeInSession.isExpired(), VALIDATE_CODE_EXPIRED, token);

        // 验证码校验
        boolean verify = sliderCodeInSession.getLocationY().equals(locationY) && Math.abs(sliderCodeInSession.getLocationX() - locationX) < 2;
        if (!verify)
        {
            if (!sliderCodeInSession.getReuse())
            {
                session.removeAttribute(sessionKey);
            }
            throw new ValidateCodeException(VALIDATE_CODE_FAILURE, req.getRemoteAddr(), token);
        }

        // 更新 session 中的验证码信息, 以便于第二次校验
        sliderCodeInSession.setSecondCheck(true);
        // 方便二次校验时, 调用 ValidateCodeGenerator.defaultValidate 方法.
        sliderCodeInSession.setCode(ValidateCodeUtil.getUUID());
        // 这里第一次校验通过, 第二次校验不需要使用复用功能, 不然第二次校验时不会清除 session 中的验证码缓存
        sliderCodeInSession.setReuse(false);

        session.setAttribute(sessionKey, sliderCodeInSession);

    }

    /**
     * 根据 condition 是否删除 session 指定 sessionKey 缓存, 并抛出异常
     * @param sessionKey        sessionKey
     * @param session           session
     * @param req               req
     * @param condition         condition
     * @param errorCodeEnum     errorCodeEnum
     * @param errorData         errorData
     * @throws ValidateCodeException ValidateCodeException
     */
    private void checkParam(String sessionKey, HttpSession session, HttpServletRequest req, boolean condition,
                            ErrorCodeEnum errorCodeEnum, String errorData) throws ValidateCodeException {
        if (condition)
        {
            // 按照逻辑是前端过滤无效参数, 如果进入此逻辑, 按非正常访问处理
            session.removeAttribute(sessionKey);
            throw new ValidateCodeException(errorCodeEnum, req.getRemoteAddr(), errorData);
        }
    }
}
