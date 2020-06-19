package top.dcenter.security.social.provider.gitee.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.dcenter.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.security.social.provider.gitee.api.Gitee;
import top.dcenter.security.social.properties.SocialProperties;

import static top.dcenter.security.core.consts.SecurityConstants.URL_SEPARATOR;

/**
 * gitee 登录 ConnectionFactory
 * @author zyw
 * @version V1.0  Created by 2020/5/8 22:14
 */
public class GiteeConnectionFactory extends BaseOAuth2ConnectionFactory<Gitee> {



    public GiteeConnectionFactory(String appId, String appSecret, ObjectMapper objectMapper, SocialProperties socialProperties) {
        super(socialProperties.getGitee().getProviderId(), new GiteeServiceProvider(appId, appSecret, objectMapper),
              new GiteeAdapter(socialProperties.getGitee().getProviderId()),
              socialProperties);
    }

    @Override
    public String generateState() {
        return generateState(this.socialProperties.getCallbackUrl() + URL_SEPARATOR + getProviderId());
    }

}
