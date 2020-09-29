package top.dcenter.ums.security.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.security.config.http.SessionCreationPolicy;
import top.dcenter.ums.security.core.enums.CsrfTokenRepositoryType;
import top.dcenter.ums.security.core.enums.LoginProcessType;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PAGE_URL;
import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM;
import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_REMEMBER_ME_NAME;
import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_SESSION_INVALID_URL;
import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_UN_AUTHENTICATION_URL;

/**
 * security 客户端配置属性
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/3 19:51
 */
@SuppressWarnings("jol")
@Getter
@Setter
@ConfigurationProperties("security.client")
public class ClientProperties {

    @NestedConfigurationProperty
    private final SessionProperties session = new SessionProperties();
    @NestedConfigurationProperty
    private final RememberMeProperties rememberMe = new RememberMeProperties();
    @NestedConfigurationProperty
    private final CsrfProperties csrf = new CsrfProperties();
    @NestedConfigurationProperty
    private final AnonymousProperties anonymous = new AnonymousProperties();

    /**
     * 设置登录页，用户没有配置则默认为 /login
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
     * 错误页面
     */
    private String errorUrl = "/error";
    /**
     * 4xx 错误页面
     */
    private String error4Url = "/4*.html";
    /**
     * 5xx 错误页面
     */
    private String error5Url = "/5*.html";
    /**
     * 设置认证成功默认跳转页面
     */
    private String successUrl = "/";

    /**
     * 不需要认证的静态资源 urls, 通过以下方式配置: <br><br>
     * Example Usage:
     *
     * <pre>
     * webSecurityBuilder.ignoring()
     * // ignore all URLs that start with /resources/ or /static/
     * 		.antMatchers(&quot;/resources/**&quot;, &quot;/static/**&quot;);
     * </pre>
     *
     * Alternatively this will accomplish the same result:
     *
     * <pre>
     * webSecurityBuilder.ignoring()
     * // ignore all URLs that start with /resources/ or /static/
     * 		.antMatchers(&quot;/resources/**&quot;).antMatchers(&quot;/static/**&quot;);
     * </pre>
     *
     * Multiple invocations of ignoring() are also additive, so the following is also
     * equivalent to the previous two examples:
     *
     * <pre>
     * webSecurityBuilder.ignoring()
     * // ignore all URLs that start with /resources/
     * 		.antMatchers(&quot;/resources/**&quot;);
     * webSecurityBuilder.ignoring()
     * // ignore all URLs that start with /static/
     * 		.antMatchers(&quot;/static/**&quot;);
     * // now both URLs that start with /resources/ and /static/ will be ignored
     * </pre>
     * 支持通配符 规则具体看 AntPathMatcher.match(pattern, path)
     */
    private String[] ignoringUrls;

    /**
     * 不需要认证的 uri, 默认为 空 Set.<br>
     *     支持通配符 规则具体看 AntPathMatcher.match(pattern, path) <br><br>
     *     httpSecurity.authorizeRequests().antMatchers(permitAllArray).permitAll()
     * Example Usage:
     *
     * <pre>
     * String[] permitAllArray = new String[]{&quot;/hello&quot;, &quot;/index&quot;, &quot;/down/**&quot;};
     * httpSecurity.authorizeRequests()
     *      .antMatchers(permitAllArray).permitAll();
     * </pre>
     */
    private Set<String>  permitUrls = new HashSet<>();

    /**
     * 是否开启登录路由功能, 根据不同的uri跳转到相对应的登录页, 默认为: false, 当为 true 时还需要配置 loginUnAuthenticationUrl 和 authRedirectSuffixCondition
     */
    private Boolean openAuthenticationRedirect = false;
    /**
     * 当请求需要身份认证时，默认跳转的url, 当 openAuthenticationRedirect = true 时生效.
     * 会根据 authJumpSuffixCondition 条件判断的认证处理类型的 url，默认实现 /authentication/require. <br><br>
     * 注意: 如果修改此 uri, 需要重新实现修改后的 uri
     */
    private String loginUnAuthenticationUrl = DEFAULT_UN_AUTHENTICATION_URL;
    /**
     * 设置 uri 相对应的跳转登录页, 例如：key=/**: value=/login.html, 用等号隔开key与value, 如: /**=/login.html, 默认为空.
     * 当 openAuthenticationRedirect = true 时生效.
     * 支持通配符 规则具体看 AntPathMatcher.match(pattern, path)
     */
    private List<String> authRedirectSuffixCondition;

    /**
     * 设置默认登录后为 返回 JSON
     */
    private LoginProcessType loginProcessType = LoginProcessType.JSON;

    /**
     * 设置登出 url, 默认为 /logout
     */
    public String logoutUrl = "/logout";
    /**
     * 设置登出后跳转的 url, 默认为 /login
     */
    public String logoutSuccessUrl = "/login";
    /**
     * 设置由客户端决定认证成功要跳转的 url 的 request 参数名称, 默认为 redirectTargetUrl
     */
    public String targetUrlParameter = "redirectTargetUrl";
    /**
     * 设置登录时用户名的 request 参数名称, 默认为 username
     */
    public String usernameParameter = "username";
    /**
     * 设置登录时用户密码的 request 参数名称, 默认为 password
     */
    public String passwordParameter = "password";
    /**
     * 登录后是否利用 Referer 进行跳转, 默认为: true
     */
    public Boolean useReferer = Boolean.TRUE;
    /**
     * 允许来自同一来源(如: example.com)的请求, 默认为: false
     */
    public Boolean sameOrigin = Boolean.FALSE;




