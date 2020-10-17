package top.dcenter.ums.security.core.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 返回 json 数据的用户信息
 * @author zyw
 * @version V1.0  Created by 2020/6/6 22:27
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoJsonVo {

    /**
     * 用户 id
     */
    private String id;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 跳转 url
     */
    private String targetUrl;
    /**
     * 用户权限
     */
    private Collection<GrantedAuthority> authorities;
}
