package top.dcenter.ums.security.common.api.config;

import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;
import top.dcenter.ums.security.common.config.SecurityCoreAutoConfigurer;
import top.dcenter.ums.security.common.consts.SecurityConstants;

import java.util.Map;
import java.util.Set;


/**
 * 对 WebSecurityConfigurerAdapter 的扩展，使其能跨模块的灵活的添加 {@link HttpSecurity} 配置, {@link WebSecurity} 配置,
 * {@link AuthenticationManagerBuilder} 配置.<br><br>
 * 注意：<br><br>
 *      1. 需要要在 WebSecurityConfigurerAdapter#configure(http) 方法中放在最后处理的配置。实现
 *         {@link HttpSecurityAware#postConfigure(HttpSecurity http) } 方法。<br><br>
 *      2. 需要要在 WebSecurityConfigurerAdapter#configure(http) 方法中放在前面处理的配置。实现
 *         {@link HttpSecurityAware#preConfigure(HttpSecurity http) } 方法。<br><br>
 *      3. WebSecurityConfigurerAdapter 多个配置类继承此类是会报错，且 authorizeRequests 配置时候要
 *         authorizeRequests().anyRequest().authenticate 放到最后，不然在之后配置的都不会生效。实现
 *         {@link HttpSecurityAware#getAuthorizeRequestMap() } 方法。<br><br>
 * 最终在：{@link SecurityCoreAutoConfigurer} 中配置. <br><br>
 *
 * @author zyw
 * @version V1.0
 * Created by 2020/5/12 12:22
 */
public interface HttpSecurityAware {

    String PERMIT_ALL = "permitAll";
    String DENY_ALL = "denyAll";
    String ANONYMOUS = "anonymous";
    String AUTHENTICATED = "authenticated";
    String FULLY_AUTHENTICATED = "fullyAuthenticated";
    String REMEMBER_ME = "rememberMe";
    String ACCESS = "access";
    String HAS_ROLE = "hasRole";
    String HAS_ANY_ROLE = "hasAnyRole";
    String HAS_AUTHORITY = "hasAuthority";
    String HAS_ANY_AUTHORITY = "hasAnyAuthority";
    String HAS_IP_ADDRESS = "hasIpAddress";

    /**
     * Override this method to configure {@link WebSecurity}. For example, if you wish to
     * ignore certain requests.
     *
     * Endpoints specified in this method will be ignored by Spring Security, meaning it
     * will not protect them from CSRF, XSS, Clickjacking, and so on.
     *
     * Instead, if you want to protect endpoints against common vulnerabilities, then see
     * {@link WebSecurityConfigurerAdapter}{@code #configure(HttpSecurity)} and the
     * {@link HttpSecurity#authorizeRequests}
     * configuration method.
     * @param web the {@link WebSecurity} to use
     */
    void configure(WebSecurity web);


    /**
     * Used by the default implementation of {@link WebSecurityConfigurerAdapter}{@code #authenticationManager()} to attempt
     * to obtain an {@link AuthenticationManager}. If overridden, the
     * {@link AuthenticationManagerBuilder} should be used to specify the
     * {@link AuthenticationManager}.
     *
     * <p>
     * The {@link WebSecurityConfigurerAdapter}{@code #authenticationManagerBean()} method can be used to expose the resulting
     * {@link AuthenticationManager} as a Bean. The {@link WebSecurityConfigurerAdapter}{@code #userDetailsServiceBean()} can
     * be used to expose the last populated {@link UserDetailsService} that is created
     * with the {@link AuthenticationManagerBuilder} as a Bean. The
     * {@link UserDetailsService} will also automatically be populated on
     * {@link HttpSecurity#getSharedObject(Class)} for use with other
     * {@link SecurityContextConfigurer} (i.e. RememberMeConfigurer )
     * </p>
     *
     * <p>
     * For example, the following configuration could be used to register in memory
     * authentication that exposes an in memory {@link UserDetailsService}:
     * </p>
     *
     * <pre>
     * &#064;Override
     * protected void configure(AuthenticationManagerBuilder auth) {
     * 	auth
     * 	// enable in memory based authentication with a user named
     * 	// &quot;user&quot; and &quot;admin&quot;
     * 	.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;).and()
     * 			.withUser(&quot;admin&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;, &quot;ADMIN&quot;);
     * }
     *
     * // Expose the UserDetailsService as a Bean
     * &#064;Bean
     * &#064;Override
     * public UserDetailsService userDetailsServiceBean() throws Exception {
     * 	return super.userDetailsServiceBean();
     * }
     *
     * </pre>
     * @param auth the {@link AuthenticationManagerBuilder} to use
     * @throws Exception Exception
     */
    void configure(AuthenticationManagerBuilder auth) throws Exception;