    public ClientProperties() {
        this.authRedirectSuffixCondition = new ArrayList<>();
    }

    public String getQueryRememberMeTableExistSql(String databaseName){
        return "SELECT COUNT(1) FROM information_schema.tables WHERE table_schema='" + databaseName + "' AND table_name = 'persistent_logins'";
    }

    @Getter
    @Setter
    public static class SessionProperties {
        /**
         * 当为 false 时允许单个用户拥有任意数量的 session（不同设备或不同浏览器），默认为 false。
         * 当设置 true 时，同时请设置一下选项：maximumSessions 和 maxSessionsPreventsLogin
         */
        private Boolean sessionNumberControl = false;

        /**
         * 当设置为 1 时，maxSessionsPreventsLogin 为 false 时，同个用户登录会自动踢掉上一次的登录状态。
         * 当设置为 1 时，maxSessionsPreventsLogin 为 true 时，同个用户登录会自动自动拒绝用户再登录。
         * 默认为 1。
         * 如要此选项生效，sessionNumberControl 必须为 true
         */
        private int maximumSessions = 1;
        /**
         * 同个用户达到最大 maximumSession 后，当为 true 时自动拒绝用户再登录，当为 false 时自动踢掉上一次的登录状态, 默认为 false。
         * 如要此选项生效，sessionNumberControl 必须为 true
         */
        private Boolean maxSessionsPreventsLogin = false;


        /**
         * If set to true,
         * allows HTTP sessions to be rewritten in the URLs when using HttpServletResponse.encodeRedirectURL(String)
         * or HttpServletResponse.encodeURL(String), otherwise disallows HTTP sessions to be included in the URL. This prevents leaking information to external domains. 默认为 false。
         */
        private Boolean enableSessionUrlRewriting = false;
        /**
         * Specifies the various session creation policies for Spring Security. 默认为 {@link SessionCreationPolicy.ALWAYS}。
         */
        @SuppressWarnings("JavadocReference")
        private SessionCreationPolicy sessionCreationPolicy = SessionCreationPolicy.ALWAYS;

        /**
         * session 失效后跳转地址, loginProcessType=redirect 时有效. 默认: /session/invalid, <br><br>
         * 注意: 如果修改此 uri, 需要重新实现修改后的 uri
         */
        private String invalidSessionUrl = DEFAULT_SESSION_INVALID_URL;
        /**
         * concurrent session 失效后跳转地址, loginProcessType=redirect 时有效. 默认: /
         */
        private String invalidSessionOfConcurrentUrl = "/";
        /**
         * session 的 cookie name, 默认为: JSESSIONID, , 需要与 server.servlet.session.cookie.name 同时设置
         */
        private String sessionCookieName = "JSESSIONID";

    }

    @Getter
    @Setter
    public static class RememberMeProperties {
        /**
         * 设置记住我功能的 session 的缓存时长，默认 14 天. If a duration suffix is not specified, seconds will be used.
         */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration rememberMeTimeout = Duration.parse("P14D");

        /**
         * 设置记住我功能的 CookieName，默认 REMEMBER_ME
         */
        private String rememberMeCookieName = DEFAULT_REMEMBER_ME_NAME;
        /**
         * 设置记住我功能的参数名称，默认 REMEMBER_ME
         */
        private String rememberMeParameter = DEFAULT_REMEMBER_ME_NAME;

        /**
         * Whether the cookie should be flagged as secure or not. Secure cookies can only be sent over an HTTPS connection and thus cannot be accidentally submitted over HTTP where they could be intercepted.
         * By default the cookie will be secure if the request is secure. If you only want to use remember-me over HTTPS (recommended) you should set this property to true. 默认为 false。
         */
        private Boolean useSecureCookie = false;

    }

    @Getter
    @Setter
    public static class CsrfProperties {
        /**
         * csrf 是否开启, 默认为 false;
         */
        private Boolean csrfIsOpen = false;
        /**
         * 忽略指定请求的 CSRF 防护, 默认为 空 Set
         */
        private Set<String>  ignoringAntMatcherUrls = new HashSet<>();
        /**
         * csrf tokenRepository 的存储类型, 默认为 session
         */
        private CsrfTokenRepositoryType tokenRepositoryType = CsrfTokenRepositoryType.SESSION;

    }

    @Getter
    @Setter
    public static class AnonymousProperties {
        /**
         * ANONYMOUS 是否开启, 默认为 true;
         */
        private Boolean anonymousIsOpen = true;
        /**
         * 匿名用户名称, 默认为 ANONYMOUS
         */
        private String  principal = "ANONYMOUS";
        /**
         * 匿名用户权限 list, 默认为 ROLE_ANONYMOUS
         */
        private List<String>  authorities = new ArrayList<>(List.of("ROLE_ANONYMOUS"));

    }

}
