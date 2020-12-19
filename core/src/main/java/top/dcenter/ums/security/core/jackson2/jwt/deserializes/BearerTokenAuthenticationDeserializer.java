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
package top.dcenter.ums.security.core.jackson2.jwt.deserializes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.io.IOException;
import java.util.Collection;

/**
 * BearerTokenAuthentication 反序列化器
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.19 17:25
 */
public class BearerTokenAuthenticationDeserializer extends StdDeserializer<BearerTokenAuthentication> {

    public BearerTokenAuthenticationDeserializer() {
        super(BearerTokenAuthentication.class);
    }


    @Override
    public BearerTokenAuthentication deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        final JsonNode jsonNode = mapper.readTree(p);

        // 获取 authorities
        Collection<? extends GrantedAuthority> tokenAuthorities =
                mapper.convertValue(jsonNode.get("authorities"),
                                    new TypeReference<Collection<SimpleGrantedAuthority>>() {});
        final JsonNode detailsNode = jsonNode.get("details");
        final JsonNode principalNode = jsonNode.get("principal");

        // 两个字段 credentials/token 都是 OAuth2AccessToken, 只需获取一个就行
        final JsonNode tokenNode = jsonNode.get("token");

        // 创建 jwt 对象
        JavaType tokenJavaType = mapper.getTypeFactory().constructType(OAuth2AccessToken.class);
        OAuth2AccessToken token = mapper.convertValue(tokenNode, tokenJavaType);

        JavaType principalJavaType = mapper.getTypeFactory().constructType(OAuth2AuthenticatedPrincipal.class);
        OAuth2AuthenticatedPrincipal principal = mapper.convertValue(principalNode, principalJavaType);

        BearerTokenAuthentication bearerToken = new BearerTokenAuthentication(principal, token, tokenAuthorities);

        // 为了安全, 不信任反序列化后的凭证; 一般认证成功后都会自动释放密码.
        bearerToken.eraseCredentials();

        // 创建 details 对象
        if (!(detailsNode.isNull() || detailsNode.isMissingNode())) {
            WebAuthenticationDetails details = mapper.convertValue(detailsNode, new TypeReference<WebAuthenticationDetails>(){});
            bearerToken.setDetails(details);
        }

        return bearerToken;

    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using = BearerTokenAuthenticationDeserializer.class)
    public interface BearerTokenAuthenticationMixin {}
}
