package demo.service.impl;

import demo.dao.SysResourcesJpaRepository;
import demo.entity.SysResources;
import demo.service.SysResourcesService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.dcenter.ums.security.core.permission.dto.UriResourcesDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源服务
 * @author zyw
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
            uriResourcesDO.setRoleResourcesId(Long.valueOf(arr[0]));
            uriResourcesDO.setRoleId(Long.valueOf(arr[1]));
            uriResourcesDO.setResourcesId(Long.valueOf(arr[2]));
            uriResourcesDO.setUrl(arr[3]);
            uriResourcesDO.setPermission(arr[4]);
            result.add(uriResourcesDO);
        }

        return result;
    }
}