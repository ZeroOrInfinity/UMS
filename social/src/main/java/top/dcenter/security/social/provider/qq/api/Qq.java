package top.dcenter.security.social.provider.qq.api;

import java.io.IOException;

/**
 * social 第三方登录接口
 * @author zhailiang
 * @author  zyw
 * @version V1.0
 * Created by 2020/5/8 20:10
 */
public interface Qq {

    String EXPIRES_IN = "expires_in";
    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";
    String SCOPE = "scope";

    /**
     * 获取 Qq 用户信息
     * @return  如果没有此用户则返回 null
     * @throws IOException
     */
    QqUserInfo getUserInfo() throws IOException;
}
