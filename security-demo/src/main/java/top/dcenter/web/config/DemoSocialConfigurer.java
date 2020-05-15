package top.dcenter.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.security.SocialAuthenticationFilter;
import top.dcenter.security.social.SocialCoreConfigurer;
import top.dcenter.security.social.SocialProperties;

/**
 * 自定义第三方授权登录核心配置 SocialCoreConfigurer，
 * @author zyw
 * @version V1.0  Created by 2020/5/14 20:06
 */
@Configuration
@Slf4j
public class DemoSocialConfigurer extends SocialCoreConfigurer {

    private final SocialProperties socialProperties;
    private final AuthenticationFailureHandler browserAuthenticationFailureHandler;

    public DemoSocialConfigurer(SocialProperties socialProperties, SocialProperties socialProperties1, AuthenticationFailureHandler browserAuthenticationFailureHandler) {
        super(socialProperties);
        this.socialProperties = socialProperties1;
        this.browserAuthenticationFailureHandler = browserAuthenticationFailureHandler;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T postProcess(T object) {
        SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
        filter.setFilterProcessesUrl(socialProperties.getFilterProcessesUrl());
        filter.setSignupUrl(socialProperties.getSignUpUrl());
        // 不能乱添加失败处理器，social 第三方授权登录功能异常
        //filter.setAuthenticationFailureHandler(browserAuthenticationFailureHandler);
        // 要添加失败处理器。
        //filter.setPostFailureUrl(socialProperties.getFailureUrl());
        log.info("Demo ======>: DemoSocialConfigurer.postProcess");
        return (T) filter;
    }
}
