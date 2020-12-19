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
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import top.dcenter.ums.security.common.utils.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

/**
 * DefaultOAuth2AuthenticatedPrincipal 反序列化器
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.19 17:25
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class DefaultOAuth2AuthenticatedPrincipalDeserializer extends StdDeserializer<DefaultOAuth2AuthenticatedPrincipal> {

    public DefaultOAuth2AuthenticatedPrincipalDeserializer() {
        super(DefaultOAuth2AuthenticatedPrincipal.class);
    }


    @Override
    public DefaultOAuth2AuthenticatedPrincipal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        final JsonNode jsonNode = mapper.readTree(p);

        // 获取 authorities
        JsonNode authoritiesNode = jsonNode.get("authorities");
        Collection<GrantedAuthority> authorities;

        try {
            Collection<? extends GrantedAuthority> tempAuthorities =
                    mapper.convertValue(jsonNode.get("authorities"),
                                        new TypeReference<Collection<SimpleGrantedAuthority>>() {});
            //noinspection unchecked
            authorities = (Collection<GrantedAuthority>) tempAuthorities;
        }
        catch (Exception e) {
            String authoritiesString = authoritiesNode.toString();
            String prefix = "[\"java.util.Collections$UnmodifiableCollection\",";
            if (authoritiesString.startsWith(prefix)) {
                authoritiesString = authoritiesString.substring(prefix.length());

                int cutLen = 2;
                //noinspection AlibabaUndefineMagicConstant
                if (authoritiesString.length() == 3) {
                    cutLen = 1;
                }

                authoritiesString = authoritiesString.substring(0, authoritiesString.length() - cutLen);
            }

            String prefix2 = "\\[\"org\\.springframework\\.security\\.core\\.authority\\.SimpleGrantedAuthority\",";
            authoritiesString = authoritiesString.replaceAll(prefix2, "");

            authoritiesString = authoritiesString.replaceAll("],", ",");

            //noinspection unchecked
            List<LinkedHashMap<String, String>> list = JsonUtil.json2Object(authoritiesString, ArrayList.class);
            authorities = new ArrayList<>();

            if (nonNull(list)) {
                for (LinkedHashMap<String, String> map : list) {
                    authorities.add(new SimpleGrantedAuthority(map.get("authority")));
                }
            }

        }

        final JsonNode attributesNode = jsonNode.get("attributes");
        final String name = jsonNode.get("name").asText(null);

        // 创建 authorities map
        Map<String, Object> attributes = mapper.readValue(attributesNode.toString(),
                                                          new TypeReference<Map<String, Object>>() {});

        DefaultOAuth2AuthenticatedPrincipal principal;
        if (nonNull(name)) {
            principal = new DefaultOAuth2AuthenticatedPrincipal(name, attributes, authorities);
        }
        else {
            principal = new DefaultOAuth2AuthenticatedPrincipal(attributes, authorities);
        }

        return principal;

    }

    @SuppressWarnings("AlibabaClassNamingShouldBeCamel")
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using = DefaultOAuth2AuthenticatedPrincipalDeserializer.class)
    public interface DefaultOAuth2AuthenticatedPrincipalMixin {}
}
