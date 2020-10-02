package demo.dao;

import demo.entity.SysResources;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zyw
 * @version V1.0  Created by 2020/9/26 16:11
 */
@SuppressWarnings({"SqlNoDataSourceInspection", "SqlDialectInspection"})
@Repository
public interface SysResourcesJpaRepository extends CrudRepository<SysResources, Long> {

    /**
     * 根据 url 获取 List<String[]>
     * @param url   url
     * @return  List<String[]>
     */
    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query("select id, url, permission from SysResources where url = :url")
    List<String[]> findSysResourcesByUrl(@Param("url") String url);

    /**
     * 根据 url 获取 UriResources
     * @param url   url
     * @return  UriResources, 没有匹配数据返回 null
     */
    SysResources findByUrl(String url);


    /**
     * 根据 roleId 与 url 获取资源列表
     * @param roleId    roleId
     * @param url   url
     * @return  uri 权限资源列表
     */
    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query(value = "select srs.rs_id, srs.role_id, s.id, s.url, s.permission " +
            "from (select id as rs_id, role_id, resources_id from sys_role_resources rs where role_id = :roleId) srs " +
            "inner join sys_resources s on srs.resources_id = s.id where s.url = :url", nativeQuery = true)
    List<String[]> findUriResourcesDtoByRoleIdAndUrl(@Param("roleId") Long roleId, @Param("url") String url);

    /**
     * 根据 roleId 与 url 获取资源列表s
     * @param roleId    roleIdss
     * @param url   url
     * @return  权限资源列表
     */
    @Query(value = "select * " +
            "from sys_resources " +
            "where url = :url and id in (select resources_id from sys_role_resources where role_id = :roleId) "
            , nativeQuery = true)
    List<SysResources> findByRoleIdAndUrl(@Param("roleId") Long roleId, @Param("url") String url);

    /**
     * 更新权限资源
     * @param permission   permission
     * @param id           id
     */
    @Modifying
    @Query("update SysResources set permission = :p where id = :id")
    void updatePermissionById(@Param("p") String permission, @Param("id") Long id);
}
