package top.dcenter.ums.security.core.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpMethod;
import top.dcenter.ums.security.core.api.config.HttpSecurityAware;

import java.io.Serializable;

/**
 * uri 与 HttpMethod 权限组, 用于 {@link HttpSecurityAware} 配置时使用
 * @author zyw
 * @version V1.0  Created by 2020/10/3 23:19
 */
@EqualsAndHashCode
@Data
@AllArgsConstructor
public class UriHttpMethodTuple implements Serializable {
    private static final long serialVersionUID = -8135205986821450357L;

    private String uri;
    private HttpMethod method;

    public static UriHttpMethodTuple tuple(HttpMethod method, String uri) {
        return new UriHttpMethodTuple(uri, method);
    }
}
