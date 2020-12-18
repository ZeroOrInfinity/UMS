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

package top.dcenter.ums.security.core.auth.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.validation.annotation.Validated;
import top.dcenter.ums.security.common.enums.CsrfTokenRepositoryType;
import top.dcenter.ums.security.common.enums.LoginProcessType;
import top.dcenter.ums.security.core.auth.config.SecurityAutoConfiguration;
import top.dcenter.ums.security.core.util.MvcUtil;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static top.dcenter.ums.security.common.consts.SecurityConstants.DEFAULT_LOGIN_PAGE_URL;
import static top.dcenter.ums.security.common.consts.SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM;
import static top.dcenter.ums.security.common.consts.SecurityConstants.DEFAULT_REMEMBER_ME_NAME;
import static top.dcenter.ums.security.common.consts.SecurityConstants.DEFAULT_SESSION_INVALID_URL;
import static top.dcenter.ums.security.common.consts.SecurityConstants.DEFAULT_UN_AUTHENTICATION_ROUTING_URL;

/**
 * security 客户端配置属性
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/3 19:51
 */
@SuppressWarnings("jol")
@Getter
@Validated
@ConfigurationProperties("ums.client")
public class ClientProperties {

    private final SessionProperties session = new SessionProperties();
    private final RememberMeProperties rememberMe = new RememberMeProperties();
    private final CsrfProperties csrf = new CsrfProperties();
    private final AnonymousProperties anonymous = new AnonymousProperties();
    private final CorsProperties cors = new CorsProperties();

    /**
     * 一级域名(不包括二级域名)
     * 例如:
     * <pre>
     * domain: www.example.com -> topDomain: example.com
     * domain: www.example.com.cn -> topDomain: example.com.cn
     * domain: aaa.bbb.example.net -> topDomain: example.net
     * </pre>
     * 测试时用的 IP 或 localhost 直接原样设置就行.
     * 在应用启动时通过 {@link SecurityAutoConfiguration} 自动注入 {@link MvcUtil} 字段 topDomain 中.
     * 如在设置跨域 cookie 时可以通过 {@link MvcUtil#getTopDomain()} 方法获取.
     */
    @Setter
    @NotNull(message = "ums.client.topDomain 值必须设置")
    private String topDomain;

    // ============ 登录相关 ============

    /**
     * 设置登录时用户名的 request 参数名称, 默认为 username
     */
    @Setter
    private String usernameParameter = "username";
    /**
     * 设置登录时用户密码的 request 参数名称, 默认为 password
     */
    @Setter
    private String passwordParameter = "password";

    /**
     * 设置登录页(必须自己实现)，用户没有配置则默认为 /login
     */
    @Setter
    private String loginPage = DEFAULT_LOGIN_PAGE_URL;
    /**
     * 设置处理登录表单的 uri，不需要用户实现此 uri，由 Spring security 自动实现， 默认为 /authentication/form
     */
    @Setter
    private String loginProcessingUrl = DEFAULT_LOGIN_PROCESSING_URL_FORM;
    /**
     * 设置认证失败默认跳转页面(必须自己实现)
     */
    @Setter
    private String failureUrl = this.loginPage;
    /**
     * 设置认证成功默认跳转页面(必须自己实现)
     */
    @Setter
    private String successUrl = "/";

    /**
     * 设置由客户端决定认证成功要跳转的 url 的 request 参数名称, 默认为 redirectTargetUrl
     */
    @Setter
    private String targetUrlParameter = "redirectTargetUrl";
    /**
     * 登录后是否利用 Referer 进行跳转, 默认为: true
     */
    @Setter
    private Boolean useReferer = true;

    /**
     * 登录后是否总是使用默认的 successUrl 进行跳转, 默认为: false
     */
    @Setter
    private Boolean alwaysUseDefaultTargetUrl = Boolean.FALSE;


    // ============ 登出相关 ============

    /**
     * 设置登出 url, 默认为 /logout
     */
    @Setter
    private String logoutUrl = "/logout";
    /**
     * 设置登出后跳转的 url(必须自己实现), 默认为 /login
     */
    @Setter
    private String logoutSuccessUrl = "/login";


    // ============ 权限相关 ============

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
    @Setter
    private String[] ignoringUrls;

    /**
     * 不需要认证的 uri(可以带 HttpMethod 后缀; 用:隔开), 例如: /user/** 或 /user/**:post, 默认为 空 Set.<br>
     *     支持通配符 规则具体看 AntPathMatcher.match(pattern, path) <br><br>
     * Example Usage:<br>
     * <pre>
     * Set&#60;String&#62; permitUrls = Set.of("/user/**:post");
     * //equivalent to :
     * httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/user/**").permitAll();
     * // 如果没有带 HttpMethod 后缀:
     * Set&#60;String&#62; permitUrls = Set.of("/user/**");
     * //equivalent to :
     * httpSecurity.authorizeRequests().antMatchers("/user/**").permitAll();
     * </pre>
     */
    @Setter
    private Set<String>  permitUrls = new HashSet<>();


