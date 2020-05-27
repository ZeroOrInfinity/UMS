package top.dcenter.security.social.api.callback;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import top.dcenter.security.social.controller.SocialController;
import top.dcenter.security.social.properties.SocialProperties;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

import static top.dcenter.security.core.consts.SecurityConstants.CALLBACK_URL_KEY_IN_STATE;
import static top.dcenter.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.security.core.consts.SecurityConstants.UUID_SEPARATOR;

/**
 * 扩展 {@link OAuth2ConnectionFactory}. <br>
 * 添加专门为多个回调地址添加路由功能方法 {@link #generateState(String)}，方便对于多个回调地址进行路由管理。<br>
 * 注意： 所有第三方登录的 {@link org.springframework.social.connect.ConnectionFactory} 必须继承此抽象工厂。<br>
 *     如果修改回调地址加密逻辑，同时要修改 {@link RedirectUrlHelper#decodeRedirectUrl(String)} 的解密逻辑。
 * @author zyw
 * @version V1.0  Created by 2020/5/21 10:54
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public abstract class BaseOAuth2ConnectionFactory<T> extends OAuth2ConnectionFactory<T> {

    protected SocialProperties socialProperties;
    /**
     * Create a {@link OAuth2ConnectionFactory}.
     * @see OAuth2ConnectionFactory
     * @param providerId      the provider id e.g. "facebook"
     * @param serviceProvider the ServiceProvider model for conducting the authorization flow and obtaining a native service API instance.
     * @param apiAdapter      the ApiAdapter for mapping the provider-specific service API model to the uniform {@link Connection} interface.
     */
    public BaseOAuth2ConnectionFactory(String providerId, OAuth2ServiceProvider<T> serviceProvider,
                                       ApiAdapter<T> apiAdapter, SocialProperties socialProperties) {
        super(providerId, serviceProvider, apiAdapter);
        this.socialProperties = socialProperties;
    }

    /**
     * Generates a value for the state parameter with realAuthCallbackPath.<br>
     * 注意：这里实现接口时，对 realAuthCallbackPath 格式为：path=myAuthCallbackPath
     * 格式化后再对它进行加密({@link java.util.Base64})，以便在
     * {@link SocialController#authCallbackRouter(HttpServletRequest)} 中进行解密。
     * @param realAuthCallbackPath 用于 {@link SocialController#authCallbackRouter(HttpServletRequest)} 路由的 realAuthCallbackPath.
     * @return
     */
    public String generateState(String realAuthCallbackPath) {

        String state = UUID.randomUUID().toString();
        // 对真实回调地址设置成KV键值对形式
        String router = CALLBACK_URL_KEY_IN_STATE + KEY_VALUE_SEPARATOR + realAuthCallbackPath;
        // 加密
        String routerEncoder = Base64.getEncoder().encodeToString(router.getBytes());
        // 把真实的回调地址放入 state
        state = state.substring(state.lastIndexOf(UUID_SEPARATOR) + 1) + UUID_SEPARATOR + routerEncoder;

        return state;
    }

    /**
     *  通过此方法, 可以自定义构建 social 统一的回调地址路由到多个回调地址的 redirectUrl 的算法，去替换
     * {@link top.dcenter.security.social.callback.SocialOAuth2AuthenticationService#buildReturnToUrl(HttpServletRequest)} 方法的默认算法；<br>
     * @param request
     * @param returnToUrlParameters url参数
     * @return  返回 null 时，使用默认算法，否则使用此方法的实现
     */
    @SuppressWarnings("JavadocReference")
    public String buildReturnToUrl(HttpServletRequest request, Set<String> returnToUrlParameters) {
        // 自定义时可参考 top.dcenter.security.social.callback.SocialOAuth2AuthenticationService#buildReturnToUrl(HttpServletRequest) 方法的实现
        return null;
    }


}
