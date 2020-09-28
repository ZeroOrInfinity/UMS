package demo.service;

import demo.polo.UserDO;
import demo.entity.SysUser;
import org.springframework.lang.NonNull;

/**
 * 用户服务
 * @author zyw23
 * @version V1.0
 * Created by 2020/9/26 17:03
 */
public interface SysUserService extends BaseService<SysUser, Long> {
    /**
     * 根据 username 获取 UserDO
     * @param username  username
     * @return  UserDO
     */
    UserDO findByUsername(@NonNull String username);
}
