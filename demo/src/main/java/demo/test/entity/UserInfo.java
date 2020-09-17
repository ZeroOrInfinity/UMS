package demo.test.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/5 23:20
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    @JsonAlias({"username"})
    private String userId;
    private String password;
    private String avatarUrl;
    private String providerId;
    private String providerUserId;
}
