package top.dcenter.ums.security.common.bean;

import org.springframework.http.HttpMethod;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;

import java.io.Serializable;

/**
 * uri 与 HttpMethod 权限组, 用于 {@link HttpSecurityAware} 配置时使用
 * @author zyw
 * @version V1.0  Created by 2020/10/3 23:19
 */
public class UriHttpMethodTuple implements Serializable {
    private static final long serialVersionUID = -8135205986821450357L;

    private String uri;
    private HttpMethod method;

    public UriHttpMethodTuple(String uri, HttpMethod method) {
        this.uri = uri;
        this.method = method;
    }

    public static UriHttpMethodTuple tuple(HttpMethod method, String uri) {
        return new UriHttpMethodTuple(uri, method);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        UriHttpMethodTuple that = (UriHttpMethodTuple) o;

        if (!uri.equals(that.uri))
        {
            return false;
        }
        return method == that.method;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }
}
