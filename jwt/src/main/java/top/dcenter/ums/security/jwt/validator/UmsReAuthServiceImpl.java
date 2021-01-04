package top.dcenter.ums.security.jwt.validator;

import org.springframework.security.oauth2.jwt.Jwt;
import top.dcenter.ums.security.jwt.JwtContext;
import top.dcenter.ums.security.jwt.api.validator.service.ReAuthService;
import top.dcenter.ums.security.jwt.properties.JwtProperties;

/**
 * 检查是否需要重新登录认证的服务
 * @author YongWu zheng
 * @since 2021.1.4 16:29
 */
public class UmsReAuthServiceImpl implements ReAuthService {

    private final String principalClaimName;

    public UmsReAuthServiceImpl(JwtProperties jwtProperties) {
        this.principalClaimName = jwtProperties.getPrincipalClaimName();
    }

    @Override
    public Boolean isReAuth(Jwt jwt) {
        String userId = jwt.getClaimAsString(principalClaimName);
        return JwtContext.isReAuth(userId);
    }

}
