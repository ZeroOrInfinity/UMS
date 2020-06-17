package top.dcenter.security.core.auth.filter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import top.dcenter.security.core.util.ConvertUtil;

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
import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.security.core.consts.SecurityConstants.POST_METHOD;
import static top.dcenter.security.core.consts.SecurityConstants.PUT_METHOD;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;

/**
 * 增加对 Ajax 格式与 form 格式的解析, 覆写了 {@link JsonRequest#getParameter(String)},
 * {@link JsonRequest#getInputStream()}, 添加了 {@link JsonRequest#getFormMap()}, {@link JsonRequest#getBody()}
 * @author zyw
 * @version V1.0  Created by 2020/6/9 14:01
 */
public class JsonRequestFilter extends OncePerRequestFilter {

    /**
     * 验证 request 中参数是否是 json 的字符串的前缀,
     */
    public static final String VALIDATE_JSON_PREFIX  = "{";

    private ObjectMapper objectMapper;

    /**
     * Creates a new instance.
     *
     * @param objectMapper
     */
    public JsonRequestFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(new JsonRequest(request, objectMapper), response);
    }

    @Slf4j
    public static class JsonRequest extends HttpServletRequestWrapper {

        private ObjectMapper objectMapper;

        @Getter
        private final byte[] body;

        @Getter
        private final Map<String, Object> formMap;

        JsonRequest(HttpServletRequest request, ObjectMapper objectMapper) {
            super(request);
            this.objectMapper = objectMapper;
            String contentType = request.getContentType();
            String method = request.getMethod();
            boolean isPostOrPutRequest = POST_METHOD.equalsIgnoreCase(method) || PUT_METHOD.equalsIgnoreCase(method);
            boolean isJsonOrFormContentType =
                    contentType != null && (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE) || contentType.contains(MediaType.APPLICATION_JSON_VALUE));
            if (isPostOrPutRequest && isJsonOrFormContentType)
            {
                Map<String, Object> map = null;
                byte[] bytes = null;
                try
                {
                    // 获取 表单 字节数据
                    bytes = request.getInputStream().readAllBytes();
                    if (bytes.length != 0)
                    {
                        String jsonData = new String(bytes, StandardCharsets.UTF_8);
                        // 转换为 map 类型, 并放入 request 域方便下次调用
                        if (StringUtils.startsWith(jsonData, VALIDATE_JSON_PREFIX))
                        {
                            map = this.objectMapper.readValue(jsonData, Map.class);
                        } else
                        {
                            map = ConvertUtil.string2JsonMap(jsonData, URL_PARAMETER_SEPARATOR,
                                                             KEY_VALUE_SEPARATOR);
                        }
                    }
                }
                catch (Exception e) {
                    log.error(String.format("读取请求数据失败: %s",e.getMessage()), e);
                }
                formMap = map;
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
        public int read(byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        @Override
        public int read(byte[] b) throws IOException {
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
