package top.dcenter.ums.security.social.provider.weibo.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import top.dcenter.ums.security.social.provider.gitee.adapter.StringJsonMessageConverterExtractor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static top.dcenter.ums.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_IDENTIFIER;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;

/**
 * WeiboOAuth2Template
 * @author edva8332
 * @author zyw
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Slf4j
public class WeiboOAuth2Template extends OAuth2Template {

    private ObjectMapper objectMapper;

    public WeiboOAuth2Template(String clientId, String clientSecret,
                               String authorizeUrl,
                               String accessTokenUrl, ObjectMapper objectMapper) {
        super(clientId, clientSecret,
              authorizeUrl,
              accessTokenUrl);
        setUseParametersForClientAuthentication(true);
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected RestTemplate createRestTemplate() {

        RestTemplate restTemplate = super.createRestTemplate();
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        // 替换 json处理器
        messageConverters.removeIf((c) -> c instanceof MappingJackson2HttpMessageConverter);
        messageConverters.add(new StringJsonMessageConverterExtractor());
        // 添加处理 html 格式的消息体转换器
        messageConverters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));

        return restTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AccessGrant postForAccessGrant(String accessTokenUrl,
                                             MultiValueMap<String, String> parameters) {

        String params = parameters.toSingleValueMap()
                .entrySet()
                .stream()
                .map(entry -> (entry.getKey() + KEY_VALUE_SEPARATOR + entry.getValue()))
                .collect(Collectors.joining(URL_PARAMETER_SEPARATOR));

        String url = accessTokenUrl + URL_PARAMETER_IDENTIFIER + params;

        WeiboAccessGrant weiboAccessGrant;
        try
        {
            String responseResult = getRestTemplate().postForObject(url, null, String.class);
            weiboAccessGrant = objectMapper.readValue(responseResult, WeiboAccessGrant.class);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new RestClientException(e.getMessage(), e);
        }
        return weiboAccessGrant;

    }

}