package top.dcenter.security.core.permission.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying a method access-control value which will be evaluated to
 * decide whether a method invocation is allowed or not.
 * @author zyw
 * @version V1.0  Created by 2020/9/9 21:01
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface UriAuthorize {
    /**
     * @return permission for uri
     */
    String value() default "";
}
