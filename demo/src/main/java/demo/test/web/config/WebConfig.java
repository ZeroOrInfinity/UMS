package demo.test.web.config;

import demo.test.interceptor.TimeInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import demo.test.filter.TimeFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * demo 模块配置
 * @author zhailiang
 * @author  zyw
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
