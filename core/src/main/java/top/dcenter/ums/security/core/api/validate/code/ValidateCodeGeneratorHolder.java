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

package top.dcenter.ums.security.core.api.validate.code;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证码生成器 holder
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/22 19:35
 */
public class ValidateCodeGeneratorHolder implements InitializingBean {

    /**
     * Map&#60;type, ValidateCodeGenerator&#62;
     */
    private Map<String, ValidateCodeGenerator<?>> validateCodeGenerators;

    @Autowired
    private GenericApplicationContext applicationContext;

    @Autowired(required = false)
    @Setter
    @Getter
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 根据 type 获取 {@link ValidateCodeGenerator}，如果不存在则返回 null
     * @param type  验证码类型
     * @return  如果 {@link ValidateCodeGenerator} 不存在则返回 null
     */
    public ValidateCodeGenerator<?> findValidateCodeGenerator(ValidateCodeType type) {
        if (type == null)
        {
            return null;
        }
        ValidateCodeGenerator<?> validateCodeGenerator;
        try {
            validateCodeGenerator = this.validateCodeGenerators.get(type.name().toLowerCase());
        }
        catch (Exception e) {
            validateCodeGenerator = null;
        }
        return validateCodeGenerator;
    }

    /**
     * 根据 type 获取 {@link ValidateCodeGenerator} 如果不存在则返回 null
     * @param type  验证码类型
     * @return  如果 {@link ValidateCodeGenerator} 不存在则返回 null
     */
    public ValidateCodeGenerator<?> findValidateCodeProcessor(String type) {
        if (type == null)
        {
            return null;
        }
        return this.validateCodeGenerators.get(type);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void afterPropertiesSet() {

        // 解决循环应用问题
        Map<String, ValidateCodeGenerator> validateCodeGeneratorMap = applicationContext.getBeansOfType(ValidateCodeGenerator.class);
        Collection<ValidateCodeGenerator> values = validateCodeGeneratorMap.values();
        validateCodeGenerators = new HashMap<>(values.size());
        for (ValidateCodeGenerator value : values)
        {
            validateCodeGenerators.put(value.getValidateCodeType().toLowerCase(), value);
        }

    }
}