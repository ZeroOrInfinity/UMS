package demo.service;

import demo.entity.SysRoleResources;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * 角色资源服务
 * @author zyw23
 * @version V1.0
 * Created by 2020/9/26 17:03
 */
public interface SysRoleResourcesService extends BaseService<SysRoleResources, Long>  {

    /**
     * 根据 roleId 与 resourcesId 查询 SysRoleResources
     * @param roleId        roleId
     * @param resourcesId   resourcesId
     * @return  SysRoleResources
     */
    SysRoleResources findByRoleIdAndResourcesId(@NonNull Long roleId, @NonNull Long resourcesId);

    /**
     * 根据角色资源 Id 删除角色资源
     * @param roleResourcesId   角色资源 id
     */
    void deleteByRoleResourcesId(Long roleResourcesId);

    /**
     * 根据 roleResourcesIds 批量删除
     * @param roleResourcesIds  roleResourcesIds
     * @return  删除数量
     */
    int batchDeleteByIds(List<Long> roleResourcesIds);
}
