package demo.dao;

import demo.entity.SysRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色 repo
 * @author zyw23
 * @version V1.0
 * Created by 2020/9/26 16:38
 */
@Repository
public interface SysRoleJpaRepository extends CrudRepository<SysRole, Long> {
    /**
     * 根据角色获取 roleName 实体
     * @param roleName  角色
     * @return  SysRole
     */
    SysRole findByName(String roleName);

    /**
     * 查询所有角色的权限
     * @return  list
     */
    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query("select r.name, s.url, s.permission " +
            "from SysRole r " +
            "inner join SysRoleResources rs on r.id = rs.roleId " +
            "inner join SysResources s on s.id = rs.resourcesId")
    List<String[]> findAuthoritiesByRoles();
}
