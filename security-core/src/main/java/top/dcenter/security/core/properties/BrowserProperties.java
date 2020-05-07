package top.dcenter.security.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.dcenter.security.core.enums.LoginType;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PAGE_URL;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_UNAUTHENTICATION_URL;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/3 19:51
 */
@Getter
@Setter
@ConfigurationProperties("security.browser")
public class BrowserProperties {
    /**
     * 设置记住我功能的 session 的缓存时长，默认 7 * 24 * 3600
     */
    private int rememberMeSeconds = 7 * 24 * 3600;

    /**
     * 设置登录页，用户没有配置则默认为 /security/login.html
     */
    private String loginPage = DEFAULT_LOGIN_PAGE_URL;
    /**
     * 设置处理登录表单的 uri，不需要用户实现此 uri，由 Spring security 自动实现， 默认为 /authentication/form
     */
    private String loginProcessingUrl = DEFAULT_LOGIN_PROCESSING_URL_FORM;
    /**
     * 设置认证失败默认跳转页面
     */
    private String failureUrl = this.loginPage;
    /**
     * 设置认证成功默认跳转页面
     */
    private String successUrl = "/";

    /**
     * 当请求需要身份认证时，默认跳转的url
     * 会根据 authJumpSuffixCondition 条件判断的认证处理类型的 url，默认实现 /authentication/require
     */
    private String loginUnAuthenticationUrl = DEFAULT_UNAUTHENTICATION_URL;
    /**
     * 设置 uri 后缀, 作为区分认证方式的条件， TODO 此功能不成熟, 默认为 .html
     */
    private String authJumpSuffixCondition = ".html";

    /**
     * 设置默认登录后为 返回 JSON
     */
    private LoginType loginType = LoginType.JSON;


}
