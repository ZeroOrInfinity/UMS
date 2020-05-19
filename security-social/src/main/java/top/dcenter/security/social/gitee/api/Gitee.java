package top.dcenter.security.social.gitee.api;

import java.io.IOException;

/**
 * social 第三方登录接口
 * @author zyw
 * @version V1.0
 * Created by 2020/5/8 20:10
 */
public interface Gitee {

    String EXPIRES_IN = "expires_in";
    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";
    String SCOPE = "scope";
    String TOKEN_TYPE = "token_type";
    String CREATED_AT = "created_at";

    /**
     * 获取 Gitee 用户信息
     * @return  如果没有此用户则返回 null
     * @throws IOException
     */
    GiteeUserInfo getUserInfo() throws IOException;
}
