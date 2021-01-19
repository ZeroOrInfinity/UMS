package demo.jwt.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.jwt.api.endpoind.service.JwkEndpointPermissionService;
import top.dcenter.ums.security.jwt.api.endpoind.service.JwkSetUriConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于从 jwk set uri 获取 JWk 时传递 header,
 * 获取 jwk set 后缓存的时间, 访问 jwk set uri 的频率 的参数接口, 通过{@code UmsNimbusJwtDecoder.RestOperationsResourceRetriever}
 * 传递 header 参数.<br>
 * 不是必须实现的接口, 可以与 {@link JwkEndpointPermissionService} 配合使用
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.1.19 14:32
 */
@Component
public class DemoJwkSetUriConfig implements JwkSetUriConfig {

    @Override
    @NonNull
    public Map<String, Object> headers() {
        HashMap<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("appId", "111");
        resultMap.put("appCode", "111");
        return resultMap;
    }
}
