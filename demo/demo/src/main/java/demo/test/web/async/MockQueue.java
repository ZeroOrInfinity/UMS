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
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 模拟队列, 未实现多线程安全问题
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/2 23:12
 */
@SuppressWarnings("AlibabaAvoidManuallyCreateThread")
@Slf4j
@Component
public class MockQueue {
    private String placeOrder;
    private volatile String completeOrder;

    public String getPlaceOrder() {
        return placeOrder;
    }

    /**
     * 模拟队列, 未实现多线程安全问题
     * @param placeOrder    placeOrder
     */
    public void setPlaceOrder(String placeOrder) {
        new Thread(() -> {
            log.info("接到下单请求：{}", placeOrder);
            try
            {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e)
            {
                log.error("睡眠时出错！", e);
            }
            this.completeOrder = placeOrder;
            log.info("下单请求处理完毕: {}", placeOrder);
        }, "placeOrderThread").start();
    }

    public String getCompleteOrder() {
        return completeOrder;
    }

    public void setCompleteOrder(String completeOrder) {
        this.completeOrder = completeOrder;
    }
}