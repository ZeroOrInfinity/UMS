package top.dcenter.security.core.api.permission.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.AntPathMatcher;
import top.dcenter.security.core.permission.UriResources;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * request 的 uri 访问权限控制服务.<br><br>
 * 实现 {@link AbstractUriAuthorizeService} 抽象类并注入 IOC 容器即可替换
 * {@link top.dcenter.security.core.permission.service.DefaultUriAuthorizeService}.<br><br>
 *
 * 注意: 实现对 requestUri 的权限控制时, 不光要考虑 URI 是否匹配, 还要考虑纯正的 resetFul 风格的 api, 通过 GET/POST/PUT/DELETE 等来区别 curd 操作的情况;
 * 那么还需要判断 request method 与 {@link UriResources} 中的 permission 相对应.
 * @author zyw
 * @version V1.0  Created by 2020/9/8 15:09
 */
public abstract class AbstractUriAuthorizeService implements UriAuthorizeService {


    protected final String defaultRolePrefix = "ROLE_";
    /**
     * 权限分隔符
     */
    protected static final String PERMISSION_DELIMITER = ",";

    protected AntPathMatcher matcher = new AntPathMatcher();;

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication, String uriAuthorize) {

        Map<String, UriResources> uriResourcesMap = getUriAuthorities(authentication).orElse(new HashMap<>(0));
        return uriResourcesMap.entrySet().stream()
                // uri 是否匹配
                .filter(entry -> matcher.match(entry.getKey(), request.getRequestURI()))
                // 权限是否匹配
                .anyMatch(entry -> {
                    String[] permissions = StringUtils.split(entry.getValue().getPermission(), PERMISSION_DELIMITER);
                    return Arrays.stream(permissions).anyMatch(permission -> StringUtils.equals(permission, uriAuthorize));
                });
    }

    /**
     * 获取用户的权限 Map. <br><br>
     *     注意: 返回值是根据 authentication 中 Authorities 的 roles 从 {@link #getRolesAuthorities()} 获取返回值.<br>
     *         当有多个 role 时且不同的 role 中拥有相同的 uri 时, 随机获取其中一个 uri 所对应的 uriResources.
     * @param authentication
     * @return 用户 uri 权限 Map
     */
    @Override
    public Optional<Map<String, UriResources>> getUriAuthorities(Authentication authentication) {

        if (authentication == null)
        {
            return Optional.of(Collections.emptyMap());
        }
        // 获取角色权限集合
        Set<String> authoritySet = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        final Set<String> roleSet =
                authoritySet.stream().filter(authority -> authority.startsWith(this.defaultRolePrefix)).collect(Collectors.toSet());

        // 获取角色的 uri 权限 map
        Optional<Map<String, Map<String, UriResources>>> rolesAuthorities = getRolesAuthorities();
        Map<String, UriResources> uriAuthoritiesMap = rolesAuthorities
                .orElse(Collections.emptyMap())
                .entrySet()
                .stream()
                .filter(map -> roleSet.contains(map.getKey()))
                .flatMap(map -> map.getValue().entrySet().stream())
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));


        return Optional.of(uriAuthoritiesMap);
    }


    /**
     * 获取角色的 uri 的权限 map.<br>
     *     返回值为: Map<role, Map<uri, UriResources>>
     * @return Map<String, Map<String, String>> 的 key 为必须包含"ROLE_"前缀的角色名称(如: ROLE_ADMIN), value 为 UriResources map
     * (key 为 uri, 此 uri 可以为 antPath 通配符路径,如 /user/**; value 为 UriResources).
     */
    protected abstract Optional<Map<String, Map<String, UriResources>>> getRolesAuthorities();


}
