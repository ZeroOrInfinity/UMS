package top.dcenter.web.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 模拟队列, 未实现多线程安全问题
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/2 23:12
 */
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
     * @param placeOrder
     * @throws InterruptedException
     */
    public void setPlaceOrder(String placeOrder) throws InterruptedException {
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
