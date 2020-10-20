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

import demo.dao.SysUserJpaRepository;
import demo.entity.SysUser;
import demo.polo.UserDO;
import demo.service.SysUserService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 资源服务
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/26 16:56
 */
@Service("sysUserService")
public class SysUserServiceImpl extends BaseServiceImpl<SysUser, Long> implements SysUserService {

    private final SysUserJpaRepository repository;
    public SysUserServiceImpl(SysUserJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
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