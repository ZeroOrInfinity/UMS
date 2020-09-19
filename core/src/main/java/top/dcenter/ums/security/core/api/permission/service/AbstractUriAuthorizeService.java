package top.dcenter.ums.security.core.api.permission.service;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.AntPathMatcher;
import top.dcenter.ums.security.core.permission.dto.UriResourcesDTO;
import top.dcenter.ums.security.core.permission.service.DefaultUriAuthorizeService;
import top.dcenter.ums.security.core.util.ConvertUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * request 的 uri 访问权限控制服务.<br><br>
 * 实现 {@link AbstractUriAuthorizeService} 抽象类并注入 IOC 容器即可替换
 * {@link DefaultUriAuthorizeService}.<br><br>
 *
 * @author zyw
 * @version V1.0  Created by 2020/9/8 15:09
 */
public abstract class AbstractUriAuthorizeService implements UriAuthorizeService, InitializingBean {


    /**
     * 权限前缀
     */
    protected final String defaultRolePrefix = "ROLE_";
    /**
     * 权限分隔符
     */
    public static final String PERMISSION_DELIMITER = ",";

    /**
     * 角色 uri 权限 Map(role, map(uri, uriResourcesDTO))
     */
    protected Map<String, Map<String, UriResourcesDTO>> rolesAuthorities;

    @Getter
    protected AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final Object lock = new Object();


    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication, String uriAuthorize) {

        // Map(uri, Set(authority))
        Map<String, Set<String>> uriAuthorityMap = getUriAuthoritiesOfUserRole(authentication).orElse(new HashMap<>(0));

        final String requestURI = request.getRequestURI();
        Set<String> uriSet = uriAuthorityMap.keySet();

        // uri 是否匹配
        if (isUriContainsInUriSet(uriSet, requestURI))
        {
            return uriAuthorityMap.entrySet().stream()
                                  .map(Map.Entry::getValue)
                                  // 权限是否匹配
                                  .anyMatch(authoritySet -> authoritySet.contains(uriAuthorize));
        }

        return false;
    }

    /**
     * 根据 authentication 中 Authorities 的 roles 从 {@link #getRolesAuthorities()} 获取 uri 权限 map.<br><br>
     * 实现对 uri 的权限控制时, 要考虑纯正的 resetFul 风格的 api 是通过 GET/POST/PUT/DELETE 等来区别 curd 操作的情况;
     * 这里我们用 map(uri, Set(authority)) 来处理.
     * @param authentication    authentication
     * @return 用户角色的 uri 权限 Map(uri, Set(authority))
     */
    @Override
    public Optional<Map<String, Set<String>>> getUriAuthoritiesOfUserRole(Authentication authentication) {

        if (authentication == null)
        {
            return Optional.of(Collections.emptyMap());
        }
        // 获取角色权限集合
        Set<String> authoritySet = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        final Set<String> roleSet =
                authoritySet.stream()
                            .map(authorities -> StringUtils.splitByWholeSeparator(authorities, PERMISSION_DELIMITER))
                            .flatMap(Arrays::stream)
                            .filter(authority -> authority.startsWith(this.defaultRolePrefix))
                            .collect(Collectors.toSet());

        /*
         * 实现对 requestUri 的权限控制时, 要考虑纯正的 resetFul 风格的 api 是通过 GET/POST/PUT/DELETE 等来区别 curd 操作的情况;
         * 这里我们用 map(uri, Set(authority)) 来处理
         */
        Map<String, Set<String>> uriAuthoritiesMap = new HashMap<>(rolesAuthorities.size());

        rolesAuthorities.entrySet()
                        .stream()
                        .filter(map -> roleSet.contains(map.getKey()))
                        .map(Map.Entry::getValue)
                        .forEach(map2mapConsumer(uriAuthoritiesMap));

        return Optional.of(uriAuthoritiesMap);
    }

    /**
     * 从 {@link #getRolesAuthorities()} 中获取获取所有 roles 的 uri 权限 map.<br><br>
     * 实现对 uri 的权限控制时, 要考虑纯正的 resetFul 风格的 api 是通过 GET/POST/PUT/DELETE 等来区别 curd 操作的情况;
     * 这里我们用 map(uri, Set(authority)) 来处理.
     * @return 所有角色 uri 权限 Map(uri, Set(authority))
     */
    @Override
    public Optional<Map<String, Set<String>>> getUriAuthoritiesOfAllRole() {

        /*
         * 实现对 requestUri 的权限控制时, 要考虑纯正的 resetFul 风格的 api 是通过 GET/POST/PUT/DELETE 等来区别 curd 操作的情况;
         * 这里我们用 map(uri, Set(authority)) 来处理
         */
        Map<String, Set<String>> uriAuthoritiesMap = new HashMap<>(rolesAuthorities.size());

        rolesAuthorities.entrySet()
                        .stream()
                        .map(Map.Entry::getValue)
                        .forEach(map2mapConsumer(uriAuthoritiesMap));

        return Optional.of(uriAuthoritiesMap);
    }


    @Override
    public void updateRolesAuthorities() {

        synchronized (lock) {
            rolesAuthorities = getRolesAuthorities().orElse(new HashMap<>(0));
        }

    }

    @Override
    public Boolean isUriContainsInUriSet(Set<String> uriSet, final String requestUri) {
        return uriSet.contains(requestUri) || uriSet.stream().anyMatch(uri -> antPathMatcher.match(uri, requestUri));

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 角色 uri 权限 Map(role, map(uri, uriResourcesDTO))
        updateRolesAuthorities();
    }

    @NotNull
    private Consumer<Map<String, UriResourcesDTO>> map2mapConsumer(final Map<String, Set<String>> uriAuthoritiesMap) {
        return map -> map.forEach(
                (key, value) ->
                {
                    uriAuthoritiesMap.compute(key, (k, v) ->
                    {
                        if (v == null)
                        {
                            v = new HashSet<>();
                        }
                        v.addAll(ConvertUtil.string2Set(value.getPermission(), PERMISSION_DELIMITER));
                        return v;
                    });
                });
    }

}
