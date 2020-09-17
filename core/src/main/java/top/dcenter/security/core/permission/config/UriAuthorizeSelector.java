package top.dcenter.security.core.permission.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 根据 {@link EnableUriAuthorize} 的配置选项去动态添加 uri 权限控制方式(filter 或 interceptor(方法添加注释)).
 * @author zyw
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


        boolean filterOrInterceptor = attributes.getBoolean("filterOrInterceptor");
        boolean restfulAPI = attributes.getBoolean("restfulAPI");
        boolean repeat = attributes.getBoolean("repeat");

        List<String> classNames = new ArrayList<>(2);
        if (filterOrInterceptor)
        {
            if (restfulAPI)
            {
                classNames.add(RestfulAPI.class.getName());
            }

            if (repeat)
            {
                classNames.add(Repeat.class.getName());
            }

            classNames.add(UriAuthorizeFilterAutoConfiguration.class.getName());
        }
        else
        {
            classNames.add(UriAuthorizeInterceptorAutoConfiguration.class.getName());
            classNames.add(UriAuthorizeWebMvcAutoConfigurer.class.getName());
        }

        return classNames.toArray(new String[0]);
    }
}
