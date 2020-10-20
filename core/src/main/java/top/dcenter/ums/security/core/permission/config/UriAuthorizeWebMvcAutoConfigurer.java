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

package top.dcenter.ums.security.core.permission.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.dcenter.ums.security.core.permission.interceptor.UriAuthorizationAnnotationInterceptor;

/**
 * 注册 的 uri 访问权限控制拦截器
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/8 11:21
 */
@Configuration
@AutoConfigureAfter({UriAuthorizeInterceptorAutoConfiguration.class})
public class UriAuthorizeWebMvcAutoConfigurer implements WebMvcConfigurer {
    
    private UriAuthorizationAnnotationInterceptor uriAuthorizationAnnotationInterceptor;

    public UriAuthorizeWebMvcAutoConfigurer(UriAuthorizationAnnotationInterceptor uriAuthorizationAnnotationInterceptor) {
        this.uriAuthorizationAnnotationInterceptor = uriAuthorizationAnnotationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(uriAuthorizationAnnotationInterceptor);

    }
}