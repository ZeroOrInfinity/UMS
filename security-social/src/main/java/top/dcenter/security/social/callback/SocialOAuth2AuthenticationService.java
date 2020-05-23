package top.dcenter.security.social.callback;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.security.provider.OAuth2AuthenticationService;

import javax.servlet.http.HttpServletRequest;

import static top.dcenter.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_IDENTIFIER;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;
import static top.dcenter.security.core.consts.SecurityConstants.URL_SEPARATOR;

/**
 * 通过覆写 buildReturnToUrl(request)方法, 使 social 支持通过统一的回调地址路由到多个回调地址。
 * @author zyw
 * @version V1.0  Created by 2020/5/21 15:54
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class SocialOAuth2AuthenticationService<S> extends OAuth2AuthenticationService<S> {
    public SocialOAuth2AuthenticationService(OAuth2ConnectionFactory<S> connectionFactory) {
        super(connectionFactory);
    }

    @Override
    protected String buildReturnToUrl(HttpServletRequest request) {
        StringBuffer sb = request.getRequestURL();
        sb.setLength(sb.lastIndexOf(URL_SEPARATOR));
        sb.append(URL_PARAMETER_IDENTIFIER);
        for (String name : getReturnToUrlParameters()) {
            // Assume for simplicity that there is only one value
            String value = request.getParameter(name);

            if (value == null) {
                continue;
            }
            sb.append(name).append(KEY_VALUE_SEPARATOR).append(value).append(URL_PARAMETER_SEPARATOR);
        }
        // strip trailing ? or &
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
