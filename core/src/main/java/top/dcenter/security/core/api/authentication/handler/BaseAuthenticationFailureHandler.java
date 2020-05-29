package top.dcenter.security.core.api.authentication.handler;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * 认证失败处理器, 继承此类后，再向 IOC 容器注册自己来实现自定义功能。
 * @author zyw
 * @version V1.0  Created by 2020/5/29 12:32
 */
public abstract class BaseAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
}
