package demo.service;

import demo.entity.SysResources;
import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.permission.dto.UriResourcesDTO;

import java.util.List;

/**
 * 资源服务
 * @author zyw23
 * @version V1.0
 * Created by 2020/9/26 17:03
 */
public interface SysResourcesService extends BaseService<SysResources, Long> {

    /**
     * 根据 url 获取 UriResourcesDTO
     * @param url   url
     * @return  UriResourcesDTO
     */
    UriResourcesDTO findUriResourcesDtoByUrl(@NonNull String url);

    /**
     * 根据 url 获取 UriResources
     * @param url   url
     * @return  UriResources, 没有匹配数据返回 null
     */
    SysResources findByUrl(@NonNull String url);

    /**
     * 根据 roleId 与 url 获取资源列表
     * @param roleId    roleId
     * @param url   url
     * @return  权限资源列表
     */
    List<SysResources> findByRoleIdAndUrl(Long roleId, String url);

    /**
     * 更新权限资源
     * @param sysResourcesList   sysResourcesList
     */
    void batchUpdateBySysResources(List<SysResources> sysResourcesList);

    /**
     * 根据 roleId 与 url 获取资源列表
     * @param roleId    roleId
     * @param url   url
     * @return  URI 权限资源列表
     */
    List<UriResourcesDTO> findUriResourcesDtoByRoleIdAndUrl(Long roleId, String url);
}
