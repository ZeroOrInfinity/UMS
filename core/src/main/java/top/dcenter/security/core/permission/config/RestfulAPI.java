package top.dcenter.security.core.permission.config;

import org.springframework.context.annotation.Configuration;

/**
 * 注入 IOC 表示为 restful API, 否则不是. <br>
 * restful API 与 非 restful API 在用使用 UriAuthorizeFilter 进行权限控制时有区别.
 * @author zyw
 * @version V1.0  Created by 2020/9/17 8:53
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Configuration
public class RestfulAPI {
}
