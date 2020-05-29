package top.dcenter.security.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import top.dcenter.security.core.enums.LoginType;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PAGE_URL;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REMEMBER_ME_NAME;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_UNAUTHENTICATION_URL;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX;

/**
 * security 网页端配置属性
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 19:51
 */
@SuppressWarnings("jol")
@Getter
@Setter
@ConfigurationProperties("security.browser")
public class BrowserProperties {

    public BrowserProperties() {

        Map<String, String> map = new HashMap<>();
        this.authRedirectSuffixCondition = map;

    }

    /**
     * 设置查询表是否创建的 SQL
     */
    public static final String QUERY_REMEMBER_ME_TABLE_EXIST_SQL = "SELECT COUNT(1) FROM information_schema.tables WHERE table_schema='sso-demo' AND table_name = 'persistent_logins'";

    /**
     * 设置记住我功能的 session 的缓存时长，默认 7 天. If a duration suffix is not specified, seconds will be used.
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration rememberMeTimeout = Duration.parse("P7D");

    /**
     * 设置记住我功能的 CookieName，默认 remember-me
     */
    private String rememberMeCookieName = DEFAULT_REMEMBER_ME_NAME;


    /**
     * 当为 false 时允许单个用户拥有任意数量的 session（不同设备或不同浏览器），默认为 false。
     * 当设置 true 时，同时请设置一下选项：maximumSessions 和 maxSessionsPreventsLogin
     */
    private Boolean sessionNumberSetting = false;

    /**
     * 当设置为 1 时，maxSessionsPreventsLogin 为 false 时，同个用户登录会自动踢掉上一次的登录状态。
     * 当设置为 1 时，maxSessionsPreventsLogin 为 true 时，同个用户登录会自动自动拒绝用户再登录。
     * 默认为 1。
     * 如要此选项生效，sessionNumberSetting 必须为 true
     */
    private int maximumSessions = 1;
    /**
     * 同个用户达到最大 maximumSession 后，自动拒绝用户再登录，默认为 false。
     * 如要此选项生效，sessionNumberSetting 必须为 false
     */
    private Boolean maxSessionsPreventsLogin = false;



    /**
     * 处理验证码的url前缀: 默认为 /code, 例如：图片验证码校验时的url为 /code/image,短信验证码校验时的url为 /code/sms
     */
    public String defaultValidateCodeUrlPrefix = DEFAULT_VALIDATE_CODE_URL_PREFIX;
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
     * 当请求需要身份认证时，默认跳转的url
     * 会根据 authJumpSuffixCondition 条件判断的认证处理类型的 url，默认实现 /authentication/require
     */
    private String loginUnAuthenticationUrl = DEFAULT_UNAUTHENTICATION_URL;
    /**
     * 设置 uri 的特定后缀对应的跳转登录页, 例如：key=/**: value=/security/login.html。 默认为空
     * 支持通配符 规则具体看 AntPathMatcher.match(pattern, path)
     */
    private Map<String, String> authRedirectSuffixCondition;

    /**
     * 设置默认登录后为 返回 JSON
     */
    private LoginType loginType = LoginType.JSON;

    public String getQueryRememberMeTableExistSql(String databaseName){
        return "SELECT COUNT(1) FROM information_schema.tables WHERE table_schema='" + databaseName + "' AND table_name = 'persistent_logins'";
    }

}
