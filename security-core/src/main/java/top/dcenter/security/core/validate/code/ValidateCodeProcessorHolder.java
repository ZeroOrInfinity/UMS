package top.dcenter.security.core.validate.code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.excception.ValidateCodeException;

import java.util.Map;

/**
 * 校验码处理器 Holder
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/7 10:32
 */
@Component
public class ValidateCodeProcessorHolder {


    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private Map<String, ValidateCodeProcessor> validateCodeProcessors;



    /**
     * 根据 type 获取 {@link ValidateCodeProcessor}，如果不存在抛出异常 ValidateCodeException
     * @param type  验证码类型
     * @return  如果 {@link ValidateCodeProcessor} 如果不存在异常 ValidateCodeException
     * @throws ValidateCodeException
     */
    public ValidateCodeProcessor findValidateCodeProcessor(ValidateCodeType type) throws ValidateCodeException {
        return findValidateCodeProcessor(type.toString().toLowerCase());
    }

    /**
     * 根据 type 获取 {@link ValidateCodeProcessor} 如果不存在抛出异常 ValidateCodeException
     * @param type  验证码类型
     * @return  如果 {@link ValidateCodeProcessor} 不存在抛出异常 ValidateCodeException
     * @throws ValidateCodeException
     */
    public ValidateCodeProcessor findValidateCodeProcessor(String type) throws ValidateCodeException {
        String name = type.toLowerCase() + ValidateCodeProcessor.class.getSimpleName();
        ValidateCodeProcessor processor;
        if (validateCodeProcessors != null)
        {
            processor = validateCodeProcessors.get(name);

        } else {
            processor = null;
        }
        if (processor == null) {
            throw new ValidateCodeException("验证码处理器: " + name + " 不存在");
        }
        return processor;
    }
}
