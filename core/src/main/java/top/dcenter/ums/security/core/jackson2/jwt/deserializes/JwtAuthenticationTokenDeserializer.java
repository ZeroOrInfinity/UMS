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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import top.dcenter.ums.security.common.utils.JsonUtil;

import java.io.IOException;
import java.util.Collection;

import static java.util.Objects.nonNull;

/**
 * JwtAuthenticationToken 反序列化器
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.19 17:25
 */
public class JwtAuthenticationTokenDeserializer extends StdDeserializer<JwtAuthenticationToken> {

    public JwtAuthenticationTokenDeserializer() {
        super(JwtAuthenticationToken.class);
    }


    @Override
    public JwtAuthenticationToken deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        final JsonNode jsonNode = mapper.readTree(p);

        // 获取 authorities
        Collection<? extends GrantedAuthority> tokenAuthorities =
                mapper.convertValue(jsonNode.get("authorities"),
                                    new TypeReference<Collection<SimpleGrantedAuthority>>() {});
        final boolean authenticated = jsonNode.get("authenticated").asBoolean();
        final JsonNode detailsNode = jsonNode.get("details");
        final String name = jsonNode.get("name").asText(null);
        // 三个字段 credentials/principal/token 都是 jwt, 只需获取一个就行
        final JsonNode tokenNode = jsonNode.get("token");

        // 创建 jwt 对象
        Object jwt = JsonUtil.getObject(mapper, tokenNode);

        JwtAuthenticationToken token;
        if (authenticated) {
            if (nonNull(name)) {
                token = new JwtAuthenticationToken((Jwt) jwt, tokenAuthorities, name);
            }
            else {
                token = new JwtAuthenticationToken((Jwt) jwt, tokenAuthorities);
            }
        }
        else {
            token = new JwtAuthenticationToken((Jwt) jwt);
        }

        // 为了安全, 不信任反序列化后的凭证; 一般认证成功后都会自动释放密码.
        token.eraseCredentials();

        // 创建 details 对象
        if (!(detailsNode.isNull() || detailsNode.isMissingNode())) {
            WebAuthenticationDetails details = mapper.convertValue(detailsNode, new TypeReference<WebAuthenticationDetails>(){});
            token.setDetails(details);
        }

        return token;

    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using = JwtAuthenticationTokenDeserializer.class)
    public interface JwtAuthenticationTokenMixin {}
}
