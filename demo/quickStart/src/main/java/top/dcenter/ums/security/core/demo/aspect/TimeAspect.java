package top.dcenter.ums.security.core.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 切面类型：对方法运行时间的统计
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/2 18:26
 */
@SuppressWarnings("AlibabaCommentsMustBeJavadocFormat")
@Aspect
@Component
@Slf4j
public class TimeAspect {
    @Around("execution(* top.dcenter.ums.security.core.oauth.repository.jdbc.Auth2JdbcUsersConnectionRepository.*(..))")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        long start = Instant.now().toEpochMilli();
        Object obj = pjp.proceed();
        log.info("{}.{} 耗时：{} {}", pjp.getSignature().getDeclaringType().getName(), pjp.getSignature().getName(),
                 (Instant.now().toEpochMilli() - start), "毫秒");
        return obj;
    }
    @Around("execution(* top.dcenter.ums.security.core.oauth.repository.jdbc.Auth2JdbcUsersConnectionTokenRepository.*(..))")
    public Object handlerControllerMethod1(ProceedingJoinPoint pjp) throws Throwable {
        long start = Instant.now().toEpochMilli();
        Object obj = pjp.proceed();
        log.info("{}.{} 耗时：{} {}", pjp.getSignature().getDeclaringType().getName(), pjp.getSignature().getName(),
                 (Instant.now().toEpochMilli() - start), "毫秒");
        return obj;
    }
}
