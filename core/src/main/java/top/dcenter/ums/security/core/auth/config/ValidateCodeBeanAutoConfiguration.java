/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeProcessorHolder;
import top.dcenter.ums.security.core.api.validate.code.image.ImageCodeFactory;
import top.dcenter.ums.security.core.api.validate.code.slider.SliderCodeFactory;
import top.dcenter.ums.security.core.api.validate.code.sms.SmsCodeSender;
import top.dcenter.ums.security.core.auth.controller.ValidateCodeController;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeFilter;
import top.dcenter.ums.security.core.auth.validate.codes.image.DefaultImageCodeFactory;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCodeGenerator;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageValidateCodeProcessor;
import top.dcenter.ums.security.core.auth.validate.codes.job.RefreshValidateCodeCacheJobHandler;
import top.dcenter.ums.security.core.auth.validate.codes.slider.SimpleSliderCodeFactory;
import top.dcenter.ums.security.core.auth.validate.codes.slider.SliderCoderProcessor;
import top.dcenter.ums.security.core.auth.validate.codes.slider.SliderValidateCodeGenerator;
import top.dcenter.ums.security.core.auth.validate.codes.sms.DefaultSmsCodeSender;
import top.dcenter.ums.security.core.auth.validate.codes.sms.SmsCodeGenerator;
import top.dcenter.ums.security.core.auth.validate.codes.sms.SmsValidateCodeProcessor;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 验证码功能配置
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/5 0:02
 */
@Configuration
@AutoConfigureAfter({SecurityAutoConfiguration.class})
@Slf4j
public class ValidateCodeBeanAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.validate.codes.image.ImageCodeGenerator")
    public ImageCodeGenerator imageCodeGenerator(ValidateCodeProperties validateCodeProperties,
                                                 ImageCodeFactory imageCodeFactory) {
        return new ImageCodeGenerator(validateCodeProperties, imageCodeFactory);
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
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.validate.code.image.ImageCodeFactory")
    public ImageCodeFactory imageCodeFactory(ValidateCodeProperties validateCodeProperties) {
        return new DefaultImageCodeFactory(validateCodeProperties);
    }
    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.validate.codes.image.ImageValidateCodeProcessor")
    public ImageValidateCodeProcessor imageValidateCodeProcessor(@NonNull ValidateCodeGeneratorHolder validateCodeGeneratorHolder,
                                                                 @NonNull ValidateCodeProperties validateCodeProperties,
                                                                 @Nullable @Autowired(required = false) StringRedisTemplate stringRedisTemplate) {
        return new ImageValidateCodeProcessor(validateCodeGeneratorHolder,
                                              validateCodeProperties.getValidateCodeCacheType() ,
                                              stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.validate.codes.sms.SmsValidateCodeProcessor")
    public SmsValidateCodeProcessor smsValidateCodeProcessor(@NonNull ValidateCodeGeneratorHolder validateCodeGeneratorHolder,
                                                             @NonNull ValidateCodeProperties validateCodeProperties,
                                                             @Nullable @Autowired(required = false) StringRedisTemplate stringRedisTemplate) {
        return new SmsValidateCodeProcessor(validateCodeGeneratorHolder,
                                            validateCodeProperties.getValidateCodeCacheType() ,
                                            stringRedisTemplate);
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
    public SliderCoderProcessor sliderCoderProcessor(@NonNull ValidateCodeGeneratorHolder validateCodeGeneratorHolder,
                                                     @NonNull ValidateCodeProperties validateCodeProperties,
                                                     @Nullable @Autowired(required = false) StringRedisTemplate stringRedisTemplate) {
        return new SliderCoderProcessor(validateCodeGeneratorHolder,
                                        validateCodeProperties,
                                        stringRedisTemplate);
    }

    @Bean
    @ConditionalOnProperty(prefix = "ums.codes", name = "enable-refresh-validate-code-job", havingValue = "true")
    public RefreshValidateCodeCacheJobHandler refreshValidateCodeJobHandler(ValidateCodeProperties validateCodeProperties,
                                                                            @Qualifier("jobTaskScheduledExecutor") ScheduledExecutorService jobTaskScheduledExecutor) {

        return new RefreshValidateCodeCacheJobHandler(validateCodeProperties, jobTaskScheduledExecutor);
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