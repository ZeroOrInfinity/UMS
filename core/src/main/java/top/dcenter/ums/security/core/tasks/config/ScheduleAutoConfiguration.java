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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import top.dcenter.ums.security.core.api.mdc.MdcIdGenerator;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.mdc.MdcIdType;
import top.dcenter.ums.security.core.mdc.properties.MdcProperties;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;
import top.dcenter.ums.security.core.tasks.handler.RefreshAccessTokenJobHandler;
import top.dcenter.ums.security.core.tasks.handler.RefreshValidateCodeCacheJobHandler;

import java.util.concurrent.ScheduledExecutorService;

import static top.dcenter.ums.security.core.mdc.utils.MdcUtil.decorateTasks;

/**
 * 简单的任务定时任务调度配置
 * @author YongWu zheng
 * @version V2.0  Created by 2020.11.25 14:01
 */
@Configuration
@AutoConfigureAfter({TasksAutoConfiguration.class})
@EnableScheduling
public class ScheduleAutoConfiguration implements SchedulingConfigurer {

    private final ScheduledExecutorService jobTaskScheduledExecutor;
    private final Auth2Properties auth2Properties;
    private final ValidateCodeProperties validateCodeProperties;
    private final RefreshValidateCodeCacheJobHandler refreshValidateCodeCacheJobHandler;
    private final RefreshAccessTokenJobHandler refreshAccessTokenJobHandler;

    private final MdcIdType mdcIdType;
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private MdcIdGenerator mdcIdGenerator;

    public ScheduleAutoConfiguration(@Qualifier("jobTaskScheduledExecutor") ScheduledExecutorService jobTaskScheduledExecutor,
                                     Auth2Properties auth2Properties,
                                     ValidateCodeProperties validateCodeProperties,
                                     MdcProperties mdcProperties,
                                     @Autowired(required = false) RefreshValidateCodeCacheJobHandler refreshValidateCodeCacheJobHandler,
                                     @Autowired(required = false) RefreshAccessTokenJobHandler refreshAccessTokenJobHandler) {
        this.jobTaskScheduledExecutor = jobTaskScheduledExecutor;
        this.auth2Properties = auth2Properties;
        this.validateCodeProperties = validateCodeProperties;
        this.refreshValidateCodeCacheJobHandler = refreshValidateCodeCacheJobHandler;
        this.refreshAccessTokenJobHandler = refreshAccessTokenJobHandler;
        this.mdcIdType = mdcProperties.getType();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(this.jobTaskScheduledExecutor);
        if (this.refreshAccessTokenJobHandler != null) {
            // 刷新 AccessToken 定时任务
            taskRegistrar.addCronTask(decorateTasks(this.refreshAccessTokenJobHandler::refreshAccessTokenJob,
                                                    this.mdcIdType, this.mdcIdGenerator),
                                      auth2Properties.getRefreshTokenJobCron());
        }
        if (this.refreshValidateCodeCacheJobHandler != null) {
            // 刷新 验证码缓存 定时任务
            taskRegistrar.addCronTask(decorateTasks(this.refreshValidateCodeCacheJobHandler::refreshValidateCodeJob,
                                                    this.mdcIdType, this.mdcIdGenerator),
                                      validateCodeProperties.getRefreshValidateCodeJobCron());
        }
    }
}


