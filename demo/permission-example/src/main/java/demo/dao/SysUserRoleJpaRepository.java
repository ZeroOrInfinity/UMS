package demo.dao;

import demo.entity.SysUserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zyw23
 * @version V1.0
 * Created by 2020/9/26 16:40
 */
@Repository
public interface SysUserRoleJpaRepository extends CrudRepository<SysUserRole, Long> {
}
