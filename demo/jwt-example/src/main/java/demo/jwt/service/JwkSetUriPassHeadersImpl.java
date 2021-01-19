package demo.jwt.service;

import top.dcenter.ums.security.jwt.api.endpoind.service.JwkEndpointPermissionService;
import top.dcenter.ums.security.jwt.api.endpoind.service.JwkSetUriPassHeaders;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于从 jwk set uri 获取 JWk 时传递 header 的参数接口, 通过{@code UmsNimbusJwtDecoder.RestOperationsResourceRetriever}
 * 传递 header 参数.<br>
 * 不是必须实现的接口, 可以与 {@link JwkEndpointPermissionService} 配合使用
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.1.19 14:32
 */
public class JwkSetUriPassHeadersImpl implements JwkSetUriPassHeaders {
    @Override
    public Map<String, Object> headers() {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("appId", "111");
        resultMap.put("appCode", "111");
        return resultMap;
    }
}
