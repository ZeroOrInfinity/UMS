package top.dcenter.security.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.api.validate.code.ValidateCodeProcessor;
import top.dcenter.security.core.exception.ValidateCodeException;
import top.dcenter.security.core.exception.ValidateCodeProcessException;
import top.dcenter.security.core.validate.code.ValidateCodeProcessorHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX;
import static top.dcenter.security.core.enums.ErrorCodeEnum.GET_VALIDATE_CODE_FAILURE;
import static top.dcenter.security.core.enums.ErrorCodeEnum.ILLEGAL_VALIDATE_CODE_TYPE;


/**
 * 验证码 控制器
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 23:41
 */
@RestController
@Slf4j
public class ValidateCodeController {

    private final ValidateCodeProcessorHolder validateCodeProcessorHolder;

    public ValidateCodeController(ValidateCodeProcessorHolder validateCodeProcessorHolder) {
        this.validateCodeProcessorHolder = validateCodeProcessorHolder;
    }


    /**
     * 获取图片验证码, 根据验证码类型不同，调用不同的 {@link ValidateCodeProcessor} 接口实现
     * @param request request 中的 width 的值如果小于 height * 45 / 10, 则 width = height * 45 / 10
     * @param response  {@link HttpServletResponse}
     */
    @GetMapping(DEFAULT_VALIDATE_CODE_URL_PREFIX + "/{type}")
    public void createCode(@PathVariable("type") String type,
                                     HttpServletRequest request, HttpServletResponse response) {

        ValidateCodeProcessor validateCodeProcessor;
        if (validateCodeProcessorHolder != null)
        {
            validateCodeProcessor = validateCodeProcessorHolder.findValidateCodeProcessor(type);
        } else {
            validateCodeProcessor = null;
        }
        if (validateCodeProcessor == null)
        {
            throw new ValidateCodeException(ILLEGAL_VALIDATE_CODE_TYPE);
        }

        boolean validateStatus = validateCodeProcessor.produce(new ServletWebRequest(request, response));

        if (!validateStatus)
        {
            throw new ValidateCodeProcessException(GET_VALIDATE_CODE_FAILURE);
        }

    }

}
