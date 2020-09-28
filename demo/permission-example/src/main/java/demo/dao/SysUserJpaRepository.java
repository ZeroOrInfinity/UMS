package demo.dao;

import demo.entity.SysUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户 dao
 * @author zyw23
 * @version V1.0
 * Created by 2020/9/26 16:39
 */
@Repository
public interface SysUserJpaRepository extends CrudRepository<SysUser, Long> {
    /**
     * 根据 username 获取 UserDO
     * @param username  username
     * @return  List<String[]>
     */
    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Query("select id, username, password, mobile, authorities, status from SysUser where username = :username")
    List<String[]> findByUsername(@Param("username") String username);
}
