package top.dcenter.security.core.authentication.mobile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.security.core.validate.code.ValidateCodeProcessorHolder;

/**
 * 手机登录控制器
 * @author zyw
 * @version V1.0  Created by 2020/5/15 0:18
 */
@RestController
@Slf4j
public class SmsCodeAuthenticationController {

    private final ValidateCodeProcessorHolder validateCodeProcessorHolder;

    public SmsCodeAuthenticationController(ValidateCodeProcessorHolder validateCodeProcessorHolder) {
        this.validateCodeProcessorHolder = validateCodeProcessorHolder;
    }

//    /**
//     * 手机登录
//     * @param request
//     * @param response
//     */
//    @PostMapping(DEFAULT_LOGIN_PROCESSING_URL_MOBILE)
//    public void createCode(HttpServletRequest request, HttpServletResponse response) {
//
//        ValidateCodeProcessor validateCodeProcessor;
//        if (validateCodeProcessorHolder != null)
//        {
//            validateCodeProcessor =
//                    validateCodeProcessorHolder.findValidateCodeProcessor(ValidateCodeType.SMS.name().toLowerCase());
//        } else {
//            validateCodeProcessor = null;
//        }
//        if (validateCodeProcessor == null)
//        {
//            throw new ValidateCodeException("非法的校验码类型");
//        }
//
//        boolean validateStatus = validateCodeProcessor.produce(new ServletWebRequest(request, response));
//
//        if (!validateStatus)
//        {
//            throw new ValidateCodeProcessException("获取验证码失败，请重试！");
//        }
//
//    }
}
