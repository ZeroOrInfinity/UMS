package top.dcenter.security.core.api.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.Map;
import java.util.Set;

/**
 * 对 WebSecurityConfigurerAdapter 的扩展，使其能跨模块的灵活的添加 HttpSecurity 配置。<br>
 * 注意：<br>
 *      1. 需要要在 WebSecurityConfigurerAdapter#configure(http) 方法中放在最后处理的配置。实现
 *         {@link WebSecurityConfigurerAware#postConfigure(HttpSecurity http) } 方法。<br>
 *      2. 需要要在 WebSecurityConfigurerAdapter#configure(http) 方法中放在前面处理的配置。实现
 *         {@link WebSecurityConfigurerAware#preConfigure(HttpSecurity http) } 方法。<br>
 *      3. WebSecurityConfigurerAdapter 多个配置类继承此类是会报错，且 authorizeRequests 配置时候要
 *         authorizeRequests().anyRequest().authenticate 放到最后，不然在之后配置的都不会生效。实现
 *         {@link WebSecurityConfigurerAware#getAuthorizeRequestMap() } 方法。<br>
 * 使用方法请看：{@link top.dcenter.security.browser.config.BrowserSecurityConfigurer}
 * @author zyw
 * @version V1.0
 * Created by 2020/5/12 12:22
 */
@SuppressWarnings("JavadocReference")
public interface WebSecurityConfigurerAware {

    String permitAll = "permitAll";
    String denyAll = "denyAll";
    String anonymous = "anonymous";
    String authenticated = "authenticated";
    String fullyAuthenticated = "fullyAuthenticated";
    String rememberMe = "rememberMe";

    /**
     * 需要要在 WebSecurityConfigurerAdapter#configure(http) 方法中放在最后处理的配置。<br>
     * 使用方法请看：{@link top.dcenter.security.browser.BrowserSecurityConfig}
     * @param http  HttpSecurity
     * @throws Exception    exception
     */
    @SuppressWarnings("JavadocReference")
    void postConfigure(HttpSecurity http) throws Exception;

    /**
     * 需要要在 WebSecurityConfigurerAdapter#configure(http) 方法中放在前面处理的配置。<br>
     * 使用方法请看：{@link top.dcenter.security.browser.BrowserSecurityConfig}
     * @param http  HttpSecurity
     * @throws Exception    exception
     */
    @SuppressWarnings("JavadocReference")
    void preConfigure(HttpSecurity http) throws Exception;

    /**
     * 因为 authorizeRequests 配置时候要 authorizeRequests().anyRequest().authenticate 放到最后，所以这里临时把 权限与 uri 放入 map
     * 给主配置器处理<br>
     * @return authorizeRequestMap key 为权限类型({@link #permitAll},{@link #permitAll},{@link #denyAll},
     * {@link #anonymous},{@link #authenticated},{@link #fullyAuthenticated},{@link #rememberMe})， value 为 uriSet
     */
    Map<String, Set<String>> getAuthorizeRequestMap();

}
