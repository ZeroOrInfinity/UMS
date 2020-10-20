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

package demo.dao;

import demo.entity.SysRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色 repo
 * @author YongWu zheng
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