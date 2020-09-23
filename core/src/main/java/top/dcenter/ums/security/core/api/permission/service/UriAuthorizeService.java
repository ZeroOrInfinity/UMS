package top.dcenter.ums.security.core.api.permission.service;

import org.springframework.security.core.Authentication;
import org.springframework.util.AntPathMatcher;
import top.dcenter.ums.security.core.permission.dto.UriResourcesDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * request 的 uri 访问权限控制服务.<br>
 * @author zyw
 * @version V1.0  Created by 2020/8/28 16:09
 */
public interface UriAuthorizeService {

    /**
     * 根据 authentication 来判断 request 是否有访问权限.
     * @param request           request
     * @param authentication    authentication
     * @param uriAuthorize      uri 权限
     * @return  有访问权限则返回 true, 否则返回 false.
     */
    boolean hasPermission(HttpServletRequest request, Authentication authentication, String uriAuthorize);

    /**
     * 获取用户角色的 uri 权限 Map
     * @param authentication    authentication
     * @return 用户角色的 uri 权限 Map(uri, Set(authority))
     */
    Optional<Map<String, Set<String>>> getUriAuthoritiesOfUserRole(Authentication authentication);

    /**
     * 获取所有角色的 uri 权限 Map
     * @return 所有角色的 uri 权限 Map(uri, Set(authority))
     */
    Optional<Map<String, Set<String>>> getUriAuthoritiesOfAllRole();


    /**
     * 当没有访问权限时的处理方式
     * @param status    返回状态
     * @param response  response
     */
    void handlerError(int status, HttpServletResponse response);

    /**
     * 获取角色的 uri 的权限 map.<br>
     *     返回值为: Map(role, Map(uri, UriResourcesDTO))
     * @return Map(String, Map(String, String)) 的 key 为必须包含"ROLE_"前缀的角色名称(如: ROLE_ADMIN), value 为 UriResourcesDTO map
     * (key 为 uri, 此 uri 可以为 antPath 通配符路径,如 /user/**; value 为 UriResourcesDTO).
     */
    Optional<Map<String, Map<String, UriResourcesDTO>>> getRolesAuthorities();

    /**
     * 每次修改角色的权限时, 都要更新角色的 uri 的权限 map
     */
    void updateRolesAuthorities();

    /**
     * 获取 AntPathMatcher
     * @return AntPathMatcher
     */
    AntPathMatcher getAntPathMatcher();

    /**
     * 遍历 uriSet, 测试是否有匹配 requestUri(去除 ServletContextPath) 的 uri.
     * @param uriSet        uriSet(可以是Ant通配符的 uri)
     * @param requestUri    requestUri
     * @return Boolean
     */
    Boolean isUriContainsInUriSet(Set<String> uriSet, String requestUri);

}
