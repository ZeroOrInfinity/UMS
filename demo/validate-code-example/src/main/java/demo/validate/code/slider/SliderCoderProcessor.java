package demo.validate.code.slider;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.api.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;
import top.dcenter.ums.security.core.util.AuthenticationUtil;

import javax.servlet.http.HttpServletResponse;

/**
 * 滑块验证码处理器
 * @author zyw
 * @version V1.0  Created by 2020/9/21 23:05
 */
@Component
@Slf4j
public class SliderCoderProcessor extends AbstractValidateCodeProcessor {


    private final ObjectMapper objectMapper;
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
        return ValidateCodeType.SLIDER;
    }
}
