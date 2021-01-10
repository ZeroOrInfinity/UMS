package top.dcenter.ums.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * ums 属性
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.1.10 11:58
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ums")
public class UmsProperties {
    /**
     * 第三方登录成功后, 返回获取 token 自动脚本页面. 默认: /oauth2Token
     */
    private String autoGetTokenUri = "/oauth2Token";

    /**
     * 第三方登录成功后, autoGetTokenUrl 获取 token 处理接口 url, 默认: /oauth2Callback
     */
    private String oauth2CallbackUri = "/oauth2Callback";

    /**
     * 第三方登录成功后, oauth2CallbackUri 接收的参数名称, 默认: tk
     */
    private String oauth2TokenParamName = "tk";

    /**
     * 存储 OAuth2Token 的临时缓存前缀: 默认: TEMP:OAuth2Token:
     */
    private String tempOauth2TokenPrefix = "TEMP:OAuth2Token:";
    /**
     * token 与 refreshToken 的分隔符: 默认: #@#
     */
    private String delimiterOfTokenAndRefreshToken = "#@#";

    /**
     * 第三方登录成功后, 临时存储在 redis 的 token 值的 TTL, 默认: 30 秒
     */
    private Duration tempOauth2TokenTimeout = Duration.ofSeconds(30);

}
