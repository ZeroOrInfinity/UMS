package demo.test.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 测试自定义注解
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/2 14:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Constraint(validatedBy = MyConstraintValidator.class)
public @interface MyConstraint {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
