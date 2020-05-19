package top.dcenter.security.social.gitee.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import top.dcenter.security.social.gitee.api.Gitee;

/**
 * gitee 登录 ConnectionFactory
 * @author zyw
 * @version V1.0  Created by 2020/5/8 22:14
 */
public class GiteeConnectionFactory extends OAuth2ConnectionFactory<Gitee> {

    public GiteeConnectionFactory(String providerId, String appId, String appSecret, ObjectMapper objectMapper) {
        super(providerId, new GiteeServiceProvider(appId, appSecret, objectMapper), new GiteeAdapter(providerId));
    }

}
