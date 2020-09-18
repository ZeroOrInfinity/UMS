package top.dcenter.ums.security.core.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeProcessor;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeProcessorHolder;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.exception.ValidateCodeProcessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.GET_VALIDATE_CODE_FAILURE;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.ILLEGAL_VALIDATE_CODE_TYPE;


/**
 * 验证码 控制器
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/3 23:41
 */
@Slf4j
@ResponseBody
public class ValidateCodeController implements InitializingBean {

    private ValidateCodeProcessorHolder validateCodeProcessorHolder;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private GenericApplicationContext applicationContext;

    /**
     * 获取图片验证码, 根据验证码类型不同，调用不同的 {@link ValidateCodeProcessor} 接口实现
     * @param request request 中的 width 的值如果小于 height * 45 / 10, 则 width = height * 45 / 10
     * @param response  {@link HttpServletResponse}
     */
    @GetMapping("/code/{type}")
    public void createCode(@PathVariable("type") String type,
                                     HttpServletRequest request, HttpServletResponse response) {

        ValidateCodeProcessor validateCodeProcessor;
        if (validateCodeProcessorHolder != null)
        {
            validateCodeProcessor = validateCodeProcessorHolder.findValidateCodeProcessor(type);
        } else {
            validateCodeProcessor = null;
        }

        String ip = request.getRemoteAddr();
        String errorMsg = ILLEGAL_VALIDATE_CODE_TYPE.getMsg();
        if (validateCodeProcessor == null)
        {
            log.warn("创建验证码错误: error={}, ip={}, type={}", errorMsg, ip, type);
            throw new ValidateCodeException(ILLEGAL_VALIDATE_CODE_TYPE, ip, type);
        }

        boolean validateStatus = validateCodeProcessor.produce(new ServletWebRequest(request, response));

        if (!validateStatus)
        {
            log.warn("发送验证码失败: error={}, ip={}, type={}", errorMsg, ip, type);
            throw new ValidateCodeProcessException(GET_VALIDATE_CODE_FAILURE, ip, type);
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // 解决循环应用问题
        ValidateCodeProcessorHolder holder = applicationContext.getBean(ValidateCodeProcessorHolder.class);
        this.validateCodeProcessorHolder = holder;

    }
}
