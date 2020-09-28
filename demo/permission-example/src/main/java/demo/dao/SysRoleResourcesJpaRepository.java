package demo.dao;

import demo.entity.SysRoleResources;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zyw
 * @version V1.0  Created by 2020/9/26 16:41
 */
@Repository
public interface SysRoleResourcesJpaRepository extends CrudRepository<SysRoleResources, Long> {
    /**
     * 根据 roleId 与 resourcesId 查询 SysRoleResources
     * @param roleId        roleId
     * @param resourcesId   resourcesId
     * @return  SysRoleResources
     */
    SysRoleResources findByRoleIdAndResourcesId(Long roleId, Long resourcesId);

}
