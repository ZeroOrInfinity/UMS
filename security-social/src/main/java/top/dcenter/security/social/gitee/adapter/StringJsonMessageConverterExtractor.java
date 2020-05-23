package top.dcenter.security.social.gitee.adapter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 针对 Gitee，处理 gitee 服务商回调时返回的 JSON 进行解析。
 * @author zyw
 * @version V1.0  Created by 2020/5/19 12:32
 */
public class StringJsonMessageConverterExtractor implements GenericHttpMessageConverter<String> {

    private List<MediaType> mediaTypes;
    private ObjectMapper objectMapper;
    public StringJsonMessageConverterExtractor() {
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        this.mediaTypes = mediaTypes;
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return clazz.isAssignableFrom(String.class) && mediaTypes.contains(mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return this.mediaTypes;
    }

    @Override
    public String read(Class<? extends String> clazz, HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {
        byte[] bytes = IOUtils.toByteArray(inputMessage.getBody());
        return new String(bytes, "utf-8");
    }

    @Override
    public void write(String s, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        // do nothing
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return ((Class<?>) type).isAssignableFrom(String.class) && mediaTypes.contains(mediaType);
    }

    @Override
    public String read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        byte[] bytes = IOUtils.toByteArray(inputMessage.getBody());
        return new String(bytes, "utf-8");
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public void write(String giteeAccessGrant, Type type, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        // do nothing
    }

}
