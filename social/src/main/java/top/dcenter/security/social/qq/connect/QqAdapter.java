package top.dcenter.security.social.qq.connect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.social.ApiException;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import top.dcenter.security.social.qq.api.Qq;
import top.dcenter.security.social.qq.api.QqUserInfo;

/**
 * QQ 服务适配器
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/8 21:57
 */
@Slf4j
public class QqAdapter implements ApiAdapter<Qq> {


    private String providerId;

    public QqAdapter() {
    }

    public QqAdapter(String providerId) {
        this.providerId = providerId;
    }

    @Override
    public boolean test(Qq api) {
        return true;
    }

    @Override
    public void setConnectionValues(Qq api, ConnectionValues values) throws ApiException {
        try
        {
            QqUserInfo userInfo = api.getUserInfo();
            values.setDisplayName(userInfo.getNickname());
            values.setImageUrl(userInfo.getFigureurl_qq_1());
            values.setProfileUrl(null);
            values.setProviderUserId(userInfo.getOpenId());
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new ApiException(providerId, e.getMessage(), e);
        }
    }

    @Override
    public UserProfile fetchUserProfile(Qq api) {
        // qq no homepage
        return null;
    }

    @Override
    public void updateStatus(Qq api, String message) {
        // do nothing
    }
}
