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

package top.dcenter.ums.security.core.oauth.properties;

import com.xkcoding.http.config.HttpConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.validation.annotation.Validated;
import top.dcenter.ums.security.core.oauth.filter.login.Auth2LoginAuthenticationFilter;
import top.dcenter.ums.security.core.api.oauth.job.RefreshTokenJob;
import top.dcenter.ums.security.core.oauth.userdetails.TemporaryUser;

import javax.validation.constraints.NotNull;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;

/**
 * 第三方登录属性
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/6 21:01
 */
@SuppressWarnings({"jol"})
@Getter
@Setter
@Validated
@ConfigurationProperties("ums.oauth")
public class Auth2Properties {

    // =================== 第三方 属性 ===================
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties github;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties weibo;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties gitee;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties dingtalk;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties baidu;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties coding;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties oschina;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties alipay;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties qq;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties wechatOpen;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties wechatMp;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties taobao;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties google;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties facebook;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties douyin;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties linkedin;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties microsoft;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties mi;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties toutiao;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties teambition;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties renren;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties pinterest;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties stackOverflow;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties huawei;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties wechatEnterprise;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties wechatEnterpriseWeb;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties kujiale;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties gitlab;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties meituan;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties eleme;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties twitter;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties jd;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties aliyun;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties feishu;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties xmly;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties customize;
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty
    private BaseAuth2Properties gitlabPrivate;

    
    // =================== OAuth2 属性 ===================
    /**
     * 是否支持第三方授权登录功能, 默认: 空, 必须明确配置是否支持
     */
    @NotNull(message = "ums.oauth.enabled 值必须设置为: true 或 false")
    private Boolean enabled;

    /**
     * 第三方授权登录后如未注册用户是否支持自动注册功能, 默认: true<br>
     * {@code https://gitee.com/pcore/just-auth-spring-security-starter/issues/I22KP3}
     */
    private Boolean autoSignUp = true;

    /**
     * 第三方授权登录后如未注册用户不支持自动注册功能, 则跳转到此 url 进行注册逻辑, 此 url 必须开发者自己实现; 默认: /signUp.html; <br>
     * 例如:<br>
     * 1. 设置值 "/signUp", 则跳转指定到 "/signUp" 进行注册. <br>
     * 2. 想返回自定义 json 数据到前端, 这里要设置 为空 , 在 {@link Auth2LoginAuthenticationFilter} 设置的
     * {@link AuthenticationSuccessHandler} 上处理返回 json; 判断是否为临时用户的条件是: {@link Authentication#getPrincipal()}
     * 是否为 {@link TemporaryUser} 类型.<br>
     */
    private String signUpUrl = "/signUp";

    /**
     * 第三方登录回调的域名, 例如：https://localhost 默认为 "http://127.0.0.1"，
     * redirectUrl 直接由 {domain}/{servletContextPath}/{redirectUrlPrefix}/{providerId}(ums.oauth.[qq/gitee/weibo])组成
     */
    private String domain = "http://127.0.0.1";

    /**
     * 第三方登录回调处理 url 前缀 ，也就是 RedirectUrl 的前缀, 不包含 ServletContextPath，默认为 /oauth/login.<br><br>
     */
    private String redirectUrlPrefix = "/oauth/login";

    /**
     * 第三方登录授权登录 url 前缀, 不包含 ServletContextPath，默认为 /oauth/authorization.<br><br>
     */
    private String authLoginUrlPrefix = "/oauth/authorization";

    /**
     * 第三方授权登录成功后的默认权限, 多个权限用逗号分开, 默认为: "ROLE_USER"
     */
    private String defaultAuthorities = "ROLE_USER";

    /**
     * 用于第三方授权登录时, 未开启自动注册且用户是第一次授权登录的临时用户密码, 默认为: "".<br>
     *     注意: 生产环境更换密码
     */
    private String temporaryUserPassword = "";
    /**
     * 用于第三方授权登录时, 未开启自动注册且用户是第一次授权登录的临时用户的默认权限, 多个权限用逗号分开, 默认为: "ROLE_TEMPORARY_USER"
     */
    private String temporaryUserAuthorities = "ROLE_TEMPORARY_USER";

    // =================== refreshToken 定时任务 属性 ===================
    /**
     * A cron-like expression.
     * <pre>
     * 0 * 2 * * ? 分别对应: second/minute/hour/day of month/month/day of week
     * </pre>
     * 默认为: "0 * 2 * * ?", 凌晨 2 点启动定时任务, 支持分布式(分布式 IOC 容器中必须有 {@link RedisConnectionFactory}, 也就是说,
     * 是否分布式执行依据 IOC 容器中是否有 {@link RedisConnectionFactory})
     * @see org.springframework.scheduling.support.CronSequenceGenerator
     */
    private String refreshTokenJobCron = "0 * 2 * * ?";

    /**
     * 是否支持定时刷新 AccessToken 定时任务, 考虑到很多应用都有自己的定时任务应用, 默认: false.
     * {@link RefreshTokenJob} 接口的实现已注入 IOC 容器, 方便自定义定时任务接口时调用. <br>
     * 支持分布式(分布式 IOC 容器中必须有 {@link RedisConnectionFactory}, 也就是说,
     * 是否分布式执行依据 IOC 容器中是否有 {@link RedisConnectionFactory})
     */
    private Boolean enableRefreshTokenJob = false;

    /**
     * 定时刷新 accessToken 任务时, 批处理数据库的记录数.<br>
     * 注意: 分布式应用时, 此配置不同服务器配置必须是一样的. batchCount 大小需要根据实际生产环境进行优化
     */
    private Integer batchCount = 1000;

    /**
     * accessToken 的剩余有效期内进行刷新 accessToken, 默认: 24, 单位: 小时.<br>
     * 注意: 需要根据实际生产环境进行优化
     */
    private Integer remainingExpireIn = 24;

    // =================== justAuth 属性 ===================

    @NestedConfigurationProperty
    private JustAuthProperties justAuth = new JustAuthProperties();
    /**
     * 针对国外服务可以单独设置代理
     * HttpConfig config = new HttpConfig();
     * config.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10080)));
     * config.setTimeout(15000);
     *
     * @since 1.15.5
     */
    private HttpConfigProperties proxy = new HttpConfigProperties();
    

    @Getter
    @Setter
    public static class HttpConfigProperties {

        /**
         * 当 enable = true 时, 返回 HttpConfig 对象, 否则返回为 null.
         * @return  当 enable = true 时, 返回 HttpConfig 对象, 否则返回为 null.
         */
        public HttpConfig getHttpConfig() {
            if (!enable)
            {
                return HttpConfig.builder().timeout((int) timeout.toMillis()).build();
            }
            return HttpConfig.builder()
                    .proxy(new Proxy(proxy, new InetSocketAddress(hostname, port)))
                    .timeout((int) timeout.toMillis())
                    .build();
        }

        /**
         * 是否支持代理, 默认为: false. <br>
         */
        private Boolean enable = false;

        /**
         * 针对国外服务可以单独设置代理类型, 默认 Proxy.Type.HTTP, enable = true 时生效.
         */
        private Proxy.Type proxy = Proxy.Type.HTTP;

        /**
         * 代理 host, enable = true 时生效.
         */
        private String hostname;

        /**
         * 代理端口, enable = true 时生效.
         */
        private Integer port;

        /**
         * 代理超时, 默认 PT3S
         */
        private Duration timeout = Duration.ofSeconds(3);
        /**
         * 用于国外网站代理超时, 默认 PT15S
         */
        private Duration foreignTimeout = Duration.ofSeconds(15);

    }
}