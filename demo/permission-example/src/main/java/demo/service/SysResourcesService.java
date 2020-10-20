/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package demo.service;

import demo.entity.SysResources;
import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.permission.dto.UriResourcesDTO;

import java.util.List;

/**
 * 资源服务
 * @author YongWu zheng
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