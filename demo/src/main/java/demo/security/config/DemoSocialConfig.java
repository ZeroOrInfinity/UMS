package demo.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.security.SocialAuthenticationFilter;
import top.dcenter.security.social.config.SocialCoreConfig;
import top.dcenter.security.social.properties.SocialProperties;

/**
 * 自定义第三方授权登录核心配置 SocialCoreConfig，注意: 覆写方法 {@link #postProcess(Object)} 时一定要调用
 * <code>
 *     super.postProcess(object);
 * </code>
 * @author zyw
 * @version V1.0  Created by 2020/5/14 20:06
 */
@SuppressWarnings("jol")
@Configuration
@Slf4j
public class DemoSocialConfig extends SocialCoreConfig {

    private final SocialProperties socialProperties;
    private final AuthenticationFailureHandler clientAuthenticationFailureHandler;

    public DemoSocialConfig(SocialProperties socialProperties, SocialProperties socialProperties1, AuthenticationFailureHandler clientAuthenticationFailureHandler) {
        super(socialProperties);
        this.socialProperties = socialProperties1;
        this.clientAuthenticationFailureHandler = clientAuthenticationFailureHandler;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T postProcess(T object) {
        SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);

        filter.setFilterProcessesUrl(socialProperties.getCallbackUrl());
        filter.setSignupUrl(socialProperties.getSignUpUrl());
        filter.setDefaultFailureUrl(socialProperties.getFailureUrl());
        log.info("Demo ======>: DemoSocialConfig.postProcess");
        return (T) filter;
    }

}