    /**
     * 需要要在 WebSecurityConfigurerAdapter#configure(http) 方法中放在前面处理的配置。<br><br>
     * 最终在：{@link SecurityCoreAutoConfigurer} 中配置
     * @param http  HttpSecurity
     * @throws Exception    exception
     */
    void preConfigure(HttpSecurity http) throws Exception;

    /**
     * 需要要在 WebSecurityConfigurerAdapter#configure(http) 方法中放在最后处理的配置。<br><br>
     * 最终在：{@link SecurityCoreAutoConfigurer} 中配置
     * @param http  HttpSecurity
     * @throws Exception    exception
     */
    void postConfigure(HttpSecurity http) throws Exception;

    /**
     * 因为 authorizeRequests 配置时候要 authorizeRequests().anyRequest().authenticate 放到最后，<br>所以这里临时把 权限与 uri 放入 map
     * 给主配置器处理.<br><br>
     * 最终在{@link SecurityCoreAutoConfigurer} 中 <code>configure(HttpSecurity)</code> 方法中配置, return 可以为 null 值.
     * @return authorizeRequestMap <br> ==key== 为权限类型({@link #PERMIT_ALL},{@link #DENY_ALL},
     * {@link #ANONYMOUS},{@link #AUTHENTICATED},{@link #FULLY_AUTHENTICATED},{@link #REMEMBER_ME},{@link #ACCESS},{@link #HAS_ROLE},{@link #HAS_ANY_ROLE},
     *      {@link #HAS_AUTHORITY},{@link #HAS_ANY_AUTHORITY},{@link #HAS_IP_ADDRESS}); <br> ==value== 为
     *      {@link Map}(Map&#60;String, Set&#60;String&#62;&#62;)的 <br> =key= 为 UriHttpMethodTuple, <br> =value= 为 role/authority/ip 的 Set; 当
     *      authorizeRequestMap 的 ==key== 为 {@link #HAS_ROLE},{@link #HAS_ANY_ROLE}/{@link #HAS_AUTHORITY}/{@link #HAS_ANY_AUTHORITY}/
     *      {@link #HAS_IP_ADDRESS}时, set 不为 null, <br> 当 authorizeRequestMap 的 ==key== 为 {@link #PERMIT_ALL}/{@link #DENY_ALL}/
     *      {@link #ANONYMOUS}/{@link #AUTHENTICATED}/{@link #FULLY_AUTHENTICATED}/@link #REMEMBER_ME}/{@link #ACCESS}时, set 可以为
     *      null).
     */
    Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap();


    /**
     * permitUrls 注入到 permitAllMap
     * @param permitUrls    permitUrls 在 application.yml 配置文件上的 url(带 HttpMethod 后缀; 用 : 分隔)
     * @param permitAllMap  permitAllMap
     */
    @SuppressWarnings("unused")
    default void permitUrlsFillingPermitAllMap(@NonNull Set<String> permitUrls,
                                               @NonNull final Map<UriHttpMethodTuple, Set<String>> permitAllMap) {
        permitUrls.forEach(uri -> permitUrlFillingPermitAllMap(uri, permitAllMap));
    }

    /**
     * permitUrl 注入到 permitAllMap
     * @param permitUrl    permitUrl 在 application.yml 配置文件上的 url(带 HttpMethod 后缀; 用 : 分隔)
     * @param permitAllMap  permitAllMap
     */
    default void permitUrlFillingPermitAllMap(@NonNull String permitUrl,
                                                    @NonNull final Map<UriHttpMethodTuple, Set<String>> permitAllMap) {
        UriHttpMethodTuple tuple = null;
        String[] split = StringUtils.split(permitUrl, SecurityConstants.URI_METHOD_SEPARATOR);
        if (null == split)
        {
            return;
        }
        switch (split.length)
        {
            case 1:
                tuple = UriHttpMethodTuple.tuple(null, split[0]);
                break;
            case 2:
                HttpMethod method = HttpMethod.resolve(split[1]);
                if (method != null)
                {
                    tuple = UriHttpMethodTuple.tuple(method, split[0]);

                } else
                {
                    tuple = UriHttpMethodTuple.tuple(null, split[0]);
                }
                break;
            default:
                break;

        }
        if (tuple != null)
        {
            permitAllMap.put(tuple, null);
        }
    }
}
