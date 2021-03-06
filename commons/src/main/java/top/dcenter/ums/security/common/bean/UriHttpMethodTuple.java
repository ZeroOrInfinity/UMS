/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.common.bean;

import org.springframework.http.HttpMethod;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;

import java.io.Serializable;

/**
 * uri 与 HttpMethod 权限组, 用于 {@link HttpSecurityAware} 配置时使用
 * @author YongWu zheng
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