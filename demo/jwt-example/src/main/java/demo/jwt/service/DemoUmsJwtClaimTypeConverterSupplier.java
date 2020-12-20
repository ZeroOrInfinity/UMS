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
package demo.jwt.service;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.core.converter.ClaimConversionService;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.jwt.api.supplier.JwtClaimTypeConverterSupplier;
import top.dcenter.ums.security.jwt.enums.JwtCustomClaimNames;
import top.dcenter.ums.security.jwt.supplier.UmsJwtClaimTypeConverterSupplier;

import java.net.URL;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 只是 {@link UmsJwtClaimTypeConverterSupplier} 的原样拷贝, 示例替换 {@link UmsJwtClaimTypeConverterSupplier}.
 * jwt claim set converter supplier
 * @see JwtClaimTypeConverterSupplier
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.6 16:49
 */
@Component
public class DemoUmsJwtClaimTypeConverterSupplier implements JwtClaimTypeConverterSupplier {

    private static final ConversionService CONVERSION_SERVICE = ClaimConversionService.getSharedInstance();

    private static final TypeDescriptor OBJECT_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(Object.class);

    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);

    private static final TypeDescriptor INSTANT_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(Instant.class);

    private static final TypeDescriptor URL_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(URL.class);

    private static final TypeDescriptor COLLECTION_STRING_DESCRIPTOR = TypeDescriptor.collection(Collection.class, STRING_TYPE_DESCRIPTOR);

    private static final TypeDescriptor MAP_STRING_OBJECT_DESCRIPTOR = TypeDescriptor.map(LinkedHashMap.class,
                                                                                          STRING_TYPE_DESCRIPTOR, OBJECT_TYPE_DESCRIPTOR);

    private static Converter<Object, ?> getConverter(TypeDescriptor targetDescriptor) {
        return (source) -> CONVERSION_SERVICE.convert(source, OBJECT_TYPE_DESCRIPTOR, targetDescriptor);
    }

    @Override
    @NonNull
    public Map<String, Converter<Object, ?>> getConverter() {
        Map<String, Converter<Object, ?>> map = new HashMap<>(16);
        map.put(JwtCustomClaimNames.USER_ID.getClaimName(), getConverter(STRING_TYPE_DESCRIPTOR));
        map.put(JwtCustomClaimNames.USERNAME.getClaimName(), getConverter(STRING_TYPE_DESCRIPTOR));
        map.put(JwtCustomClaimNames.TENANT_ID.getClaimName(), getConverter(STRING_TYPE_DESCRIPTOR));
        map.put(JwtCustomClaimNames.CLIENT_ID.getClaimName(), getConverter(STRING_TYPE_DESCRIPTOR));
        map.put(JwtCustomClaimNames.USER_DETAILS.getClaimName(), getConverter(MAP_STRING_OBJECT_DESCRIPTOR));
        map.put(JwtCustomClaimNames.AUTHORITIES.getClaimName(), getConverter(COLLECTION_STRING_DESCRIPTOR));
        map.put(JwtCustomClaimNames.SCOPE.getClaimName(), getConverter(COLLECTION_STRING_DESCRIPTOR));
        map.put(JwtCustomClaimNames.SCP.getClaimName(), getConverter(COLLECTION_STRING_DESCRIPTOR));
        return Collections.unmodifiableMap(map);
    }
}
