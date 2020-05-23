package top.dcenter.security.browser.api.controller;

import top.dcenter.security.browser.controller.BrowserSecurityController;

/**
 * 网页端 url 认证与授权的路由控制接口，无任何功能实现，只作为标识实现此接口类为 url 认证与授权的路由 Controller，实现此接口并注册到 IOC 容器，则会替换此类
 * {@link BrowserSecurityController}
 * @author zyw23
 * @version V1.0
 * Created by 2020/5/22 17:26
 */
public interface BaseBrowserSecurityController {
}
