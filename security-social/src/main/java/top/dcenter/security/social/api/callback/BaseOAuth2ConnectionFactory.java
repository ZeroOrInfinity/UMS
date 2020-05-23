package top.dcenter.security.social.api.callback;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

import java.util.Base64;
import java.util.UUID;

/**
 * 扩展 {@link OAuth2ConnectionFactory}, 添加专门为多个回调地址添加路由功能提供的
 * {@link GenerateConnectState#generateState(String authCallbackPath)}
 * 接口，方便对于多个回调地址进行路由管理。<br>
 * 注意： 所有第三方登录的 {@link org.springframework.social.connect.ConnectionFactory} 必须实现此接口
 * @author zyw
 * @version V1.0  Created by 2020/5/21 10:54
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public abstract class BaseOAuth2ConnectionFactory<T> extends OAuth2ConnectionFactory<T> implements GenerateConnectState {
    /**
     * Create a {@link OAuth2ConnectionFactory}.
     *
     * @param providerId      the provider id e.g. "facebook"
     * @param serviceProvider the ServiceProvider model for conducting the authorization flow and obtaining a native service API instance.
     * @param apiAdapter      the ApiAdapter for mapping the provider-specific service API model to the uniform {@link Connection} interface.
     */
    public BaseOAuth2ConnectionFactory(String providerId, OAuth2ServiceProvider<T> serviceProvider, ApiAdapter<T> apiAdapter) {
        super(providerId, serviceProvider, apiAdapter);
    }

    @Override
    public String generateState(String authCallbackPath) {

        // TODO 提取常量
        String state = UUID.randomUUID().toString();
        String router = "path=" + authCallbackPath;
        String routerEncoder = Base64.getEncoder().encodeToString(router.getBytes());
        state = state.substring(state.lastIndexOf("-") + 1) + "-" + routerEncoder;

        return state;
    }
}
