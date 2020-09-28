package demo.permission.service.impl;

import demo.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.core.permission.dto.UriResourcesDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

/**
 * request 的 uri 访问权限控制服务.<br>
 * @author zyw
 * @version V1.0  Created by 2020/9/8 21:54
 */
@Service
@Slf4j
public class UriAuthorizeServiceImpl extends AbstractUriAuthorizeService {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public Optional<Map<String, Map<String, UriResourcesDTO>>> getRolesAuthorities() {

        // 从数据源获取 RolesAuthorities
        Map<String, Map<String, UriResourcesDTO>> rolesAuthorities = sysRoleService.getRolesAuthorities();

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
