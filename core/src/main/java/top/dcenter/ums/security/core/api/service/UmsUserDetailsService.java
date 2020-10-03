package top.dcenter.ums.security.core.api.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户名密码、手机短信登录与注册服务：<br><br>
 *     1. 用于用户名密码登录与手机短信登录逻辑。<br><br>
 *     2. 用于用户名密码注册与手机短信注册逻辑。<br><br>
 * @author zyw
 * @version V1.0  Created by 2020/5/16 11:48
 */
public interface UmsUserDetailsService extends UserDetailsService, UserDetailsRegisterService {

}
