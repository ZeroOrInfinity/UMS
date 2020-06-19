package top.dcenter.security.social.provider.gitee.connect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.social.ApiException;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import top.dcenter.security.social.provider.gitee.api.Gitee;
import top.dcenter.security.social.provider.gitee.api.GiteeUserInfo;

import java.io.IOException;

/**
 * gitee 服务适配器
 * @author  zyw
 * @version V1.0  Created by 2020/5/8 21:57
 */
@Slf4j
public class GiteeAdapter implements ApiAdapter<Gitee> {


    private String providerId;

    public GiteeAdapter() {
    }

    public GiteeAdapter(String providerId) {
        this.providerId = providerId;
    }

    @Override
    public boolean test(Gitee api) {
        return true;
    }

    @Override
    public void setConnectionValues(Gitee api, ConnectionValues values) throws ApiException {
        try
        {
            GiteeUserInfo userInfo = api.getUserInfo();
            values.setDisplayName(userInfo.getName());
            values.setImageUrl(userInfo.getAvatarUrl());
            values.setProfileUrl(userInfo.getHtmlUrl());
            values.setProviderUserId(userInfo.getId().toString());
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new ApiException(providerId, e.getMessage(), e);
        }
    }

    @Override
    public UserProfile fetchUserProfile(Gitee api) {
        // gitee no homepage
        GiteeUserInfo userInfo;
        try
        {
            userInfo = api.getUserInfo();
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
            return null;
        }
        return new UserProfile(userInfo.getId().toString(),
                               userInfo.getName(),
                               null,
                               null,
                               userInfo.getEmail(),
                               userInfo.getName());
    }

    @Override
    public void updateStatus(Gitee api, String message) {
        // dto nothing
    }
}
