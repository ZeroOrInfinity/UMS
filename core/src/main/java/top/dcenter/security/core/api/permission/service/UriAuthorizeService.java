package top.dcenter.security.core.api.permission.service;

import org.springframework.security.core.Authentication;
import top.dcenter.security.core.permission.UriResources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

/**
 * request 的 uri 访问权限控制服务.<br>
 * @author zyw
 * @version V1.0  Created by 2020/8/28 16:09
 */
public interface UriAuthorizeService {

    /**
     * 根据 authentication 来判断 request 是否有访问权限.
     * @param request
     * @param authentication
     * @param uriAuthorize      uri 权限
     * @return  有访问权限则返回 true, 否则返回 false.
     */
    boolean hasPermission(HttpServletRequest request, Authentication authentication, String uriAuthorize);

    /**
     * 获取用户的权限 Map
     * @param authentication
     * @return 用户 uri 权限 Map
     */
    Optional<Map<String, UriResources>> getUriAuthorities(Authentication authentication);


    /**
     * 当没有访问权限时的处理方式
     * @param status    返回状态
     * @param response  response
     */
    void handlerError(int status, HttpServletResponse response);

}
