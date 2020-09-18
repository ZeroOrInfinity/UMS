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

    private String id;
    private String username;
    private String url;
    private Collection<GrantedAuthority> authorities;
}
