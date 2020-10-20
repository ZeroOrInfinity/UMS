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

package top.dcenter.ums.security.core.api.controller;


import top.dcenter.ums.security.core.auth.controller.ClientSecurityController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 客户端 url 认证与授权的路由控制实现此接口并注册到 IOC 容器，则会替换
 * {@link ClientSecurityController} 类
 * @author YongWu zheng
 * @version V1.0
 * Created by 2020/5/22 17:26
 */
public interface BaseSecurityController {

    /**
     * 当需要身份认证时，跳转到这里, 根据不同 uri(支持通配符) 跳转到不同的认证入口.<br><br>
     * 必须添加注解：<pre>
     *     \@RequestMapping(loginUnAuthenticationRoutingUrl)
     *     public void requireAuthentication(HttpServletRequest request, HttpServletResponse response) {
     *          ...
     *     }
     * </pre>
     * @param request   request
     * @param response  response
     */
    void requireAuthentication(HttpServletRequest request, HttpServletResponse response);

}