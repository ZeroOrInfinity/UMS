package top.dcenter.ums.security.core.oauth.justauth.util;

import com.xkcoding.http.config.HttpConfig;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.beans.BeanUtils;
import top.dcenter.ums.security.core.oauth.justauth.request.Auth2DefaultRequest;
import top.dcenter.ums.security.core.oauth.entity.AuthTokenPo;
import top.dcenter.ums.security.core.oauth.entity.ConnectionData;

/**
 * JustAuth util
 * @author zyw
 * @version V1.0  Created by 2020/10/6 22:00
 */
public class JustAuthUtil {

    /**
     * 根据传入的参数生成 {@link AuthTokenPo}
     * @param token         {@link AuthToken}
     * @param providerId    第三方服务商 ID, 如: qq, github
     * @param timeout       {@link HttpConfig#getTimeout()}
     * @return {@link AuthTokenPo}
     */
    public static AuthTokenPo getAuthTokenPo(AuthToken token, String providerId, Integer timeout) {
        AuthTokenPo authToken = new AuthTokenPo();
        BeanUtils.copyProperties(token, authToken);
        authToken.setProviderId(providerId);
        // 有效期转时间戳
        Auth2DefaultRequest.expireIn2Timestamp(timeout, token.getExpireIn(), authToken);
        return authToken;
    }

    /**
     * 根据传入的参数生成 {@link ConnectionData}
     * @param providerId    第三方服务商 ID, 如: qq, github
     * @param authUser      {@link AuthUser}
     * @param userId        本地账户用户 Id
     * @param authToken     {@link AuthTokenPo}
     */
    public static ConnectionData getConnectionData(String providerId, AuthUser authUser, String userId,
                                              AuthTokenPo authToken) {
        // @formatter:off
        return ConnectionData.builder()
                             .userId(userId)
                             .displayName(authUser.getUsername())
                             .imageUrl(authUser.getAvatar())
                             .profileUrl(authUser.getBlog())
                             .providerId(providerId)
                             .providerUserId(authUser.getUuid())
                             .accessToken(authToken.getAccessToken())
                             .tokenId(authToken.getId())
                             .refreshToken(authToken.getRefreshToken())
                             .expireTime(authToken.getExpireTime())
                             .build();
        // @formatter:on
    }
}
