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
 * 拦截器模式, 在方法上注解 {@link UriAuthorize} 或 {@link org.springframework.security.access.prepost.PreAuthorize} 方式进行
 * uri 权限控制.<br>
 * 注意: <br>
 *     1. 拦截器模式也可以使用
 *     <pre>
 *         &#64;PreAuthorize("hasPermission('/users', '/users:list')")
 *         // equivalent to
 *         &#64;UriAuthorize("/users:list")
 *     </pre>
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

}
