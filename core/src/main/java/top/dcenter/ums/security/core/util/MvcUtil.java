package top.dcenter.ums.security.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.UrlPathHelper;
import top.dcenter.ums.security.core.config.SecurityAutoConfiguration;

import java.lang.reflect.Method;

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
     * {@link UrlPathHelper}, 在应用启动时通过 {@link SecurityAutoConfiguration} 自动注入.
     */
    private static UrlPathHelper urlPathHelper = null;

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
    private static void unregisterController(String controllerBeanName, GenericApplicationContext applicationContext) {

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
    public static void registerController(String controllerBeanName, GenericApplicationContext applicationContext,
                                          Class<?> clz) throws Exception {
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

}
