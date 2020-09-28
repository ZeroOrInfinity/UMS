package demo.service.impl;

import demo.dao.SysRoleResourcesJpaRepository;
import demo.entity.SysRoleResources;
import demo.service.SysRoleResourcesService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * 资源服务
 * @author zyw
 * @version V1.0  Created by 2020/9/26 16:56
 */
@Service("sysRoleResourcesService")
public class SysRoleResourcesServiceImpl extends BaseServiceImpl<SysRoleResources, Long> implements SysRoleResourcesService {

    @PersistenceContext
    private EntityManager entityManager;

    private final SysRoleResourcesJpaRepository repository;
    public SysRoleResourcesServiceImpl(SysRoleResourcesJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public SysRoleResources findByRoleIdAndResourcesId(@NotNull Long roleId, @NotNull Long resourcesId) {
        return repository.findByRoleIdAndResourcesId(roleId, resourcesId);
    }

    @Transactional(rollbackFor = {Error.class, Exception.class})
    @Override
    public void deleteByRoleResourcesId(Long roleResourcesId) {
        repository.deleteById(roleResourcesId);
    }


    @SuppressWarnings("AlibabaRemoveCommentedCode")
    @Transactional(rollbackFor = {Error.class, Exception.class})
    @Override
    public int batchDeleteByIds(List<Long> roleResourcesIds) {

        StringBuilder sb = new StringBuilder();
        sb.append("delete from sys_role_resources where id in (");
        for(Long id : roleResourcesIds) {
            sb.append(id).append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append(")");
        String sql = sb.toString();
        Query query = entityManager.createNativeQuery(sql);
        //int paramIndex = 1;
        //for(Long id : roleResourcesIds) {
        //    query.setParameter(paramIndex++, id);
        //}
        return query.executeUpdate();
    }
}
