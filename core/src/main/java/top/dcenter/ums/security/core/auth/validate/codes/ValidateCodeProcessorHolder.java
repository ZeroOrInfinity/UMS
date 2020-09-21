package top.dcenter.ums.security.core.auth.validate.codes;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeProcessor;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 验证码处理器 Holder
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/7 10:32
 */
public class ValidateCodeProcessorHolder implements InitializingBean {

    private Map<String, ValidateCodeProcessor> validateCodeProcessors;

    @Autowired
    private GenericApplicationContext applicationContext;


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
        ValidateCodeProcessor validateCodeProcessor;
        try {
            validateCodeProcessor = this.validateCodeProcessors.get(type.name().toLowerCase());
        }
        catch (Exception e) {
            validateCodeProcessor = null;
        }
        return validateCodeProcessor;
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

    @Override
    public void afterPropertiesSet() {

        // 解决循环应用问题
        Map<String, ValidateCodeProcessor> validateCodeProcessorMap = applicationContext.getBeansOfType(ValidateCodeProcessor.class);
        Collection<ValidateCodeProcessor> values = validateCodeProcessorMap.values();
        validateCodeProcessors =
                values.stream().collect(Collectors.toMap(validateCodeProcessor -> validateCodeProcessor.getValidateCodeType().name().toLowerCase(),
                                                         validateCodeProcessor -> validateCodeProcessor));

    }
}
