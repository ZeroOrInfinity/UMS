package top.dcenter.security.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.api.validate.code.ImageCodeFactory;
import top.dcenter.security.core.api.validate.code.SmsCodeSender;
import top.dcenter.security.core.api.validate.code.ValidateCodeGenerator;
import top.dcenter.security.core.api.validate.code.ValidateCodeProcessor;
import top.dcenter.security.core.auth.controller.ValidateCodeController;
import top.dcenter.security.core.auth.validate.codes.ValidateCodeFilter;
import top.dcenter.security.core.auth.validate.codes.ValidateCodeProcessorHolder;
import top.dcenter.security.core.auth.validate.codes.image.DefaultImageCodeFactory;
import top.dcenter.security.core.auth.validate.codes.image.ImageCodeGenerator;
import top.dcenter.security.core.auth.validate.codes.image.ImageValidateCodeProcessor;
import top.dcenter.security.core.auth.validate.codes.sms.DefaultSmsCodeSender;
import top.dcenter.security.core.auth.validate.codes.sms.SmsCodeGenerator;
import top.dcenter.security.core.auth.validate.codes.sms.SmsValidateCodeProcessor;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.util.MvcUtil;

import java.util.Map;

/**
 * 验证码功能配置
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/5 0:02
 */
@Configuration
@AutoConfigureAfter({SecurityAutoConfiguration.class})
@Slf4j
public class ValidateCodeBeanAutoConfiguration implements InitializingBean {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private GenericApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.auth.validate.codes.image.ImageCodeGenerator")
    public ImageCodeGenerator imageCodeGenerator(ValidateCodeProperties validateCodeProperties) {
        return new ImageCodeGenerator(validateCodeProperties, imageCodeFactory(validateCodeProperties));
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.auth.validate.codes.sms.SmsCodeGenerator")
    public SmsCodeGenerator smsCodeGenerator(ValidateCodeProperties validateCodeProperties, SmsCodeSender smsCodeSender) {
        return new SmsCodeGenerator(validateCodeProperties, smsCodeSender);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.api.validate.code.SmsCodeSender")
    public SmsCodeSender smsCodeSender(ValidateCodeProperties validateCodeProperties) {
        return new DefaultSmsCodeSender(validateCodeProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.validate.codes.ValidateCodeBeanAutoConfiguration.imageCodeFactory")
    public ImageCodeFactory imageCodeFactory(ValidateCodeProperties validateCodeProperties) {
        return new DefaultImageCodeFactory(validateCodeProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.auth.validate.codes.image.ImageValidateCodeProcessor")
    public ImageValidateCodeProcessor imageValidateCodeProcessor(Map<String, ValidateCodeGenerator<?>> validateCodeGenerators) {
        return new ImageValidateCodeProcessor(validateCodeGenerators);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.auth.validate.codes.sms.SmsValidateCodeProcessor")
    public SmsValidateCodeProcessor smsValidateCodeProcessor(SmsCodeSender smsCodeSender,
                                                             ValidateCodeProperties validateCodeProperties,
                                                             Map<String, ValidateCodeGenerator<?>> validateCodeGenerators) {
        return new SmsValidateCodeProcessor(smsCodeSender, validateCodeProperties, validateCodeGenerators);
    }

    @Bean
    public ValidateCodeProcessorHolder validateCodeProcessorHolder(Map<String, ValidateCodeProcessor> validateCodeProcessors) {
        return new ValidateCodeProcessorHolder(validateCodeProcessors);
    }

    @Bean
    public ValidateCodeController validateCodeController(ValidateCodeProcessorHolder validateCodeProcessorHolder) {
        return new ValidateCodeController(validateCodeProcessorHolder);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.auth.validate.codes.ValidateCodeFilter")
    public ValidateCodeFilter validateCodeFilter(ValidateCodeProcessorHolder validateCodeProcessorHolder,
                                                 BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                                 ValidateCodeProperties validateCodeProperties) {
        return new ValidateCodeFilter(validateCodeProcessorHolder, baseAuthenticationFailureHandler, validateCodeProperties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // 在 mvc 中做 Uri 映射等动作
        MvcUtil.registerController("validateCodeController", applicationContext, null);

    }

}
