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

package top.dcenter.ums.security.core.auth.filter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import top.dcenter.ums.security.common.consts.SecurityConstants;
import top.dcenter.ums.security.core.util.ConvertUtil;
import top.dcenter.ums.security.core.util.MvcUtil;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static top.dcenter.ums.security.core.util.RequestUtil.readAllBytes;

/**
 * 增加对 Ajax 格式与 form 格式的解析, 解析数据时默认使用 UTF-8 格式, 覆写了
 * <pre>
 *     AjaxOrFormRequest#getParameter(String);
 *     AjaxOrFormRequest#getInputStream();
 * </pre>, 添加了
 * <pre>
 *     AjaxOrFormRequest#getFormMap();
 *     AjaxOrFormRequest#getBody();
 * </pre><br><br>
 * 解决  Ajax 格式与 form 格式的请求被读取一次后, 不能在次读取的问题.
 * @author YongWu zheng
 * @version V1.0  Created by 2020/6/9 14:01
 */
public class AjaxOrFormRequestFilter extends OncePerRequestFilter {

    /**
     * 验证 request 中参数是否是 json 的字符串的前缀,
     */
    public static final String VALIDATE_JSON_PREFIX  = "{";

    /**
     * Creates a new instance.
     */
    public AjaxOrFormRequestFilter() {
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(new AjaxOrFormRequest(request), response);
    }

    @Slf4j
    public static class AjaxOrFormRequest extends HttpServletRequestWrapper {

        @Getter
        private final byte[] body;

        @Getter
        private final Map<String, Object> formMap;

        AjaxOrFormRequest(HttpServletRequest request) {
            super(request);
            String contentType = request.getContentType();
            String method = request.getMethod();
            boolean isPostOrPutRequest = SecurityConstants.POST_METHOD.equalsIgnoreCase(method) || SecurityConstants.PUT_METHOD.equalsIgnoreCase(method);
            boolean isJsonOrFormContentType =
                    contentType != null && (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE) || contentType.contains(MediaType.APPLICATION_JSON_VALUE));
            if (isPostOrPutRequest && isJsonOrFormContentType)
            {
                Map<String, Object> map = null;
                byte[] bytes = null;
                try
                {
                    // 获取 表单 字节数据
                    bytes = readAllBytes(request.getInputStream());
                    if (bytes.length != 0)
                    {
                        String jsonData = new String(bytes, StandardCharsets.UTF_8).trim();
                        // 转换为 map 类型, 并放入 request 域方便下次调用
                        if (jsonData.startsWith(VALIDATE_JSON_PREFIX))
                        {
                            //noinspection unchecked
                            map = MvcUtil.json2Object(jsonData, Map.class);
                        } else
                        {
                            map = ConvertUtil.string2JsonMap(jsonData, SecurityConstants.URL_PARAMETER_SEPARATOR,
                                                             SecurityConstants.KEY_VALUE_SEPARATOR);
                        }
                    }
                }
                catch (Exception e) {
                    log.error(String.format("读取请求数据失败: %s",e.getMessage()), e);
                }
                formMap = ofNullable(map).orElse(new HashMap<>(0));
                body = bytes;
            } else
            {
                body = null;
                formMap = null;
            }
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (body == null)
            {
                return super.getInputStream();
            }
            return new BodyInputStream(body);
        }

        @Override
        public String getParameter(String name) {
            if (formMap == null)
            {
                return super.getParameter(name);
            }
            return (String) formMap.get(name);
        }


    }

    private static class BodyInputStream extends ServletInputStream {

        private final InputStream delegate;

        public BodyInputStream(byte[] body) {
            this.delegate = new ByteArrayInputStream(body);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return this.delegate.read();
        }

        @Override
        public int read(@NonNull byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        @Override
        public int read(@NonNull byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        @Override
        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        @Override
        public int available() throws IOException {
            return this.delegate.available();
        }

        @Override
        public void close() throws IOException {
            this.delegate.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            this.delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return this.delegate.markSupported();
        }
    }

}