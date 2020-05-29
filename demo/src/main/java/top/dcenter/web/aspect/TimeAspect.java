package top.dcenter.web.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.time.Instant;
import java.util.Arrays;

/**
 * 切面类型：对方法运行时间的统计
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/2 18:26
 */
@Aspect
//@Component
@Slf4j
public class TimeAspect {
    @Around("execution(* top.dcenter..*Controller.*(..))")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        log.info("start ...");
        long start = Instant.now().toEpochMilli();
        if (log.isInfoEnabled())
        {
            Arrays.stream(pjp.getArgs()).forEach((arg) -> log.info(arg.toString()));
        }
        Object obj = pjp.proceed();
        log.info("end ... 耗时：{}", (Instant.now().toEpochMilli() - start));
        return obj;
    }
}
