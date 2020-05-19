package top.dcenter.security.social.gitee.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import top.dcenter.security.social.gitee.api.Gitee;
import top.dcenter.security.social.gitee.api.GiteeImpl;

/**
 * gitee 服务提供商
 * @author zyw
 * @version V1.0  Created by 2020/5/8 21:31
 */
public class GiteeServiceProvider extends AbstractOAuth2ServiceProvider<Gitee> {
    /**
     * 获取 Gitee openid 链接
     */
    public static final String ACCESS_TOKEN_URL = "https://gitee.com/oauth/token";
    /**
     * 获取 Gitee userInfo 链接
     */
    public static final String AUTHORIZE_URL = "https://gitee.com/oauth/authorize";

    private ObjectMapper objectMapper;

    /**
     * Create a new {@link OAuth2ServiceProvider}.
     *
     * @param oauth2Operations the OAuth2Operations template for conducting the OAuth 2 flow with the provider.
     * @param objectMapper
     */
    public GiteeServiceProvider(OAuth2Operations oauth2Operations, ObjectMapper objectMapper) {
        super(oauth2Operations);
        this.objectMapper = objectMapper;
    }

    public GiteeServiceProvider(String appId, String appSecret, ObjectMapper objectMapper) {
        this(new GiteeOauth2Template(appId, appSecret, AUTHORIZE_URL, ACCESS_TOKEN_URL, objectMapper), objectMapper);
    }

    @Override
    public Gitee getApi(String accessToken) {
        return new GiteeImpl(accessToken, objectMapper);
    }
}
