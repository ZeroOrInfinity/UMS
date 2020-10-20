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
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * 异步控制器
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/2 22:41
 */
@SuppressWarnings("AlibabaRemoveCommentedCode")
@RestController
@Slf4j
public class AsyncController {

    private final MockQueue mockQueue;
    private final DeferredResultHolder deferredResultHolder;
    public AsyncController(MockQueue mockQueue, DeferredResultHolder deferredResultHolder) {
        this.mockQueue = mockQueue;
        this.deferredResultHolder = deferredResultHolder;
    }

    @GetMapping("/order")
    public DeferredResult<String> order() {
        log.info("AsyncController.order 主线程开启");

        String orderNumber = RandomStringUtils.randomNumeric(8);
        mockQueue.setPlaceOrder(orderNumber);

        DeferredResult<String> result = new DeferredResult<>();
        deferredResultHolder.getMap().put(orderNumber, result);

        //Callable<String> result = () -> {
        //    log.info("Callable 副线程开启");
        //    TimeUnit.SECONDS.sleep(1);
        //    log.info("Callable 副线程关闭");
        //    return "success";
        //}
        log.info("AsyncController.order 主线程结束");
        return result;
    }
}