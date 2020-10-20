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

package demo.test.web.config;

import demo.test.filter.TimeFilter;
import demo.test.interceptor.TimeInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * demo 模块配置
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/2 16:15
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TimeInterceptor timeInterceptor;

    public WebConfig(TimeInterceptor timeInterceptor) {
        this.timeInterceptor = timeInterceptor;
    }

    //    @Bean

    public FilterRegistrationBean timeFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new TimeFilter());
        List<String> urls = new ArrayList<>();
        urls.add("/*");
        filterRegistrationBean.setUrlPatterns(urls);
        return filterRegistrationBean;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
//        super.configureAsyncSupport(configurer);
//        configurer.registerDeferredResultInterceptors((DeferredResultProcessingInterceptor) this.timeInterceptor)
//                .setDefaultTimeout(300000)
//                .setTaskExecutor(new ConcurrentTaskExecutor(Executors.newFixedThreadPool(8)));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        HandlerInterceptor interceptor = this.timeInterceptor;
//        registry.addInterceptor(interceptor);
    }
}