package top.dcenter.security.core.permission.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import top.dcenter.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.security.core.permission.dto.UriResourcesDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static top.dcenter.security.core.util.AuthenticationUtil.responseWithJson;

/**
 * request 的 uri 访问权限控制服务.<br>
 * 实现 {@link AbstractUriAuthorizeService} 抽象类并注入 IOC 容器即可替换此类.
 * @author zyw
 * @version V1.0  Created by 2020/9/8 21:54
 */
@Slf4j
public class DefaultUriAuthorizeService extends AbstractUriAuthorizeService {

    private AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public Optional<Map<String, Map<String, UriResourcesDTO>>> getRolesAuthorities() {
        // do nothing
        return Optional.empty();
    }

    @Override
    public void handlerError(int status, HttpServletResponse response) {

        try
        {
            responseWithJson(response, status, "{\"msg\":\"您没有访问权限或未登录\"}");
        }
        catch (IOException e)
        {
            log.error(String.format("权限控制错误响应异常: status={}, error={}", status, e.getMessage()), e);
        }
    }

}
