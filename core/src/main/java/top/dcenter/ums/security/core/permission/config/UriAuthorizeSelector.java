package top.dcenter.ums.security.core.permission.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 根据 {@link EnableUriAuthorize} 的配置选项去动态添加 uri 权限控制方式(filter 或 interceptor(方法添加注解)).
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

        List<String> classNames = new ArrayList<>(3);
        classNames.add(UpdateRolesAuthoritiesListenerAutoConfiguration.class.getName());
        if (filterOrInterceptor)
        {
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
