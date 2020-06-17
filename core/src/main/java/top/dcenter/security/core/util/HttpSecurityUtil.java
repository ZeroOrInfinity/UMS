package top.dcenter.security.core.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.GenericApplicationListenerAdapter;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.security.context.DelegatingApplicationListener;

/**
 * HttpSecurity tools
 * @author zyw
 * @version V1.0  Created by 2020/6/8 22:45
 */
public class HttpSecurityUtil {

    /**
     * 注册 {@link ApplicationListener} 到 {@link DelegatingApplicationListener}
     * @param applicationContext  {@link ApplicationContext}
     * @param delegate  {@link ApplicationListener}
     */
    public static void registerDelegateApplicationListener(ApplicationContext applicationContext,
                                                           ApplicationListener<?> delegate) {

        if (applicationContext.getBeansOfType(DelegatingApplicationListener.class).isEmpty()) {
            return;
        }
        DelegatingApplicationListener delegating = applicationContext.getBean(DelegatingApplicationListener.class);
        SmartApplicationListener smartListener = new GenericApplicationListenerAdapter(delegate);
        delegating.addListener(smartListener);
    }
}
