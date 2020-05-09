package top.dcenter.security.core.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.enums.ValidateStatus;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.validate.code.imagecode.ImageCodeGenerator;
import top.dcenter.security.core.validate.code.imagecode.ImageValidateCodeProcessor;
import top.dcenter.security.core.validate.code.smscode.DefaultSmsCodeSender;
import top.dcenter.security.core.validate.code.smscode.SmsCodeGenerator;
import top.dcenter.security.core.validate.code.smscode.SmsCodeSender;
import top.dcenter.security.core.validate.code.smscode.SmsValidateCodeProcessor;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/5 0:02
 */
@Configuration
@Slf4j
public class ValidateCodeBeanConfig {

    @Bean
    @ConditionalOnMissingBean(name = "imageCodeGenerator")
    public ImageCodeGenerator imageCodeGenerator(ValidateCodeProperties validateCodeProperties) {
        return new ImageCodeGenerator(validateCodeProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "smsCodeGenerator")
    public SmsCodeGenerator smsCodeGenerator(ValidateCodeProperties validateCodeProperties) {
        return new SmsCodeGenerator(validateCodeProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SmsCodeSender.class)
    public SmsCodeSender smsCodeSender() {
        return new DefaultSmsCodeSender();
    }

    @Bean
    @ConditionalOnMissingBean(ValidateCodeProcessor.class)
    public ValidateCodeProcessor validateCodeProcessor() {

        return new AbstractValidateCodeProcessor() {
            @Override
            public ValidateStatus sent(ServletWebRequest request, ValidateCode validateCode) {
                // 默认为图片验证码，所以不做任何处理。
                return ValidateStatus.SUCCESS;
            }
        };
    }
    @Bean
    @ConditionalOnMissingBean(ImageValidateCodeProcessor.class)
    public ImageValidateCodeProcessor imageValidateCodeProcessor() {
        return new ImageValidateCodeProcessor();
    }

    @Bean
    @ConditionalOnMissingBean(SmsValidateCodeProcessor.class)
    public SmsValidateCodeProcessor smsValidateCodeProcessor(SmsCodeSender smsCodeSender, ValidateCodeProperties validateCodeProperties) {
        return new SmsValidateCodeProcessor(smsCodeSender, validateCodeProperties);
    }


}
