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

package demo.service.impl;

import demo.dao.SysRoleResourcesJpaRepository;
import demo.entity.SysRoleResources;
import demo.entity.SysRoleResourcesKey;
import demo.service.SysRoleResourcesService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * 资源服务
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/26 16:56
 */
@Service("sysRoleResourcesService")
public class SysRoleResourcesServiceImpl extends BaseServiceImpl<SysRoleResources, SysRoleResourcesKey> implements SysRoleResourcesService {

    @PersistenceContext
    private EntityManager entityManager;

    private final SysRoleResourcesJpaRepository repository;
    public SysRoleResourcesServiceImpl(SysRoleResourcesJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public SysRoleResources findByRoleIdAndResourcesId(@NonNull Long roleId, @NonNull Long resourcesId) {
        return repository.findByRoleIdAndResourcesId(roleId, resourcesId);
    }

    @Transactional(rollbackFor = {Error.class, Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public void deleteByRoleResourcesId(SysRoleResourcesKey key) {
        repository.deleteById(key);
    }


    @SuppressWarnings("AlibabaRemoveCommentedCode")
    @Transactional(rollbackFor = {Error.class, Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public int batchDeleteByIds(List<SysRoleResourcesKey> roleResourcesIds) {

        StringBuilder sb = new StringBuilder();
        // 构建 ids 字符串，用逗号分隔
        for(SysRoleResourcesKey key : roleResourcesIds) {
            sb.append(key.getWhere()).append(" OR ");
        }
        sb.setLength(sb.length() - 4);
        String whereKeys = sb.toString();

        // 构建查询 ids 是否存在语句并加行锁，防止在 delete 时因没有对应的记录而产生表锁。
        sb.setLength(0);
        sb.append("select role_id, resources_id from sys_role_resources where ");
        sb.append(whereKeys).append(" for update");
        String existsByIdsSql = sb.toString();

        Query existsByIdsQuery = entityManager.createNativeQuery(existsByIdsSql, SysRoleResources.class);
        //noinspection unchecked
        final List<SysRoleResources> resultList = (List<SysRoleResources>) existsByIdsQuery.getResultList();

        for(SysRoleResources sysRoleResources : resultList) {
            if (!entityManager.contains(sysRoleResources)) {
                entityManager.remove(entityManager.merge(sysRoleResources));
            }
            else {
                entityManager.remove(sysRoleResources);
            }
        }
        entityManager.flush();
        entityManager.close();
        return resultList.size();
    }
}