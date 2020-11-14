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

import demo.entity.SysRoleResources;
import demo.entity.SysRoleResourcesKey;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * 角色权限资源服务
 * @author YongWu zheng
 * @version V1.0
 * Created by 2020/9/26 17:03
 */
public interface SysRoleResourcesService extends BaseService<SysRoleResources, SysRoleResourcesKey>  {

    /**
     * 根据 roleId 与 resourcesId 查询 SysRoleResources
     * @param roleId        roleId
     * @param resourcesId   resourcesId
     * @return  SysRoleResources
     */
    SysRoleResources findByRoleIdAndResourcesId(@NonNull Long roleId, @NonNull Long resourcesId);

    /**
     * 根据角色资源 Id 删除角色权限资源
     * @param key   角色权限资源 id
     */
    void deleteByRoleResourcesId(SysRoleResourcesKey key);

    /**
     * 根据 roleResourcesIds 批量删除
     * @param keys  roleResourcesIds
     * @return  删除数量
     */
    int batchDeleteByIds(List<SysRoleResourcesKey> keys);

    /**
     * 根据 roleId 查询 角色的权限资源
     * @param roleId    角色 ID
     * @return  角色的权限资源
     */
    List<SysRoleResources> findByRoleId(Long roleId);
}