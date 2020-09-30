package top.dcenter.ums.security.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.validate.code.image.ImageCodeFactory;
import top.dcenter.ums.security.core.api.validate.code.sms.SmsCodeSender;
import top.dcenter.ums.security.core.auth.controller.ValidateCodeController;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeFilter;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeProcessorHolder;
import top.dcenter.ums.security.core.auth.validate.codes.image.DefaultImageCodeFactory;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCodeGenerator;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageValidateCodeProcessor;
import top.dcenter.ums.security.core.auth.validate.codes.slider.SimpleSliderCodeFactory;
import top.dcenter.ums.security.core.api.validate.code.slider.SliderCodeFactory;
import top.dcenter.ums.security.core.auth.validate.codes.slider.SliderCoderProcessor;
import top.dcenter.ums.security.core.auth.validate.codes.slider.SliderValidateCodeGenerator;
import top.dcenter.ums.security.core.auth.validate.codes.sms.DefaultSmsCodeSender;
import top.dcenter.ums.security.core.auth.validate.codes.sms.SmsCodeGenerator;
import top.dcenter.ums.security.core.auth.validate.codes.sms.SmsValidateCodeProcessor;
import top.dcenter.ums.security.core.properties.ValidateCodeProperties;

/**
 * 验证码功能配置
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/5 0:02
 */
@Configuration
@AutoConfigureAfter({SecurityAutoConfiguration.class})
@Slf4j
public class ValidateCodeBeanAutoConfiguration {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private GenericApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.validate.codes.image.ImageCodeGenerator")
    public ImageCodeGenerator imageCodeGenerator(ValidateCodeProperties validateCodeProperties) {
        return new ImageCodeGenerator(validateCodeProperties, imageCodeFactory(validateCodeProperties));
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.validate.codes.sms.SmsCodeGenerator")
    public SmsCodeGenerator smsCodeGenerator(ValidateCodeProperties validateCodeProperties, SmsCodeSender smsCodeSender) {
        return new SmsCodeGenerator(validateCodeProperties, smsCodeSender);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.validate.code.sms.SmsCodeSender")
    public SmsCodeSender smsCodeSender(ValidateCodeProperties validateCodeProperties) {
        return new DefaultSmsCodeSender(validateCodeProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.security.core.validate.codes.ValidateCodeBeanAutoConfiguration.imageCodeFactory")
    public ImageCodeFactory imageCodeFactory(ValidateCodeProperties validateCodeProperties) {
        return new DefaultImageCodeFactory(validateCodeProperties);
    }
    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.validate.codes.image.ImageValidateCodeProcessor")
    public ImageValidateCodeProcessor imageValidateCodeProcessor(ValidateCodeGeneratorHolder validateCodeGeneratorHolder) {
        return new ImageValidateCodeProcessor(validateCodeGeneratorHolder);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.validate.codes.sms.SmsValidateCodeProcessor")
    public SmsValidateCodeProcessor smsValidateCodeProcessor(ValidateCodeGeneratorHolder validateCodeGeneratorHolder) {
        return new SmsValidateCodeProcessor(validateCodeGeneratorHolder);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.validate.code.slider.SliderCodeFactory")
    public SimpleSliderCodeFactory simpleSliderCodeFactory(ValidateCodeProperties validateCodeProperties) {
        return new SimpleSliderCodeFactory(validateCodeProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.validate.codes.slider.SliderValidateCodeGenerator")
    public SliderValidateCodeGenerator sliderValidateCodeGenerator(ValidateCodeProperties validateCodeProperties,
                                                                   SliderCodeFactory sliderCodeFactory) {
        return new SliderValidateCodeGenerator(sliderCodeFactory, validateCodeProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.validate.codes.slider.SliderCoderProcessor")
    public SliderCoderProcessor sliderCoderProcessor(ValidateCodeGeneratorHolder validateCodeGeneratorHolder,
                                                     ObjectMapper objectMapper,
                                                     ValidateCodeProperties validateCodeProperties) {
        return new SliderCoderProcessor(validateCodeGeneratorHolder, objectMapper, validateCodeProperties);
    }


    @Bean
    public ValidateCodeProcessorHolder validateCodeProcessorHolder() {
        return new ValidateCodeProcessorHolder();
    }

    @Bean
    public ValidateCodeGeneratorHolder validateCodeGeneratorHolder() {
        return new ValidateCodeGeneratorHolder();
    }

    @Bean()
    public ValidateCodeController validateCodeController() {
        return new ValidateCodeController();
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeFilter")
    public ValidateCodeFilter validateCodeFilter(ValidateCodeProcessorHolder validateCodeProcessorHolder,
                                                 BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                                 ValidateCodeProperties validateCodeProperties) {
        return new ValidateCodeFilter(validateCodeProcessorHolder, baseAuthenticationFailureHandler, validateCodeProperties);
    }

}
