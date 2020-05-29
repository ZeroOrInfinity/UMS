package top.dcenter.web.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异步结构 Holder
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/2 23:17
 */
@Slf4j
@Component
public class DeferredResultHolder {
    private Map<String, DeferredResult<String>> map = new ConcurrentHashMap<>();

    public Map<String, DeferredResult<String>> getMap() {
        return map;
    }

    public void setMap(Map<String, DeferredResult<String>> map) {
        this.map = map;
    }
}
