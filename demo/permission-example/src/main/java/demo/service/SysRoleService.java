package demo.service;

import demo.entity.SysRole;
import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.permission.dto.UriResourcesDTO;

import java.util.Map;

/**
 * 角色服务
 * @author zyw23
 * @version V1.0
 * Created by 2020/9/26 17:03
 */
public interface SysRoleService extends BaseService<SysRole, Long> {

    /**
     * 根据角色获取 roleName 实体
     * @param roleName  角色
     * @return  SysRole
     */
    SysRole findByName(@NonNull String roleName);

    /**
     * 获取所有角色的权限
     * @return  Map(String, Map(String, String)) 的 key 为必须包含"ROLE_"前缀的角色名称(如: ROLE_ADMIN), value 为 UriResourcesDTO map (key 为 uri, 此 uri 可以为 antPath 通配符路径,如 /user/**; value 为 UriResourcesDTO).
     */
    Map<String, Map<String, UriResourcesDTO>> getRolesAuthorities();
}
