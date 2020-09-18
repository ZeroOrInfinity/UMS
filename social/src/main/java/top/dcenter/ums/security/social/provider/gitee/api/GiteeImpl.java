package top.dcenter.ums.security.social.provider.gitee.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import top.dcenter.ums.security.social.provider.gitee.adapter.AbstractOAuth2ApiBinding;
import top.dcenter.ums.security.social.provider.gitee.adapter.GiteeTokenStrategy;

import java.io.IOException;

/**
 * 请求返回信息绑定服务实现
 * @author zyw
 * @version V1.0  Created by 2020/5/8 20:13
 */
@Getter
@Setter
@Slf4j
public class GiteeImpl extends AbstractOAuth2ApiBinding implements Gitee {

    /**
     * 获取 Gitee userInfo 链接
     */
    public static final String URL_GET_USER_INFO = "https://gitee.com/api/v5/user?access_token=%s";
    private String accessToken;

    private final ObjectMapper objectMapper;

    public GiteeImpl(String accessToken, ObjectMapper objectMapper) {
        super(accessToken, GiteeTokenStrategy.ACCESS_TOKEN_PARAMETER);
        this.accessToken = accessToken;
        this.objectMapper = objectMapper;

    }

    @Override
    public GiteeUserInfo getUserInfo() throws IOException {
        String url = String.format(URL_GET_USER_INFO, accessToken);
        String response = getRestTemplate().getForObject(url, String.class);
        if (log.isDebugEnabled())
        {
            log.debug("gitee userInfo = {}", response);
        }
        GiteeUserInfo giteeUserInfo = objectMapper.readValue(response, GiteeUserInfo.class);
        return giteeUserInfo;
    }

}
