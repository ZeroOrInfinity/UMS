package top.dcenter.security.social.api.callback;

import org.springframework.lang.NonNull;
import top.dcenter.security.core.util.ConvertUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static top.dcenter.security.core.consts.SecurityConstants.CALLBACK_URL_KEY_IN_STATE;
import static top.dcenter.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;
import static top.dcenter.security.core.consts.SecurityConstants.UUID_SEPARATOR;

/**
 * 解析 state，返回真实的回调地址，支持通过统一的回调地址路由到多个回调地址的解析助手。<br>
 *     注意：如果修改回调地址解密逻辑，同时要修改 {@link BaseOAuth2ConnectionFactory#buildReturnToUrl(HttpServletRequest, Set)} 与
 *     {@link BaseOAuth2ConnectionFactory#generateState(String)}的加密逻辑。
 *
 * @author zyw
 * @version V1.0  Created by 2020/5/25 20:54
 */
public class RedirectUrlHelper {
    /**
     * 解析 state，返回真实的回调地址
     * @param state redirectUrl 中的 state 参数的值
     * @return 返回真实回调地址, 如果传入 state 为格式不正确，返回 null
     */
    public String decodeRedirectUrl(@NonNull String state) {
        // 解密 state
        byte[] router = Base64.getDecoder().decode(state.substring(state.indexOf(UUID_SEPARATOR) + 1));
        // 提取 redirectUrl
        Map<String, String> routerMap = ConvertUtil.string2Map(new String(router, UTF_8),
                                                               URL_PARAMETER_SEPARATOR,
                                                               KEY_VALUE_SEPARATOR);
        return routerMap.get(CALLBACK_URL_KEY_IN_STATE);
    }
}
