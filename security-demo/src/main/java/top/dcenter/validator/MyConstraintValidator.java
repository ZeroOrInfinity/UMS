package top.dcenter.validator;

import top.dcenter.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义注解验证器
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/2 14:36
 */
@Slf4j
public class MyConstraintValidator implements ConstraintValidator<MyConstraint, String> {

   @Autowired
   private HelloService helloService;
   @Override
   public void initialize(MyConstraint constraint) {
      log.info("MyConstraintValidator initialize");
   }

   @Override
   public boolean isValid(String value, ConstraintValidatorContext context) {
      helloService.greeting(value);
      return StringUtils.isNotBlank(value);
   }
}
