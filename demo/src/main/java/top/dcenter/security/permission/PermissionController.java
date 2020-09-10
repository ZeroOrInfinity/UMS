package top.dcenter.security.permission;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.security.core.permission.annotation.UriAuthorize;

/**
 * @author zyw
 * @version V1.0  Created by 2020/9/9 22:49
 */
@RestController
@Slf4j
public class PermissionController {
    /**
     * 测试有 /test/permission:add 权限, 放行
     */
    @UriAuthorize("/test/permission:add")
    @GetMapping("/test/permission/{id}")
    public String testPermission(@PathVariable("id") String id) {
        return "test permission: " + id;
    }


    /**
     * 测试不匹配 /test/deny:add 权限, 禁止访问
     */
    @UriAuthorize("/test/deny:add")
    @GetMapping("/test/deny/{id}")
    public String testDeny(@PathVariable("id") String id) {
        return "test deny: " + id;
    }

    /**
     * 没有注释 @UriAuthorize 直接放行
     */
    @GetMapping("/test/pass/{id}")
    public String testPass(@PathVariable("id") String id) {
        return "test pass: " + id;
    }

}
