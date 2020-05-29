package top.dcenter.security.social.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * social 用户信息
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/11 13:35
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserInfo {
    private String providerId;
    private String providerUserId;
    private String username;
    private String avatarImg;
}

