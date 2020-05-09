package top.dcenter.security.core.social.qq.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import top.dcenter.security.core.social.qq.api.Qq;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/8 22:14
 */
public class QqConnectionFactory extends OAuth2ConnectionFactory<Qq> {
    /**
     * Create a {@link OAuth2ConnectionFactory}.
     *
     * @param providerId      the provider id e.g. "facebook"
     * @param serviceProvider the ServiceProvider model for conducting the authorization flow and obtaining a native service API instance.
     * @param apiAdapter      the ApiAdapter for mapping the provider-specific service API model to the uniform {@link Connection} interface.
     */
    public QqConnectionFactory(String providerId, OAuth2ServiceProvider<Qq> serviceProvider, ApiAdapter<Qq> apiAdapter) {
        super(providerId, serviceProvider, apiAdapter);
    }

    public QqConnectionFactory(String providerId, String appId, String appSecret) {
        this(providerId, new QqServiceProvider(appId, appSecret), new QqAdapter(providerId));
    }
}
