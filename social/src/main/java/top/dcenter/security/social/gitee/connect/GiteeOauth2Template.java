package top.dcenter.security.social.gitee.connect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import top.dcenter.security.social.gitee.adapter.OAuth2Template;
import top.dcenter.security.social.gitee.adapter.RestTemplate;
import top.dcenter.security.social.gitee.adapter.StringJsonMessageConverterExtractor;

import java.nio.charset.Charset;
import java.util.List;

import static top.dcenter.security.core.consts.SecurityConstants.CHARSET_UTF8;

/**
 * GiteeOauth2Template
 * @author zyw
 * @version V1.0  Created by 2020/5/10 22:43
 */
@Slf4j
public class GiteeOauth2Template extends OAuth2Template {
    private final ObjectMapper objectMapper;

    public GiteeOauth2Template(String clientId,
                               String clientSecret,
                               String authorizeUrl,
                               String accessTokenUrl,
                               ObjectMapper objectMapper) {
        super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
        // 设置 UseParameters 时 带上 clientId 与 clientSecret
        setUseParametersForClientAuthentication(true);
        this.objectMapper = objectMapper;
    }

    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate restTemplate = super.createRestTemplate();
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        // 替换 json处理器
        messageConverters.removeIf((c) -> c instanceof MappingJackson2HttpMessageConverter);
        messageConverters.add(new StringJsonMessageConverterExtractor());
        // 添加处理 html 格式的消息体转换器
        messageConverters.add(new StringHttpMessageConverter(Charset.forName(CHARSET_UTF8)));

        return restTemplate;
    }

    @Override
    protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
        String responseStr = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
        try
        {
            GiteeAccessGrant giteeAccessGrant = this.objectMapper.readValue(responseStr, GiteeAccessGrant.class);
            return new GiteeAccessGrant(giteeAccessGrant.getAccessToken(),
                                        giteeAccessGrant.getScope(),
                                        giteeAccessGrant.getRefreshToken(),
                                        giteeAccessGrant.getExpiresIn(),
                                        giteeAccessGrant.getCreatedAt(),
                                        giteeAccessGrant.getTokenType());

        }
        catch (JsonProcessingException e)
        {
            log.error(e.getMessage(), e);
            throw new HttpClientErrorException(HttpStatus.OK,
                                               "ObjectMapper string 转 AccessGrant 异常: " + e.getMessage());
        }
    }

}
