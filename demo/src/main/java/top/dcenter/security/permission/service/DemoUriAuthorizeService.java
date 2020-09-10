package top.dcenter.security.permission.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import top.dcenter.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.security.core.permission.UriResources;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * request 的 uri 访问权限控制服务.<br>
 * 实现 {@link AbstractUriAuthorizeService} 抽象类并注入 IOC 容器即可替换此类.
 * @author zyw
 * @version V1.0  Created by 2020/9/8 21:54
 */
@Component
@Slf4j
public class DemoUriAuthorizeService extends AbstractUriAuthorizeService {

    private AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public Optional<Map<String, Map<String, UriResources>>> getRolesAuthorities() {

        // 生产环境: 从 session 中或数据源获取 RolesAuthorities

        // 示例代码
        Map<String, Map<String, UriResources>> rolesAuthorities = new HashMap<>(10);
        Map<String, UriResources> uriAuthority = new HashMap<>();
        UriResources uriResources = new UriResources();
        uriResources.setUrl("/test/permission/**");
        uriResources.setPermission("/test/permission:add");

        uriAuthority.put("/test/permission/**", uriResources);

        rolesAuthorities.put("ROLE_USER", uriAuthority);
        rolesAuthorities.put("ROLE_ANONYMOUS", uriAuthority);
        return Optional.of(rolesAuthorities);
    }

    @Override
    public void handlerError(int status, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        try (PrintWriter writer = response.getWriter())
        {
            writer.write("{\"msg\":\"您没有访问权限或未登录\"}");
            writer.flush();
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
        }
    }

}
