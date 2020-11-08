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

package top.dcenter.ums.security.core.permission.annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import top.dcenter.ums.security.core.permission.config.UriAuthorizeInterceptorAutoConfiguration;
import top.dcenter.ums.security.core.permission.config.UriAuthorizeWebMvcAutoConfigurer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 根据 {@link EnableUriAuthorize} 的配置加载器.
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/17 10:42
 */
final class UriAuthorizeSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Class<EnableUriAuthorize> annoType = EnableUriAuthorize.class;
        Map<String, Object> annotationAttributes = importingClassMetadata
                .getAnnotationAttributes(annoType.getName(), false);
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(annotationAttributes);
        Assert.notNull(attributes, () -> String.format(
                "@%s is not present on importing class '%s' as expected",
                annoType.getSimpleName(), importingClassMetadata.getClassName()));


        List<String> classNames = new ArrayList<>(2);

        classNames.add(UriAuthorizeInterceptorAutoConfiguration.class.getName());
        classNames.add(UriAuthorizeWebMvcAutoConfigurer.class.getName());

        return classNames.toArray(new String[0]);
    }
}