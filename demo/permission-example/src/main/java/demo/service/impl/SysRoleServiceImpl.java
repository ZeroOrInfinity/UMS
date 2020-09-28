package demo.service.impl;

import demo.dao.SysRoleJpaRepository;
import demo.entity.SysRole;
import demo.service.SysRoleService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import top.dcenter.ums.security.core.permission.dto.UriResourcesDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源服务
 * @author zyw
 * @version V1.0  Created by 2020/9/26 16:56
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends BaseServiceImpl<SysRole, Long> implements SysRoleService {

    private final SysRoleJpaRepository repository;

    public SysRoleServiceImpl(SysRoleJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }


    @Override
    public SysRole findByName(@NonNull String roleName) {

        return repository.findByName(roleName);
    }

    @Override
    public Map<String, Map<String, UriResourcesDTO>> getRolesAuthorities() {
        // 获取所有角色的 uri 的权限
        List<String[]> authoritiesByRoles = repository.findAuthoritiesByRoles();

        int size = authoritiesByRoles.size();

        Map<String, Map<String, UriResourcesDTO>> result = new HashMap<>(size);
        if (size < 1)
        {
            return result;
        }

        // r.name, s.url, s.permission
        authoritiesByRoles.forEach(arr -> {
            final UriResourcesDTO uriResourcesDO = new UriResourcesDTO(arr[1], arr[2]);
            result.compute(arr[0], (k, v) -> {
                if (v == null)
                {
                    v = new HashMap<>(1);
                }
                v.put(uriResourcesDO.getUrl(), uriResourcesDO);
                return v;
            });
        });

        return result;
    }
}
