package top.dcenter.ums.security.core.sign.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用签到功能
 * @author zyw
 * @version V1.0  Created by 2020/9/16 18:52
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({SignAutoConfiguration.class})
public @interface EnableSign {
}
