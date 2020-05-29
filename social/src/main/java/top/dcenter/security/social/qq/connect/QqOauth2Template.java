package top.dcenter.security.social.qq.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import top.dcenter.security.core.util.CastUtil;
import top.dcenter.security.social.qq.api.Qq;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.CHARSET_UTF8;
import static top.dcenter.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;

/**
 * QqOauth2Template
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/10 22:43
 */
@Slf4j
public class QqOauth2Template extends OAuth2Template {

    private final ObjectMapper objectMapper;

    public QqOauth2Template(String clientId,
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
        // 添加处理 html 格式的消息体转换器
        messageConverters.add(new StringHttpMessageConverter(Charset.forName(CHARSET_UTF8)));
        return restTemplate;
    }

    @Override
    protected QqAccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
        Map<String, String> responseMap = extractAccessGrantMap(getRestTemplate().postForObject(accessTokenUrl, parameters, String.class));
        String responseStr = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
        String expiresInStr = responseMap.get(Qq.EXPIRES_IN);
        Long expiresIn = null;
        if (!StringUtils.isEmpty(expiresInStr))
        {
            expiresIn = Long.valueOf(expiresInStr);
        }

        return new QqAccessGrant(responseMap.get(Qq.ACCESS_TOKEN), responseMap.get(Qq.SCOPE),
                                 responseMap.get(Qq.REFRESH_TOKEN), expiresIn);
    }

    private Map<String, String> extractAccessGrantMap(String responseResult) {
        return CastUtil.string2Map(responseResult, URL_PARAMETER_SEPARATOR, KEY_VALUE_SEPARATOR);
    }
}
