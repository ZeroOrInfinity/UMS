package demo.service.impl;

import demo.polo.UserDO;
import demo.dao.SysUserJpaRepository;
import demo.entity.SysUser;
import demo.service.SysUserService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资源服务
 * @author zyw
 * @version V1.0  Created by 2020/9/26 16:56
 */
@Service("sysUserService")
public class SysUserServiceImpl extends BaseServiceImpl<SysUser, Long> implements SysUserService {

    private final SysUserJpaRepository repository;
    public SysUserServiceImpl(SysUserJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public UserDO findByUsername(@NonNull String username) {
        List<String[]> list = repository.findByUsername(username);

        if (list.size() < 1)
        {
            return null;
        }

        String[] objs = list.get(0);
        //  id, username, password, mobile, authorities, status
        UserDO userDO = new UserDO();
        userDO.setId(Long.valueOf(objs[0]));
        userDO.setUsername(objs[1]);
        userDO.setPassword(objs[2]);
        userDO.setMobile(objs[3]);
        userDO.setAuthorities(objs[4]);
        userDO.setStatus(Integer.valueOf(objs[5]));

        return userDO;
    }
}
