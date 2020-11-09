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

import demo.dao.SysResourcesJpaRepository;
import demo.entity.SysResources;
import demo.entity.UriResourcesDTO;
import demo.service.SysResourcesService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源服务
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/26 16:56
 */
@Service("sysResourcesService")
public class SysResourcesServiceImpl extends BaseServiceImpl<SysResources, Long> implements SysResourcesService {

    private final SysResourcesJpaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public SysResourcesServiceImpl(SysResourcesJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public UriResourcesDTO findUriResourcesDtoByUrl(@NonNull String url) {
        List<String[]> list = repository.findSysResourcesByUrl(url);
        if (list.size() < 1)
        {
            return null;
        }
        // id, url, permission
        String[] strArr = list.get(0);
        UriResourcesDTO uriResourcesDO = new UriResourcesDTO();
        uriResourcesDO.setResourcesId(Long.valueOf(strArr[0]));
        uriResourcesDO.setUrl(strArr[1]);
        uriResourcesDO.setPermission(strArr[2]);

        return uriResourcesDO;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public SysResources findByUrl(@NonNull String url) {
        return repository.findByUrl(url);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public List<SysResources> findByRoleIdAndUrl(Long roleId, String url) {
        return repository.findByRoleIdAndUrl(roleId, url);
    }

    @Transactional(rollbackFor = {Error.class, Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public void batchUpdateBySysResources(List<SysResources> resourcesList) {

        int i = 0;
        for (SysResources sysResources : resourcesList)
        {
            entityManager.merge(sysResources);
            i++;
            if (i % 50 == 0)
            {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public List<UriResourcesDTO> findUriResourcesDtoByRoleIdAndUrl(Long roleId, String url) {
        List<String[]> list = repository.findUriResourcesDtoByRoleIdAndUrl(roleId, url);
        List<UriResourcesDTO> result = new ArrayList<>(list.size());
        UriResourcesDTO uriResourcesDO;
        for (String[] arr : list)
        {
            // rs.id, rs.role_id, s.id, s.url, s.permission
            uriResourcesDO = new UriResourcesDTO();
            uriResourcesDO.setRoleId(Long.valueOf(arr[0]));
            uriResourcesDO.setResourcesId(Long.valueOf(arr[1]));
            uriResourcesDO.setUrl(arr[2]);
            uriResourcesDO.setPermission(arr[3]);
            result.add(uriResourcesDO);
        }

        return result;
    }

    @Override
    public List<SysResources> findByRoleId(Long roleId) {
        return repository.findByRoleId(roleId);
    }
}