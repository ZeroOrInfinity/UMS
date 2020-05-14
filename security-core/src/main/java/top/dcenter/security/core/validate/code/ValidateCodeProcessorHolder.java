package top.dcenter.security.core.validate.code;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 校验码处理器 Holder
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/7 10:32
 */
@Component
public class ValidateCodeProcessorHolder {


    private final Map<String, ValidateCodeProcessor> validateCodeProcessors;

    public ValidateCodeProcessorHolder(Map<String, ValidateCodeProcessor> validateCodeProcessors) {
        if (validateCodeProcessors == null)
        {
            this.validateCodeProcessors = new HashMap<>(0);
            return;
        }
        Collection<ValidateCodeProcessor> values = validateCodeProcessors.values();
        this.validateCodeProcessors =
                values.stream().collect(Collectors.toMap((validateCodeProcessor -> validateCodeProcessor.getValidateCodeType()),
                                                         validateCodeProcessor -> validateCodeProcessor));
    }


    /**
     * 根据 type 获取 {@link ValidateCodeProcessor}，如果不存在则返回 null
     * @param type  验证码类型
     * @return  如果 {@link ValidateCodeProcessor} 不存在则返回 null
     */
    public ValidateCodeProcessor findValidateCodeProcessor(ValidateCodeType type) {
        if (type == null)
        {
            return null;
        }
        return this.validateCodeProcessors.get(type.name().toLowerCase());
    }

    /**
     * 根据 type 获取 {@link ValidateCodeProcessor} 如果不存在则返回 null
     * @param type  验证码类型
     * @return  如果 {@link ValidateCodeProcessor} 不存在则返回 null
     */
    public ValidateCodeProcessor findValidateCodeProcessor(String type) {
        if (type == null)
        {
            return null;
        }
        return this.validateCodeProcessors.get(type);
    }
}
