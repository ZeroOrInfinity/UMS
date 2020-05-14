package top.dcenter.security.social.qq.connect;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import top.dcenter.security.social.qq.api.Qq;
import top.dcenter.security.social.qq.api.QqImpl;

/**
 * QQ 服务提供商
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/8 21:31
 */
public class QqServiceProvider extends AbstractOAuth2ServiceProvider<Qq> {
    /**
     * 获取 Qq openid 链接
     */
    public static final String ACCESS_TOKEN_URL = "https://graph.qq.com/oauth2.0/token";
    /**
     * 获取 Qq userInfo 链接
     */
    public static final String AUTHORIZE_URL = "https://graph.qq.com/oauth2.0/authorize";

    private String appId;

    /**
     * Create a new {@link OAuth2ServiceProvider}.
     *
     * @param oauth2Operations the OAuth2Operations template for conducting the OAuth 2 flow with the provider.
     */
    public QqServiceProvider(OAuth2Operations oauth2Operations) {
        super(oauth2Operations);
    }

    public QqServiceProvider(String appId, String appSecret) {
        this(new QqOauth2Template(appId, appSecret, AUTHORIZE_URL, ACCESS_TOKEN_URL));
        this.appId = appId;
    }

    @Override
    public Qq getApi(String accessToken) {
        return new QqImpl(accessToken, appId);
    }
}
