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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import top.dcenter.ums.security.core.advice.SecurityControllerAdviceHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.api.tenant.handler.TenantContextHolder;
import top.dcenter.ums.security.core.auth.controller.ClientSecurityController;
import top.dcenter.ums.security.core.auth.handler.ClientAuthenticationFailureHandler;
import top.dcenter.ums.security.core.auth.handler.ClientAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.auth.handler.DefaultLogoutSuccessHandler;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.auth.provider.UsernamePasswordAuthenticationProvider;
import top.dcenter.ums.security.core.util.MvcUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import static top.dcenter.ums.security.core.util.MvcUtil.TOP_DOMAIN_PARAM_NAME;

/**
 * security 配置
 *
 * @author YongWu zheng
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 19:59
 */
@SuppressFBWarnings("REC_CATCH_EXCEPTION")
@Configuration(proxyBeanMethods = false)
@Order(98)
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
@Slf4j
public class SecurityAutoConfiguration implements InitializingBean {

    private final ClientProperties clientProperties;

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired
    private UmsUserDetailsService umsUserDetailsService;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private TenantContextHolder tenantContextHolder;

    public SecurityAutoConfiguration(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        /*
            默认为 BCryptPasswordEncoder 的实现了添加随机 salt 算法，并且能从hash后的字符串中获取 salt 进行原始密码与hash后的密码的对比
            支持格式:
            {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
            {noop}password
            {pbkdf2}5d923b44a6d129f3ddf3e3c8d29412723dcbde72445e8ef6bf3b508fbf17fa4ed4d6b99ca763d8dc
            {scrypt}$e0801$8bWJaSu2IKSn9Z9kM+TPXfOc/9bdYSrN1oD9qfVThWEwdRTnO7re7Ei+fUZRJ68k9lTyuTeUp4of4g24hHnazw==$OAOec05+bXxvuu/1qZ6NUR+xQYvYv7BeL1QxwRpY5Pc=
            {sha256}97cde38028ad898ebc02e690819fa220e88c62e0699403e94fff291cfffaf8410849f27605abcbc0
         */
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler")
    public BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler() {
        return new ClientAuthenticationSuccessHandler(clientProperties, null); }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler")
    public BaseAuthenticationFailureHandler baseAuthenticationFailureHandler() {
        return new ClientAuthenticationFailureHandler(clientProperties);
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.advice.SecurityControllerAdviceHandler")
    public SecurityControllerAdviceHandler securityControllerExceptionHandler() {
        return new SecurityControllerAdviceHandler();
    }

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.auth.provider.UsernamePasswordAuthenticationProvider")
    public UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider(PasswordEncoder passwordEncoder) {
        return new UsernamePasswordAuthenticationProvider(passwordEncoder, umsUserDetailsService, tenantContextHolder);
    }

    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.security.web.authentication.logout.LogoutSuccessHandler")
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new DefaultLogoutSuccessHandler(clientProperties);
    }


    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.controller.BaseSecurityController")
    @ConditionalOnProperty(prefix = "ums.client", name = "open-authentication-redirect", havingValue = "true")
    public ClientSecurityController clientSecurityController() {
        return new ClientSecurityController(this.clientProperties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // 忽略非法反射警告  适用于jdk11
        if (clientProperties.getSuppressReflectWarning())
        {
            disableAccessWarnings();
        }

        // 给 MvcUtil.topDomain 设置 TOP_DOMAIN
        Class<MvcUtil> mvcUtilClass = MvcUtil.class;
        Class.forName(mvcUtilClass.getName());
        Field[] declaredFields = mvcUtilClass.getDeclaredFields();
        for (Field field : declaredFields)
        {
            field.setAccessible(true);
            if (Objects.equals(field.getName(), TOP_DOMAIN_PARAM_NAME)) {
                field.set(null, clientProperties.getTopDomain());
            }

        }

    }

    /**
     * 忽略非法反射警告  适用于jdk11
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void disableAccessWarnings() {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);

            Method putObjectVolatile = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
            Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);

            Class loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        } catch (Exception ignored) {
            log.info("忽略非法反射警告配置失效!");
        }
    }
}