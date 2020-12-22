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
package top.dcenter.ums.security.core.tasks.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.dcenter.ums.security.common.executor.RejectedExecutionHandlerPolicy;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务调度线程池属性配置
 * @author YongWu zheng
 * @version V2.0  Created by  2020-12-22 11:12
 */
@Setter
@Getter
@ConfigurationProperties("ums.executor.job-task-scheduled-executor")
public class JobTaskScheduledExecutorProperties {

    /**
     * 线程池中空闲时保留的线程数, 默认: 0
     */
    private Integer corePoolSize = 0;
    /**
     * keep alive time, 默认: 10
     */
    private Integer keepAliveTime = 10;
    /**
     * keepAliveTime 时间单位, 默认: 毫秒
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    /**
     * 线程池名称, 默认: jobTask
     */
    private String poolName = "jobTask";
    /**
     * 拒绝策略, 默认: ABORT
     */
    private RejectedExecutionHandlerPolicy rejectedExecutionHandlerPolicy = RejectedExecutionHandlerPolicy.ABORT;
    /**
     * 线程池关闭过程的超时时间, 默认: PT10S
     */
    private Duration executorShutdownTimeout = Duration.ofSeconds(10);
}