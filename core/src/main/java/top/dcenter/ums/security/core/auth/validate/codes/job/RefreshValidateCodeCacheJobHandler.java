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
package top.dcenter.ums.security.core.auth.validate.codes.job;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import top.dcenter.ums.security.core.api.validate.code.job.RefreshValidateCodeCacheJob;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static top.dcenter.ums.security.core.util.MvcUtil.setScheduledCron;

/**
 * 刷新验证码缓存的的定时任务默认实现: 刷新图片验证码与滑块验证码缓存
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/2 10:28
 */
public class RefreshValidateCodeCacheJobHandler implements InitializingBean {

    private final ValidateCodeProperties validateCodeProperties;

    private final ScheduledExecutorService jobTaskScheduledExecutor;

    @Autowired
    private Map<String, RefreshValidateCodeCacheJob> refreshValidateCodeJobMap;

    public RefreshValidateCodeCacheJobHandler(ValidateCodeProperties validateCodeProperties,
                                              @Qualifier("jobTaskScheduledExecutor") ScheduledExecutorService jobTaskScheduledExecutor) {
        this.validateCodeProperties = validateCodeProperties;
        this.jobTaskScheduledExecutor = jobTaskScheduledExecutor;
    }

    @Scheduled(cron = "0 * 4 * * ?")
    public void refreshValidateCodeJob() {
        this.jobTaskScheduledExecutor.schedule(() -> {
            if (this.refreshValidateCodeJobMap == null) {
                return;
            }
            Collection<RefreshValidateCodeCacheJob> validateCodeJobs = this.refreshValidateCodeJobMap.values();
            // 刷新验证码缓存
            validateCodeJobs.forEach(RefreshValidateCodeCacheJob::refreshValidateCodeJob);
        }, 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 动态注入 refreshValidateCodeJob() Scheduled 的映射 cron
        String methodName = "refreshValidateCodeJob";
        if (validateCodeProperties.getEnableRefreshValidateCodeJob())
        {
            setScheduledCron(methodName, validateCodeProperties.getRefreshValidateCodeJobCron(), this.getClass());
        }
    }
}
