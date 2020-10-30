/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package demo.validate.code.slider;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.api.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.ums.security.core.api.validate.code.ValidateCode;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeType;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.util.AuthenticationUtil;
import top.dcenter.ums.security.core.util.ValidateCodeUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_EXPIRED;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_FAILURE;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_NOT_EMPTY;

/**
 * 滑块验证码处理器
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/21 23:05
 */
@Component("demoSliderCoderProcessor")
@Slf4j
public class SliderCoderProcessor extends AbstractValidateCodeProcessor {


    public static final String TOKEN_REQUEST_PARAM_NAME = "sliderToken";
    public static final String X_REQUEST_PARAM_NAME = "x";
    public static final String Y_REQUEST_PARAM_NAME = "y";

    private final ObjectMapper objectMapper;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private ValidateCodeProperties validateCodeProperties;

    /**
     * 验证码处理逻辑的默认实现抽象类.<br><br>
     *
     * @param validateCodeGeneratorHolder validateCodeGeneratorHolder
     * @param objectMapper  objectMapper
     */
    public SliderCoderProcessor(ValidateCodeGeneratorHolder validateCodeGeneratorHolder, ObjectMapper objectMapper) {
        super(validateCodeGeneratorHolder);
        this.objectMapper = objectMapper;
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
            log.info("Demo ========>: sliderCode = {}", resultJson);
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
        return ValidateCodeType.CUSTOMIZE;
    }

    @SuppressWarnings({"ConstantConditions", "AlibabaLowerCamelCaseVariableNaming"})
    @Override
    public void validate(ServletWebRequest request) throws ValidateCodeException {
        // 获取 session 中的 SliderCode
        HttpServletRequest req = request.getRequest();
        ValidateCodeType sliderType = ValidateCodeType.CUSTOMIZE;
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
            defaultValidate(request, TOKEN_REQUEST_PARAM_NAME);
            return;
        }

        // 获取 request 中的验证码
        String token = request.getParameter(TOKEN_REQUEST_PARAM_NAME);
        String x = request.getParameter(X_REQUEST_PARAM_NAME);
        String y = request.getParameter(Y_REQUEST_PARAM_NAME);

        // 校验参数是否有效
        checkParam(sessionKey, session, req, !StringUtils.hasText(token), VALIDATE_CODE_NOT_EMPTY, TOKEN_REQUEST_PARAM_NAME);
        checkParam(sessionKey, session, req, !StringUtils.hasText(x), VALIDATE_CODE_NOT_EMPTY, X_REQUEST_PARAM_NAME);
        checkParam(sessionKey, session, req, !StringUtils.hasText(y), VALIDATE_CODE_NOT_EMPTY, Y_REQUEST_PARAM_NAME);

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