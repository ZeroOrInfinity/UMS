package top.dcenter.security.social.provider.weibo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;
import top.dcenter.security.core.util.RequestUtil;

import java.io.IOException;

import static top.dcenter.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_IDENTIFIER;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/18 12:04
 */
public class WeiboImpl extends AbstractOAuth2ApiBinding implements Weibo {

    /**
     * 获取 Weibo userInfo 链接
     */
    public static final String URL_GET_USER_INFO = "https://api.weibo.com/2/users/show.json";
    /**
     * 获取 userInfo 时传递的必要参数 uid 的参数名称
     */
    public static final String REQUEST_PARAMETER_UID = "uid";
    /**
     * access_token 参数名称
     */
    public static final String ACCESS_TOKEN = "access_token";

    private String accessToken;

    private final ObjectMapper objectMapper;

    public WeiboImpl(String accessToken, ObjectMapper objectMapper) {
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
        this.accessToken = accessToken;
        this.objectMapper = objectMapper;
    }


    @Override
    public WeiboUserInfo getUserInfo(String uid) throws IOException {
        StringBuilder url = new StringBuilder();
        url.append(URL_GET_USER_INFO)
            .append(URL_PARAMETER_IDENTIFIER)
            .append(ACCESS_TOKEN)
            .append(KEY_VALUE_SEPARATOR)
            .append(accessToken)
            .append(URL_PARAMETER_SEPARATOR)
            .append(REQUEST_PARAMETER_UID)
            .append(KEY_VALUE_SEPARATOR)
            .append(uid);
        String responseBody = getRestTemplate().getForObject(url.toString(), String.class);
        // 时间格式: Sun May 29 08:37:29 +0800 2011 -> EEE MMM dd HH:mm:ss ZZZ yyyy Locale.US
        return RequestUtil.requestBody2Object(objectMapper, WeiboUserInfo.class, responseBody);
    }
}
