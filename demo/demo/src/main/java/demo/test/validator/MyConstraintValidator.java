package demo.test.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义注解验证器
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/2 14:36
 */
@Slf4j
public class MyConstraintValidator implements ConstraintValidator<MyConstraint, String> {

   @Override
   public void initialize(MyConstraint constraint) {
      log.info("MyConstraintValidator initialize");
   }

   @Override
   public boolean isValid(String value, ConstraintValidatorContext context) {
      return StringUtils.isNotBlank(value);
   }
}
