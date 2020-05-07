package top.dcenter.service.impl;

import top.dcenter.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/2 14:42
 */
@Service
@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String greeting(String name) {
        log.info("HelloService " + name);
        return "hello " + name;
    }
}
