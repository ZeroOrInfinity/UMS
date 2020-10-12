package top.dcenter.ums.security.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.GenericApplicationListenerAdapter;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.context.DelegatingApplicationListener;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.UrlPathHelper;
import top.dcenter.ums.security.core.config.SecurityAutoConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 功能: <br>
 * 1. 去掉 Controller 的 Mapping 动作<br>
 * 2. Controller 在 mvc 中做 Uri 映射等动作<br>
 * 3. 获取 servletContextPath<br>
 * 4. 获取 {@link UrlPathHelper}<br>
 * @author zyw
 * @version V1.0  Created by 2020/9/17 18:32
 */
@Slf4j
public class MvcUtil {

    /**
     * servletContextPath, 在应用启动时通过 {@link SecurityAutoConfiguration} 自动注入.
     */
    private static String servletContextPath = "";

    /**
     * {@link UrlPathHelper}
     */
    private volatile static UrlPathHelper urlPathHelper = null;

    /**
     * 获取 {@link UrlPathHelper}
     * @return  UrlPathHelper
     */
    public static UrlPathHelper getUrlPathHelper() {
        if (urlPathHelper == null)
        {
            synchronized (MvcUtil.class)
            {
                if (urlPathHelper == null)
                {
                    UrlPathHelper helper = new UrlPathHelper();
                    helper.setAlwaysUseFullPath(true);
                    urlPathHelper = helper;
                }
            }
        }
        return urlPathHelper;
    }

    /**
     * 获取 servletContextPath
     * @return servletContextPath
     */
    public static String getServletContextPath() {
        return servletContextPath;
    }

    /**
     * 去掉 Controller 的 Mapping
     *
     * @param controllerBeanName    在 IOC 容器中注册的 controllerBeanName
     * @param applicationContext    applicationContext
     */
    private static void unregisterController(@NonNull String controllerBeanName, @NonNull GenericApplicationContext applicationContext) {

        final RequestMappingHandlerMapping requestMappingHandlerMapping =
                (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping");

        // 检测 controllerBeanName 是否在 IOC 容器, 没有直接抛异常
        Object controller = applicationContext.getBean(controllerBeanName);

        final Class<?> targetClass = controller.getClass();
        ReflectionUtils.doWithMethods(targetClass, method ->
        {
            Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
            try
            {
                Method createMappingMethod = RequestMappingHandlerMapping.class.
                        getDeclaredMethod("getMappingForMethod", Method.class, Class.class);
                createMappingMethod.setAccessible(true);
                RequestMappingInfo requestMappingInfo = (RequestMappingInfo)
                        createMappingMethod.invoke(requestMappingHandlerMapping, specificMethod, targetClass);
                if (requestMappingInfo != null)
                {
                    requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);

    }

    /**
     * 在 mvc 中做 Uri 映射等动作
     *
     * @param controllerBeanName    在 IOC 容器中注册的 controllerBeanName
     * @param applicationContext    applicationContext
     * @param clz    controllerBeanName 的 class, 可以是父类的 class, 可以为 null
     * @throws Exception    Exception
     */
    public static void registerController(@NonNull String controllerBeanName, @NonNull GenericApplicationContext applicationContext,
                                          @Nullable Class<?> clz) throws Exception {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)
                applicationContext.getBean("requestMappingHandlerMapping");

        try {
            // 检测 controllerBeanName 是否在 IOC 容器, 没有直接抛异常
            applicationContext.getBean(controllerBeanName);
        }
        catch (Exception e) {
            if (clz != null)
            {
                Object bean = applicationContext.getBean(clz);
                log.info("{} 没有在 IOC 容器中, 已被 {} 替代, 无须再做 Uri 映射",
                         controllerBeanName,
                         bean.getClass().getName());
                return;
            }
            throw e;
        }

        unregisterController(controllerBeanName, applicationContext);
        // 在mvc中做Uri映射等动作
        Method method = requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().
                getDeclaredMethod("detectHandlerMethods", Object.class);
        method.setAccessible(true);
        method.invoke(requestMappingHandlerMapping, controllerBeanName);
        log.info("{} 在 mvc 中做 Uri 映射等动作成功", controllerBeanName);
    }


    /**
     * 给 clz 的 methodName 方法上的 @RequestMapping 的 value 重新赋值为 requestMappingUri
     *
     * @param methodName            methodName
     * @param requestMappingUri     requestMappingUri
     * @param clz                   methodName 的 class
     * @param parameterTypes        the parameter array
     * @throws Exception    Exception
     */
    @SuppressWarnings("unchecked")
    public static void setRequestMappingUri(@NonNull String methodName, @NonNull String requestMappingUri,
                                            @NonNull Class<?> clz, Class<?>... parameterTypes) throws Exception {
        Method sliderCheckMethod = clz.getDeclaredMethod(methodName, parameterTypes);
        sliderCheckMethod.setAccessible(true);

        // 获取 RequestMapping 注解
        RequestMapping mappingAnnotation = sliderCheckMethod.getDeclaredAnnotation(RequestMapping.class);
        if (null != mappingAnnotation) {
            // 获取 RequestMapping 中 value 值
            String[] paths = mappingAnnotation.value();
            if (paths.length > 0) {
                // 设置最终的属性值
                paths[0] = requestMappingUri;
                // 获取代理处理器
                InvocationHandler invocationHandler = Proxy.getInvocationHandler(mappingAnnotation);
                // 获取私有 memberValues 属性
                Field memberValuesField = invocationHandler.getClass().getDeclaredField("memberValues");
                memberValuesField.setAccessible(true);
                // 获取实例的属性map
                Map<String, Object> memberValuesValue = (Map<String, Object>) memberValuesField.get(invocationHandler);
                // 修改属性值
                memberValuesValue.put("value", paths);
            }
        }
        else
        {
            String msg = String.format("设置 %s#%s() 方法的 requestMapping 映射值时发生错误.",
                                       clz.getName(),
                                       methodName);
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }

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
