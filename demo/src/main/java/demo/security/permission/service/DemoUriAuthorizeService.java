package demo.security.permission.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.security.core.permission.dto.UriResourcesDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * request 的 uri 访问权限控制服务.<br>
 * @author zyw
 * @version V1.0  Created by 2020/9/8 21:54
 */
@Component
@Slf4j
public class DemoUriAuthorizeService extends AbstractUriAuthorizeService {

    @Override
    public Optional<Map<String, Map<String, UriResourcesDTO>>> getRolesAuthorities() {

        // 生产环境: 从数据源获取 RolesAuthorities

        // 示例代码
        Map<String, Map<String, UriResourcesDTO>> rolesAuthorities = new HashMap<>(2);
        Map<String, UriResourcesDTO> uriAuthority = new HashMap<>(1);
        UriResourcesDTO uriResourcesDTO = new UriResourcesDTO();
        uriResourcesDTO.setUrl("/test/permission/**");
        uriResourcesDTO.setPermission("/test/permission:add");

        uriAuthority.put("/test/permission/**", uriResourcesDTO);
        uriAuthority.put("/test/pass/**", uriResourcesDTO);

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
            writer.write("{\"msg\":\"demo: 您没有访问权限或未登录\"}");
            writer.flush();
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
        }
    }

}
