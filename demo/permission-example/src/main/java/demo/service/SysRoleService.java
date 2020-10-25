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

import demo.entity.SysRole;
import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.api.permission.entity.UriResourcesDTO;

import java.util.Map;

/**
 * 角色服务
 * @author YongWu zheng
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