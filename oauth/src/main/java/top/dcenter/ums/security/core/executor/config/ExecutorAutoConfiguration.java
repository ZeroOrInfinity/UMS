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

package top.dcenter.ums.security.core.executor.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import top.dcenter.ums.security.common.executor.DefaultThreadFactory;
import top.dcenter.ums.security.common.executor.MdcScheduledThreadPoolExecutor;
import top.dcenter.ums.security.common.executor.MdcThreadPoolExecutor;
import top.dcenter.ums.security.core.executor.properties.ExecutorProperties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 实现 基于 SLF4J MDC 机制的日志链路追踪功能的 {@link MdcScheduledThreadPoolExecutor} 与 {@link MdcThreadPoolExecutor}
 * 1. 第三方授权登录 AccessToken 维护有效期定时任务配置.<br>
 * 2. 第三方授权登录时, 异步更新用户的第三方授权用户信息的 Executor 属性配置
 * @author YongWu zheng
 * @version V1.0  Created by 2020-10-15 10:21
 */
@SuppressWarnings({"unused"})
@Configuration
@AutoConfigureAfter(value = {ExecutorPropertiesAutoConfiguration.class})
@EnableScheduling
public class ExecutorAutoConfiguration implements DisposableBean {

    private final ExecutorProperties executorProperties;
    private ExecutorService updateConnectionExecutorService;
    private ExecutorService refreshTokenExecutorService;

    public ExecutorAutoConfiguration(ExecutorProperties executorProperties) {
        this.executorProperties = executorProperties;
    }

    @Bean()
    @ConditionalOnProperty(prefix = "ums.oauth", name = "enabled", havingValue = "true")
    public ExecutorService refreshTokenTaskExecutor() {
        ExecutorProperties.RefreshTokenExecutorProperties refreshToken = executorProperties.getRefreshToken();
        ThreadPoolExecutor threadPoolExecutor =
                new MdcThreadPoolExecutor(refreshToken.getCorePoolSize(),
                                          refreshToken.getMaximumPoolSize(),
                                          refreshToken.getKeepAliveTime(),
                                          refreshToken.getTimeUnit(),
                                          new LinkedBlockingQueue<>(refreshToken.getBlockingQueueCapacity()),
                                          getThreadFactory(refreshToken.getPoolName()),
                                          refreshToken.getRejectedExecutionHandlerPolicy().getRejectedHandler());

        this.refreshTokenExecutorService = threadPoolExecutor;
        return threadPoolExecutor;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = "ums.oauth", name = "enabled", havingValue = "true")
    public ExecutorService updateConnectionTaskExecutor() {
        ExecutorProperties.UserConnectionUpdateExecutorProperties userConnectionUpdate = executorProperties.getUserConnectionUpdate();
        ThreadPoolExecutor threadPoolExecutor =
                new MdcThreadPoolExecutor(userConnectionUpdate.getCorePoolSize(),
                                          userConnectionUpdate.getMaximumPoolSize(),
                                          userConnectionUpdate.getKeepAliveTime(),
                                          userConnectionUpdate.getTimeUnit(),
                                          new LinkedBlockingQueue<>(userConnectionUpdate.getBlockingQueueCapacity()),
                                          getThreadFactory(userConnectionUpdate.getPoolName()),
                                          userConnectionUpdate.getRejectedExecutionHandlerPolicy().getRejectedHandler());
        this.updateConnectionExecutorService = threadPoolExecutor;
        return threadPoolExecutor;
    }

    private ThreadFactory getThreadFactory(String poolName) {
        return new DefaultThreadFactory(poolName);
    }


    public void shutdown() throws Exception {
        if (updateConnectionExecutorService != null)
        {
            updateConnectionExecutorService.shutdown();
            updateConnectionExecutorService.awaitTermination(executorProperties.getUserConnectionUpdate().getExecutorShutdownTimeout().toMillis(),
                                                             TimeUnit.MILLISECONDS);
            if (!updateConnectionExecutorService.isTerminated()) {
                // log.error("Processor did not terminate in time")
                updateConnectionExecutorService.shutdownNow();
            }
        }
    }

    @Override
    public void destroy() throws Exception {

        if (refreshTokenExecutorService != null)
        {
            refreshTokenExecutorService.shutdown();
            refreshTokenExecutorService.awaitTermination(executorProperties.getRefreshToken()
                                                                           .getExecutorShutdownTimeout().toMillis(),
                                                         TimeUnit.MILLISECONDS);
            if (!refreshTokenExecutorService.isTerminated()) {
                // log.error("Processor did not terminate in time")
                refreshTokenExecutorService.shutdownNow();
            }
        }

    }

}