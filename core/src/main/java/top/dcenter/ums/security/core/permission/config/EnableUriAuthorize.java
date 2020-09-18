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
 * 注意: 两种方式是互斥关系
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
     * @return
     */
    boolean filterOrInterceptor() default false;

    /**
     *  是否为 restful API 风格, 默认为 false. 注意: 当 filterOrInterceptor 为 true 此配置才生效<br>
     *  根据 restfulAPI 的值不同, 在用使用 UriAuthorizeFilter 时算法上有区别.
     * @return
     */
    boolean restfulAPI() default false;

    /**
     *  需要验证权限的多个不同的 uri 对同一个 uri 都匹配的情况下, 设置为 true, 默认为 false.  注意: 当 filterOrInterceptor 为 true 此配置才生效.<br>
     *  根据 repeat 的值不同, 在用使用 UriAuthorizeFilter 时算法上有区别.<br>
     *  例如: 需要验证权限的两个 uri: /test/** 和 /test/permission/** 都对 /test/permission/1 匹配, 就需要设置为 true
     * @return
     */
    boolean repeat() default false;

}
