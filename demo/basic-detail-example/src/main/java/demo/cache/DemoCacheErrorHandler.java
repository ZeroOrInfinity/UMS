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

package demo.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Cacheable 操作自定义异常处理: 注入 IOC 容器即可自动注入 {@link CachingConfigurerSupport}, 当然也可自定义 {@link CachingConfigurerSupport}.<br>
 * 异常处理在日志中打印出错误信息，但是放行，保证redis服务器出现连接等问题的时候不影响程序的正常运行，使得能够出问题时不用缓存
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/18 12:06
 */
@Component
@Slf4j
public class DemoCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key) {
        log.error("redis异常：cacheName={}, key={}", cache.getName(), key, e);
    }

    @Override
    public void handleCachePutError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key, Object value) {
        log.error("redis异常：cacheName={}, key={}", cache.getName(), key, e);
    }

    @Override
    public void handleCacheEvictError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key) {
        log.error("redis异常：cacheName={}, key={}", cache.getName(), key, e);
    }

    @Override
    public void handleCacheClearError(@NonNull RuntimeException e, @NonNull Cache cache) {
        log.error("redis异常：cacheName={}, ", cache.getName(), e);
    }
}