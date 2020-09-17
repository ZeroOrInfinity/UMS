package top.dcenter.security.core.permission.config;

import org.springframework.context.annotation.Configuration;

/**
 * 注入 IOC 意味着有需要验证权限的多个不同的 uri 对同一个 uri 都匹配的情况, {@link EnableUriAuthorize#repeat()} 为 true 时,
 * 且 filterOrInterceptor=true.<br>
 * 例如: 需要验证权限的两个 uri: /test/** 和 /test/permission/** 都对 /test/permission/1 匹配
 * @author zyw
 * @version V1.0  Created by 2020/9/17 8:53
 */
@Configuration
public class Repeat {
}
