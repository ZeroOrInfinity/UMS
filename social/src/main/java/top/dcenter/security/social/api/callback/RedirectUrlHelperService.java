package top.dcenter.security.social.api.callback;

import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * 解析 state，返回真实的回调地址，支持通过统一的回调地址路由到多个回调地址的解析助手。<br><br>
 *     注意：如果修改回调地址解密逻辑，同时要修改 {@link BaseOAuth2ConnectionFactory#buildReturnToUrl(HttpServletRequest, Set)} 与
 *     {@link BaseOAuth2ConnectionFactory#generateState(String)}的加密逻辑。<br><br>
 *     自定义此逻辑: 实现并注入 IOC 容器即可替换
 *
 * @author zyw23
 * @version V1.0
 * Created by 2020/9/15 21:46
 */
public interface RedirectUrlHelperService {

    /**
     * 解析 state，返回真实的回调地址
     * @param state redirectUrl 中的 state 参数的值
     * @return 返回真实回调地址, 如果传入 state 为格式不正确，返回 null
     */
    String decodeRedirectUrl(@NonNull String state);

}
