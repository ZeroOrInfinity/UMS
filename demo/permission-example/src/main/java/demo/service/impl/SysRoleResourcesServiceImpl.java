package demo.service.impl;

import demo.dao.SysRoleResourcesJpaRepository;
import demo.entity.SysRoleResources;
import demo.service.SysRoleResourcesService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public SysRoleResources findByRoleIdAndResourcesId(@NotNull Long roleId, @NotNull Long resourcesId) {
        return repository.findByRoleIdAndResourcesId(roleId, resourcesId);
    }

    @Transactional(rollbackFor = {Error.class, Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public void deleteByRoleResourcesId(Long roleResourcesId) {
        repository.deleteById(roleResourcesId);
    }


    @SuppressWarnings("AlibabaRemoveCommentedCode")
    @Transactional(rollbackFor = {Error.class, Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public int batchDeleteByIds(List<Long> roleResourcesIds) {

        StringBuilder sb = new StringBuilder();
        // 构建 ids 字符串，用逗号分隔
        for(Long id : roleResourcesIds) {
            sb.append(id).append(",");
        }
        sb.setLength(sb.length() - 1);
        String ids = sb.toString();

        // 构建查询 ids 是否存在语句并加行锁，防止在 delete 时因没有对应的记录而产生表锁。
        sb.setLength(0);
        sb.append("select id from sys_role_resources where id in (");
        sb.append(ids).append(") for update");
        String existsByIdsSql = sb.toString();
        Query existsByIdsQuery = entityManager.createNativeQuery(existsByIdsSql);
        //noinspection rawtypes
        List resultList = existsByIdsQuery.getResultList();

        sb.setLength(0);
        sb.append("delete from sys_role_resources where id in (");
        for(Object id : resultList) {
            sb.append(id).append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append(")");
        String deleteSql = sb.toString();
        Query deleteQuery = entityManager.createNativeQuery(deleteSql);
        return deleteQuery.executeUpdate();
    }
}
