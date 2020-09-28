package top.dcenter.ums.security.core.permission.config;

import org.springframework.context.annotation.Import;
import top.dcenter.ums.security.core.permission.annotation.UriAuthorize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用访问权限控制. <br><br>
 * 1. 拦截器模式, 在方法上注解 {@link UriAuthorize} 或 {@link org.springframework.security.access.prepost.PreAuthorize} 方式进行
 * uri
 * 权限控制.<br>
 * 2. 过滤器模式, 通过 Filter 进行 uri 权限控制, 默认关闭.<br><br>
 * 注意: <br>
 *     1. 两种方式是互斥关系.<br>
 *     2. 过滤器方式必须 uri 与 权限是一对一关系, 也就是说不适合 restful 风格的 API.
 *     3. resetFul 风格的 api 适合拦截器模式, 不适用过滤器模式
 *
 * @author zyw
 * @version V1.0  Created by 2020/9/16 18:52
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({UriAuthorizeSelector.class})
public @interface EnableUriAuthorize {

    /**
     * 通过添加过滤器或拦截器, 进行 uri 权限控制. false 时拦截器模式打开, 过滤器模式关闭; true 时过滤器模式打开, 拦截器模式关闭; 默认 false. <br><br>
     * 拦截器模式, 在方法上注解 {@link UriAuthorize} 或 {@link org.springframework.security.access.prepost.PreAuthorize} 方式进行 uri 权限控制
     * @return  boolean
     */
    boolean filterOrInterceptor() default false;

}
