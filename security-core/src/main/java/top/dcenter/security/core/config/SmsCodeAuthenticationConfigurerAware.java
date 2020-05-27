package top.dcenter.security.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.api.config.SocialWebSecurityConfigurerAware;

import java.util.Map;
import java.util.Set;

/**
 * 手机登录配置
 * @author zyw
 * @version V1.0  Created by 2020/5/15 21:51
 */
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "security.smsCodeLogin", name = "sms-code-login-is-open", havingValue = "true")
public class SmsCodeAuthenticationConfigurerAware implements SocialWebSecurityConfigurerAware {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private SmsCodeAuthenticationConfig smsCodeAuthenticationConfig;

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // 短信验证码登录配置
        if (smsCodeAuthenticationConfig != null)
        {
            http.apply(smsCodeAuthenticationConfig);
        }
    }

    @Override
    public Map<String, Set<String>> getAuthorizeRequestMap() {
        return null;
    }
}
