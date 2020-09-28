package demo.service.impl;

import demo.dao.SysUserRoleJpaRepository;
import demo.entity.SysUserRole;
import demo.service.SysUserRoleService;
import org.springframework.stereotype.Service;

/**
 * 资源服务
 * @author zyw
 * @version V1.0  Created by 2020/9/26 16:56
 */
@Service("sysUserRoleService")
public class SysUserRoleServiceImpl extends BaseServiceImpl<SysUserRole, Long> implements SysUserRoleService {

    private final SysUserRoleJpaRepository repository;
    public SysUserRoleServiceImpl(SysUserRoleJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

}
