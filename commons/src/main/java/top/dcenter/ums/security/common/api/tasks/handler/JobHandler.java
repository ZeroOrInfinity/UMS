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
package top.dcenter.ums.security.common.api.tasks.handler;

/**
 * 任务处理器接口, 继承此接口并注入 IOC 容器, {@code top.dcenter.ums.security.core.tasks.config.ScheduleAutoConfiguration}
 * 会自动注册到任务注册器中.
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.21 17:07
 */
public interface JobHandler {

    /**
     * 根据 {@link #cronExp()} 运行定时任务
     */
    void run();

    /**
     * A cron-like expression.
     * <pre>
     * 0 * 2 * * ? 分别对应: second/minute/hour/day of month/month/day of week
     * </pre>
     * 默认为: "0 * 2 * * ?", 凌晨 2 点启动定时任务, 支持分布式(分布式 IOC 容器中必须有 {@code RedisConnectionFactory}, 也就是说,
     * 是否分布式执行依据 IOC 容器中是否有 {@code RedisConnectionFactory})
     * @see org.springframework.scheduling.support.CronSequenceGenerator
     * @return  返回 cron 表达式
     */
    String cronExp();
}
