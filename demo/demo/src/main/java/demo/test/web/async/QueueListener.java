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

package demo.test.web.async;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 队列监听器
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/2 23:28
 */
@SuppressWarnings("AlibabaAvoidManuallyCreateThread")
@Component
@Slf4j
public class QueueListener implements ApplicationListener<ContextRefreshedEvent> {
    private final MockQueue mockQueue;
    private final DeferredResultHolder deferredResultHolder;

    public QueueListener(MockQueue mockQueue, DeferredResultHolder deferredResultHolder) {
        this.mockQueue = mockQueue;
        this.deferredResultHolder = deferredResultHolder;
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        new Thread(() -> {
            String orderNumber;
            while (true)
            {
                orderNumber = mockQueue.getCompleteOrder();
                if (StringUtils.isNotBlank(orderNumber))
                {
                    log.info("返回订单处理结果：{}", orderNumber);
                    deferredResultHolder.getMap().get(orderNumber).setResult("place order success " + orderNumber);
                    mockQueue.setCompleteOrder(null);
                } else
                {
                    try
                    {
                        TimeUnit.SECONDS.sleep(1);
                    }
                    catch (InterruptedException e)
                    {
                        log.error("线程睡眠出错！", e);
                    }

                }
            }
        }, "deferredResultThread").start();
    }
}