    // ============ 登录路由相关 ============

    /**
     * 是否开启登录路由功能, 根据不同的uri跳转到相对应的登录页, 默认为: false, 当为 true 时还需要配置 loginUnAuthenticationRoutingUrl 和 authRedirectSuffixCondition
     */
    @Setter
    private Boolean openAuthenticationRedirect = false;
    /**
     * 当请求需要身份认证时，默认跳转的url, 当 openAuthenticationRedirect = true 时生效.
     * 会根据 authJumpSuffixCondition 条件判断的认证处理类型的 url，默认实现 /authentication/require. <br><br>
     */
    @Setter
    private String loginUnAuthenticationRoutingUrl = DEFAULT_UN_AUTHENTICATION_ROUTING_URL;
    /**
     * 设置 uri 相对应的跳转登录页, 例如：key=/**: value=/login.html, 用等号隔开key与value, 如: /**=/login.html, 默认为空.
     * 当 openAuthenticationRedirect = true 时生效.
     * 支持通配符 规则具体看 AntPathMatcher.match(pattern, path)
     */
    @Setter
    private List<String> authRedirectSuffixCondition = new ArrayList<>();


    // ============ 其他基本配置 ============

    /**
     * 设置登录后返回格式, 默认: JSON
     */
    @Setter
    private LoginProcessType loginProcessType = LoginProcessType.JSON;

    /**
     * 允许来自同一来源(如: example.com)的 X-Frame-Options headers 请求, 默认为: false
     */
    @Setter
    private Boolean sameOrigin = Boolean.FALSE;

    /**
     * 抑制反射警告, 支持 JDK11, 默认: false ,
     * 在确认 WARNING: An illegal reflective access operation has occurred 安全后, 可以打开此设置, 可以抑制反射警告.
     */
    @Setter
    private Boolean suppressReflectWarning = false;

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
        private Integer maximumSessions = 1;
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
         * session 失效后跳转地址(必须自己实现), loginProcessType=redirect 时有效. 默认: /session/invalid, <br><br>
         *
         */
        private String invalidSessionUrl = DEFAULT_SESSION_INVALID_URL;
        /**
         * concurrent session 失效后跳转地址, loginProcessType=redirect 时有效. 默认: /
         */
        private String invalidSessionOfConcurrentUrl = "/";
        /**
         * session 的 cookie name, 默认为: JSESSIONID, 需要与 server.servlet.session.cookie.name 同时设置
         */
        private String sessionCookieName = "JSESSIONID";

    }

    @Getter
    @Setter
    public static class RememberMeProperties {

        /**
         * RememberMe 是否开启, 默认为 false;
         */
        private Boolean enable = false;

        /**
         * 设置记住我功能的 session 的缓存时长，默认 14 天. If a duration suffix is not specified, seconds will be used.
         */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration rememberMeTimeout = Duration.parse("P14D");

        /**
         * 设置记住我功能的 CookieName，默认 REMEMBER_ME, 自定义 {@link RememberMeServices} 时, 此配置失效
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
        private List<String>  authorities = new ArrayList<>(Collections.singletonList("ROLE_ANONYMOUS"));

    }

    @Getter
    @Setter
    public static class CorsProperties {
        /**
         * 是否支持跨域, 默认为 false;
         */
        private Boolean enable = false;
        /**
         * 允许跨域访问的域，可以是一个域的列表，也可以是通配符"*"。这里要注意Origin规则只对域名有效，并不会对子目录有效。
         */
        private List<String> accessControlAllowOrigin;
        /**
         * 是否允许请求带有验证信息, 当为 true 时, accessControlAllowOrigin 不能为 "*", 默认为 true;
         */
        private Boolean accessControlAllowCredentials = true;
        /**
         * 进行跨区请求允许曝露的 headers，请求成功后，ajax 可以在 XMLHttpRequest 中访问这些头的信息;
         * 注意: 不支持 {@code *}.
         */
        private List<String> accessControlExposeHeaders;
        /**
         * 缓存此次请求的秒数。在这个时间范围内，所有同类型的请求都将不再发送预检请求而是直接使用此次返回的头作为判断依据，非常有用，大幅优化请求次数
         */
        private Duration accessControlMaxAge;
        /**
         * 允许进行跨区请求的请求方法
         */
        private List<String> accessControlAllowMethods;
        /**
         * Set the list of headers that a pre-flight request can list as allowed for use during an actual request.
         * The special value "*" allows actual requests to send any header.
         * A header name is not required to be listed if it is one of: Cache-Control, Content-Language, Expires, Last-Modified, or Pragma.
         * By default this is not set.
         */
        private List<String> accessControlAllowHeaders;
        /**
         * 允许进行跨区请求的 url, 支持通配符.
         */
        private List<String> urlList;

    }

}