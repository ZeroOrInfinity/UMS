package top.dcenter.security.core.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.dcenter.security.core.api.validateCode.ValidateCodeGenerator;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.validate.code.imagecode.DefaultImageCodeFactory;
import top.dcenter.security.core.api.validateCode.ImageCodeFactory;
import top.dcenter.security.core.validate.code.imagecode.ImageCodeGenerator;
import top.dcenter.security.core.validate.code.imagecode.ImageValidateCodeProcessor;
import top.dcenter.security.core.validate.code.smscode.DefaultSmsCodeSender;
import top.dcenter.security.core.validate.code.smscode.SmsCodeGenerator;
import top.dcenter.security.core.api.validateCode.SmsCodeSender;
import top.dcenter.security.core.validate.code.smscode.SmsValidateCodeProcessor;

import java.util.Map;

/**
 * 校验码功能配置
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/5 0:02
 */
@Configuration
@Slf4j
public class ValidateCodeBeanConfig {

    @Bean
    @ConditionalOnMissingBean()
    public ImageCodeGenerator imageCodeGenerator(ValidateCodeProperties validateCodeProperties) {
        return new ImageCodeGenerator(validateCodeProperties, imageCodeFactory(validateCodeProperties));
    }

    @Bean
    @ConditionalOnMissingBean()
    public SmsCodeGenerator smsCodeGenerator(ValidateCodeProperties validateCodeProperties) {
        return new SmsCodeGenerator(validateCodeProperties, smsCodeSender(validateCodeProperties));
    }

    @Bean
    @ConditionalOnMissingBean(SmsCodeSender.class)
    public SmsCodeSender smsCodeSender(ValidateCodeProperties validateCodeProperties) {
        return new DefaultSmsCodeSender(validateCodeProperties);
    }

    @Bean
    @ConditionalOnMissingBean(ImageCodeFactory.class)
    public ImageCodeFactory imageCodeFactory(ValidateCodeProperties validateCodeProperties) {
        return new DefaultImageCodeFactory(validateCodeProperties);
    }

    @Bean
    @ConditionalOnMissingBean(ImageValidateCodeProcessor.class)
    public ImageValidateCodeProcessor imageValidateCodeProcessor(Map<String, ValidateCodeGenerator> validateCodeGenerators) {
        return new ImageValidateCodeProcessor(validateCodeGenerators);
    }

    @Bean
    @ConditionalOnMissingBean(SmsValidateCodeProcessor.class)
    public SmsValidateCodeProcessor smsValidateCodeProcessor(SmsCodeSender smsCodeSender,
                                                             ValidateCodeProperties validateCodeProperties,
                                                             Map<String, ValidateCodeGenerator> validateCodeGenerators) {
        return new SmsValidateCodeProcessor(smsCodeSender, validateCodeProperties, validateCodeGenerators);
    }

}
