package top.dcenter.ums.security.core.api.authentication.handler;

import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.util.Set;

/**
 * 认证成功处理器, 继承此类后，再向 IOC 容器注册自己来实现自定义功能。
 * @author zyw
 * @version V1.0  Created by 2020/5/29 12:32
 */
public abstract class BaseAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    protected boolean useReferer;
    /**
     * 使用 userReferer 时, 如果 referer 是属于 loginUrls, 则跳转到 defaultTargetUrl
     */
    @Getter
    protected Set<String> loginUrls;

    /**
     * 添加 loginUrl
     * @param loginUrl  loginUrl
     */
    public void addLoginUrl(@NonNull String loginUrl) {
        loginUrls.add(loginUrl);
    }
}
