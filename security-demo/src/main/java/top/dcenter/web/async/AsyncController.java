package top.dcenter.web.async;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/2 22:41
 */
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
    public DeferredResult<String> order() throws InterruptedException {
        log.info("AsyncController.order 主线程开启");

        String orderNumber = RandomStringUtils.randomNumeric(8);
        mockQueue.setPlaceOrder(orderNumber);

        DeferredResult<String> result = new DeferredResult<>();
        deferredResultHolder.getMap().put(orderNumber, result);

//        Callable<String> result = () -> {
//            log.info("Callable 副线程开启");
//            TimeUnit.SECONDS.sleep(1);
//            log.info("Callable 副线程关闭");
//            return "success";
//        };
        log.info("AsyncController.order 主线程结束");
        return result;
    }
}
