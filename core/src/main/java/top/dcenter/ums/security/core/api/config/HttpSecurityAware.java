package top.dcenter.ums.security.core.api.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.ums.security.core.config.SecurityCoreAutoConfigurer;

import java.util.Map;
import java.util.Set;

/**
 * 对 WebSecurityConfigurerAdapter 的扩展，使其能跨模块的灵活的添加 HttpSecurity 配置。<br><br>
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
     * 最终在{@link SecurityCoreAutoConfigurer} 中配置, 主要代码:<br>
     * <pre>
     * final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = httpSecurity.authorizeRequests();
     * expressionInterceptUrlRegistry
     *     .antMatchers(permitAllArray).permitAll()
     *     .antMatchers(denyAllArray).denyAll()
     *     .antMatchers(anonymousArray).anonymous()
     *     .antMatchers(authenticatedArray).authenticated()
     *     .antMatchers(fullyAuthenticatedArray).fullyAuthenticated()
     *     .antMatchers(rememberMeArray).rememberMe();
     *
     * hasRoleMap.forEach((uri, roleArr) -> {
     *             for (String role : roleArr)
     *             {
     *                 expressionInterceptUrlRegistry.antMatchers(uri).hasRole(role);
     *             }
     *         });
     *
     * hasAnyRoleMap.forEach((uri, roleArr) -> expressionInterceptUrlRegistry.antMatchers(uri).hasAnyRole(roleArr));
     *
     * hasAuthorityMap.forEach(
     *         (uri, authorityArr) -> {
     *             for (String s : authorityArr)
     *             {
     *                 expressionInterceptUrlRegistry.antMatchers(uri).hasAuthority(s);
     *             }
     *         });
     *
     * hasAnyAuthorityMap.forEach((uri, authorityArr) -> expressionInterceptUrlRegistry.antMatchers(uri).hasAnyAuthority(authorityArr));
     *
     * hasIpAddressMap.forEach(
     *         (uri, ipArr) -> {
     *             for (String s : ipArr)
     *             {
     *                 expressionInterceptUrlRegistry.antMatchers(uri).hasIpAddress(s);
     *             }
     *         });
     *
     * if (accessArray.length > 0)
     * {
     *     StringBuilder sb = new StringBuilder();
     *     for (String access : accessArray)
     *     {
     *         sb.append(access).append(" and ");
     *     }
     *     sb.setLength(sb.length() - 5);
     *     expressionInterceptUrlRegistry.anyRequest().access(sb.toString());
     * }
     *
     * expressionInterceptUrlRegistry.anyRequest().authenticated();
     * </pre>
     * return 可以为 null 值
     * @return authorizeRequestMap <br> ==key== 为权限类型({@link #PERMIT_ALL},{@link #DENY_ALL},
     * {@link #ANONYMOUS},{@link #AUTHENTICATED},{@link #FULLY_AUTHENTICATED},{@link #REMEMBER_ME},{@link #ACCESS},{@link #HAS_ROLE},{@link #HAS_ANY_ROLE},
     *      {@link #HAS_AUTHORITY},{@link #HAS_ANY_AUTHORITY},{@link #HAS_IP_ADDRESS}); <br> ==value== 为
     *      {@link Map}(Map&#60;String, Set&#60;String&#62;&#62;的 <br> =key= 为 uri, <br> =value= 为 role/authority/ip 的 Set; 当
     *      authorizeRequestMap 的 key 为 {@link #HAS_ROLE},{@link #HAS_ANY_ROLE}/{@link #HAS_AUTHORITY}/{@link #HAS_ANY_AUTHORITY}/
     *      {@link #HAS_IP_ADDRESS}时, set 不为 null, <br> 当 authorizeRequestMap 的 key 为 {@link #PERMIT_ALL}/{@link #DENY_ALL}/
     *      {@link #ANONYMOUS}/{@link #AUTHENTICATED}/{@link #FULLY_AUTHENTICATED}/@link #REMEMBER_ME}/{@link #ACCESS}时, set 可以为
     *      null).
     */
    Map<String, Map<String, Set<String>>> getAuthorizeRequestMap();



}
