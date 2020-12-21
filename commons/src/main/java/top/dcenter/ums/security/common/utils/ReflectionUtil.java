/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package top.dcenter.ums.security.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 反射工具类, 功能: <br>
 * 1. 去掉 Controller 的 Mapping 动作<br>
 * 2. Controller 在 mvc 中做 Uri 映射等动作<br>
 * 3. 设置字段值<br>
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.16 11:41
 */
public final class ReflectionUtil {

    private ReflectionUtil() { }

    private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);
    /**
     * 反射设置字段值
     * @param fieldName     字段名称
     * @param fieldValue    要设置的字段值
     * @param targetObject  目标对象, 设置静态字段时为 null 值
     * @param targetClass   目标的 Class
     * @throws NoSuchFieldException     反射异常
     * @throws IllegalAccessException   反射异常
     */
    public static void setFieldValue(@NonNull String fieldName, @NonNull Object fieldValue,
                                     @Nullable Object targetObject, @NonNull Class<?> targetClass) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = targetClass.getDeclaredField(fieldName);
        declaredField.setAccessible(true);
        declaredField.set(targetObject, fieldValue);
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
     * 给 targetClass 的 methodName 方法上的 @RequestMapping 的 value 重新赋值为 requestMappingUri
     *
     * @param methodName            method name
     * @param requestMappingUri     request mapping uri
     * @param clz                   method 的 class
     * @param parameterTypes        the parameter array
     * @throws Exception    Exception
     */
    @SuppressWarnings("unchecked")
    public static void setRequestMappingUri(@NonNull String methodName, @NonNull String requestMappingUri,
                                            @NonNull Class<?> clz, Class<?>... parameterTypes) throws Exception {
        Method method = clz.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);

        // 获取 RequestMapping 注解
        final RequestMapping mappingAnnotation = method.getDeclaredAnnotation(RequestMapping.class);
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
            throw new RuntimeException(msg);
        }
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

}
