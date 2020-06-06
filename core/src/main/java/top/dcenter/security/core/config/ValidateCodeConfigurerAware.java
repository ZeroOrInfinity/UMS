package top.dcenter.security.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import top.dcenter.security.core.api.config.WebSecurityConfigurerAware;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.auth.validate.codes.ValidateCodeFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX;

/**
 * 验证码相关配置
 * @author zyw
 * @version V1.0  Created by 2020/5/15 21:59
 */
@Configuration
@AutoConfigureAfter({SmsCodeLoginAuthenticationConfig.class, ValidateCodeBeanConfiguration.class})
@Slf4j
public class ValidateCodeConfigurerAware implements WebSecurityConfigurerAware {

    private final ValidateCodeProperties validateCodeProperties;
    private final ValidateCodeFilter validateCodeFilter;

    public ValidateCodeConfigurerAware(ValidateCodeProperties validateCodeProperties,
                                       ValidateCodeFilter validateCodeFilter) {
        this.validateCodeProperties = validateCodeProperties;
        this.validateCodeFilter = validateCodeFilter;
    }

    @Override
    public void postConfigure(HttpSecurity http) {
        http.addFilterBefore(validateCodeFilter, AbstractPreAuthenticatedProcessingFilter.class);
    }

    @Override
    public void preConfigure(HttpSecurity http) {
        // do nothing

    }

    @Override
    public Map<String, Set<String>> getAuthorizeRequestMap() {
        Set<String> permitAllSet = new HashSet<>();
        permitAllSet.add(DEFAULT_VALIDATE_CODE_URL_PREFIX + "/*");
        permitAllSet.addAll(validateCodeProperties.getSms().getAuthUrls());
        permitAllSet.addAll(validateCodeProperties.getImage().getAuthUrls());
        Map<String, Set<String>> permitAllMap = new HashMap<>(1);
        permitAllMap.put(permitAll, permitAllSet);
        return permitAllMap;
    }
}
