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

package top.dcenter.ums.security.core.tasks.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.dcenter.ums.security.common.executor.DefaultThreadFactory;
import top.dcenter.ums.security.common.executor.MdcScheduledThreadPoolTaskExecutor;
import top.dcenter.ums.security.core.tasks.properties.JobTaskScheduledExecutorProperties;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 实现 基于 SLF4J MDC 机制的日志链路追踪功能的 {@link MdcScheduledThreadPoolTaskExecutor}}
 * @author YongWu zheng
 * @version V1.0  Created by 2020-10-15 10:21
 */
@SuppressWarnings({"unused"})
@Configuration
@AutoConfigureAfter(value = {ScheduledPropertiesAutoConfiguration.class})
public class ScheduledExecutorAutoConfiguration implements DisposableBean {

    private final JobTaskScheduledExecutorProperties jobTaskScheduledExecutorProperties;
    private ScheduledExecutorService jobTaskScheduledExecutor;

    public ScheduledExecutorAutoConfiguration(JobTaskScheduledExecutorProperties jobTaskScheduledExecutorProperties) {
        this.jobTaskScheduledExecutorProperties = jobTaskScheduledExecutorProperties;
    }

    @Bean
    public ScheduledExecutorService jobTaskScheduledExecutor() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
                new MdcScheduledThreadPoolTaskExecutor(jobTaskScheduledExecutorProperties.getCorePoolSize(),
                                                       getThreadFactory(jobTaskScheduledExecutorProperties.getPoolName()),
                                                       jobTaskScheduledExecutorProperties.getRejectedExecutionHandlerPolicy()
                                                                                         .getRejectedHandler());
        scheduledThreadPoolExecutor.setKeepAliveTime(jobTaskScheduledExecutorProperties.getKeepAliveTime(),
                                                     jobTaskScheduledExecutorProperties.getTimeUnit());

        this.jobTaskScheduledExecutor = scheduledThreadPoolExecutor;
        return scheduledThreadPoolExecutor;
    }

    private ThreadFactory getThreadFactory(String poolName) {
        return new DefaultThreadFactory(poolName);
    }

    @Override
    public void destroy() throws Exception {

        if (jobTaskScheduledExecutor != null)
        {
            jobTaskScheduledExecutor.shutdown();
            jobTaskScheduledExecutor.awaitTermination(jobTaskScheduledExecutorProperties.getExecutorShutdownTimeout()
                                                                                        .toMillis(),
                                                      TimeUnit.MILLISECONDS);
            if (!jobTaskScheduledExecutor.isTerminated()) {
                // log.error("Processor did not terminate in time")
                jobTaskScheduledExecutor.shutdownNow();
            }
        }
    }

}