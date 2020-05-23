package top.dcenter.security.social.api.callback;

import top.dcenter.security.social.SocialController;

import javax.servlet.http.HttpServletRequest;

/**
 * Generates a value for the state parameter with authCallbackPath.<br>
 * 注意：这里实现接口时，对 authCallbackPath 进行格式化为：path=myAuthCallbackPath， 格式化后再对它进行加密({@link java.util.Base64})，
 * 以便在 {@link SocialController#authCallbackRouter(HttpServletRequest)} 中进行解密。<br>
 * 推荐通过继承 {@link BaseOAuth2ConnectionFactory} 后来实现此接口的功能
 * @author zyw23
 * @version V1.0
 * Created by 2020/5/21 10:49
 */
public interface GenerateConnectState {

    /**
     * Generates a value for the state parameter with authCallbackPath.<br>
     * 注意：这里实现接口时，对 authCallbackPath 格式为：path=myAuthCallbackPath
     * 格式化后再对它进行加密({@link java.util.Base64})，以便在
     * {@link SocialController#authCallbackRouter(HttpServletRequest)} 中进行解密。
     * @param authCallbackPath 用于
     * {@link SocialController#authCallbackRouter(HttpServletRequest)} 路由的
     *                  authCallbackPath.
     * @return
     */
    String generateState(String authCallbackPath);
}
