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
 * 最终在：{@link SecurityCoreAutoConfigurer} 中配置
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
     * 因为 authorizeRequests 配置时候要 authorizeRequests().anyRequest().authenticate 放到最后，所以这里临时把 权限与 uri 放入 map
     * 给主配置器处理.<br><br>
     * return 可以为 null 值
     * @return authorizeRequestMap ==key== 为权限类型({@link #PERMIT_ALL},{@link #DENY_ALL},
     * {@link #ANONYMOUS},{@link #AUTHENTICATED},{@link #FULLY_AUTHENTICATED},{@link #REMEMBER_ME},{@link #HAS_ROLE},{@link #HAS_ANY_ROLE},
     *      {@link #HAS_AUTHORITY},{@link #HAS_ANY_AUTHORITY},{@link #HAS_IP_ADDRESS}); ==value== 为
     *      {@link Map}(Map&#60;String, Set&#60;String&#62;&#62;的 =key= 为 uri, =value= 为 role/authority/ip 的 Set; 当
     *      authorizeRequestMap 的 key 为 {@link #HAS_ROLE},{@link #HAS_ANY_ROLE}/{@link #HAS_AUTHORITY}/{@link #HAS_ANY_AUTHORITY}/
     *      {@link #HAS_IP_ADDRESS}时, set 不为 null, 当 authorizeRequestMap 的 key 为 {@link #PERMIT_ALL}/{@link #DENY_ALL}/
     *      {@link #ANONYMOUS}/{@link #AUTHENTICATED}/{@link #FULLY_AUTHENTICATED}/@link #REMEMBER_ME}时, set 可以为
     *      null).
     */
    Map<String, Map<String, Set<String>>> getAuthorizeRequestMap();



}
