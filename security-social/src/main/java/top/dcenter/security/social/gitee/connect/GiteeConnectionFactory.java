package top.dcenter.security.social.gitee.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.dcenter.security.social.properties.SocialProperties;
import top.dcenter.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.security.social.gitee.api.Gitee;

/**
 * gitee 登录 ConnectionFactory
 * @author zyw
 * @version V1.0  Created by 2020/5/8 22:14
 */
public class GiteeConnectionFactory extends BaseOAuth2ConnectionFactory<Gitee> {



    public GiteeConnectionFactory(String providerId, String appId, String appSecret, ObjectMapper objectMapper, SocialProperties socialProperties) {
        super(providerId, new GiteeServiceProvider(appId, appSecret, objectMapper), new GiteeAdapter(providerId), socialProperties);
    }

    @Override
    public String generateState() {
        return generateState(this.socialProperties.getCallbackUrl() + getProviderId());
    }

}
