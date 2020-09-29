package top.dcenter.ums.security.social.provider.qq.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import java.io.IOException;

/**
 * 请求返回信息绑定服务实现
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/8 20:13
 */
@Getter
@Setter
@Slf4j
public class QqImpl extends AbstractOAuth2ApiBinding implements Qq {

    /**
     * 获取 Qq openid 链接
     */
    public static final String URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=%S";
    /**
     * 获取 Qq userInfo 链接
     */
    public static final String URL_GET_USER_INFO = "https://graph.qq.com/user/get_user_info?oauth_consumer_key=%s&openid=%s";
    private String appId;
    private String openId;

    private final ObjectMapper objectMapper;

    public QqImpl(String accessToken, String appId, ObjectMapper objectMapper) {
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
        this.appId = appId;

        String url = String.format(URL_GET_OPENID, accessToken);
        // callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"} )
        String callback = getRestTemplate().getForObject(url, String.class);
        if (log.isDebugEnabled())
        {
            log.debug("qq openid: {}", callback);
        }
        this.openId = StringUtils.substringBetween(callback, "\"openid\":\"", "\"}");

        this.objectMapper = objectMapper;
    }

    @Override
    public QqUserInfo getUserInfo() throws IOException {
        String url = String.format(URL_GET_USER_INFO, appId, openId);
        String callback = getRestTemplate().getForObject(url, String.class);
        if (log.isDebugEnabled())
        {
            log.debug("qq userInfo = {}", callback);
        }
        QqUserInfo qqUserInfo = objectMapper.readValue(callback, QqUserInfo.class);
        qqUserInfo.setOpenId(openId);
        return qqUserInfo;
    }

}
