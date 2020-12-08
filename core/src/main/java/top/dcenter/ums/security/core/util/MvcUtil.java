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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.UrlPathHelper;
import top.dcenter.ums.security.core.auth.config.SecurityAutoConfiguration;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.dcenter.ums.security.common.consts.RegexConstants.DIGITAL_REGEX;
import static top.dcenter.ums.security.common.consts.RegexConstants.DOMAIN_REGEX;
import static top.dcenter.ums.security.common.consts.RegexConstants.TOP_DOMAIN_INDEX;
import static top.dcenter.ums.security.common.consts.RegexConstants.URL_SCHEME_REGEX;

/**
 * 功能: <br>
 * 1. 去掉 Controller 的 Mapping 动作<br>
 * 2. Controller 在 mvc 中做 Uri 映射等动作<br>
 * 3. 获取 servletContextPath<br>
 * 4. 获取 {@link UrlPathHelper}<br>
 * 5. 获取 本应用的一级域名<br>
 * 6. 获取 本应用的一级域名<br>
 * 7. 检查 redirectUrl 是否是本应用的域名, 防止跳转到外链<br>
 * 8. 从 request 中获取一级域名
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/17 18:32
 */
@Slf4j
public class MvcUtil {

    public static final String TOP_DOMAIN_PARAM_NAME = "topDomain";

    public static final String IP6_SEPARATOR = ":";

    public static final String LOCALHOST = "localhost";

    /**
     * servletContextPath, 在应用启动时通过 {@link SecurityAutoConfiguration} 自动注入.
     */
    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
    private static String servletContextPath = "";

    /**
     * 一级域名(不包括二级域名), 比如: www.example.com -> example.com, www.example.com.cn -> example.com.cn
     * 测试时用的 IP 或 localhost 直接原样设置就行.
     * 在应用启动时通过 {@link SecurityAutoConfiguration} 自动注入.
     */
    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
    private static String topDomain = "";

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
     * 获取一级域名, 通过属性 {@code ums.client.topDomain} 设置此值.
     * @return  返回一级域名
     */
    public static String getTopDomain() {
        if (StringUtils.hasText(topDomain)) {
            return topDomain;
        }
        throw new RuntimeException("topDomain 未初始化, 可通过属性 ums.client.topDomain 设置此值.");
    }

    /**
     * 从 request 中获取一级域名, ip6 或 ip4 或 localhost 时直接原样返回. 例如:
     * <pre>
     * www.example.com -> example.com,
     * aaa.bbb.example.top -> example.top,
     * www.example.com.cn -> example.com.cn,
     * aaa.bbb.example.com.cn -> example.com.cn,
     * 127.0.0.1 -> 127.0.0.1,
     * ABCD:EF01:2345:6789:ABCD:EF01:2345:6789 -> ABCD:EF01:2345:6789:ABCD:EF01:2345:6789,
     * localhost -> localhost
     * </pre>
     * @param request   request
     * @return  返回一级域名
     */
    public static String getTopDomain(HttpServletRequest request) {
        String serverName = request.getServerName();
        // 排除 ip6
        if (serverName.contains(IP6_SEPARATOR)) {
            return serverName;
        }
        // 排除 localhost
        if (serverName.equalsIgnoreCase(LOCALHOST)) {
            return serverName;
        }
        // 排除 ip4
        int lastIndexOf = serverName.lastIndexOf(".");
        if (Pattern.matches(DIGITAL_REGEX, serverName.substring(lastIndexOf + 1))) {
            return serverName;
        }
        // 提取一级域名并返回
        Pattern pattern = Pattern.compile(DOMAIN_REGEX);
        Matcher matcher = pattern.matcher(serverName);
        if (matcher.find()) {
            return matcher.group(TOP_DOMAIN_INDEX);
        }
        return serverName;
    }

    /**
     * 检查 redirectUrl 是否是本应用的域名, 防止跳转到外链
     * @param redirectUrl   要跳转的目标地址
     * @return  返回 true 时表示是本应用的链接
     */
    public static boolean isSelfTopDomain(String redirectUrl) {
        Pattern pattern = Pattern.compile(URL_SCHEME_REGEX);
        Matcher matcher = pattern.matcher(redirectUrl);
        if (matcher.find()) {
            String uri = matcher.replaceFirst("");
            int indexOf = uri.indexOf("/");
            if (indexOf != -1) {
                uri = uri.substring(0, indexOf);
            }
            return uri.contains(topDomain);
        }
        return true;
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