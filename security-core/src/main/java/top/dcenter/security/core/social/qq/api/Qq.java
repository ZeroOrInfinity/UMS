package top.dcenter.security.core.social.qq.api;

import java.io.IOException;

/**
 * @author zyw23
 * @version V1.0
 * Created by 2020/5/8 20:10
 */
public interface Qq {

    /**
     * 获取 Qq 用户信息
     * @return  如果没有此用户则返回 null
     * @throws IOException
     */
    QqUserInfo getUserInfo() throws IOException;
}
