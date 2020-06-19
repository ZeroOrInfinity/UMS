package top.dcenter.web.async;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 队列监听器
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/2 23:28
 */
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
    public void onApplicationEvent(ContextRefreshedEvent event) {
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
