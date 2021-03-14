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
package top.dcenter.ums.security.jwt.endpoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import net.minidev.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.common.utils.JsonUtil;
import top.dcenter.ums.security.common.utils.ReflectionUtil;
import top.dcenter.ums.security.jwt.api.endpoind.service.JwkEndpointPermissionService;
import top.dcenter.ums.security.jwt.exception.JwkSetUriAccessDeniedException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

import static top.dcenter.ums.security.core.mdc.utils.MdcUtil.getMdcTraceId;

/**
 * 曝露 jwk-set-uri<br>
 * Jwt token 的 keyPair 签名 key:<br>
 * 1. 生成密钥键值对命令：keytool -genkeypair -alias zyw -keyalg RSA -keypass 123456 -keystore zyw.jks -storepass 123456 <br>
 * 2. 生成公钥命令：keytool -list -rfc --keystore zyw.jks | openssl x509 -inform pem -pubkey <br>
 *
 * @author YongWu zheng
 * @version V2.0  Created by 2020-12-05 21:23
 */
@ResponseBody
public class JwkEndpoint implements InitializingBean, ApplicationContextAware {

    public static final String JWS_SET_URI = "/.well-known/jwks.json";
    private final String jwsSetJsonString;
    private final JwkEndpointPermissionService jwkEndpointPermissionService;

    private ApplicationContext applicationContext;

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public JwkEndpoint(@NonNull RSAPublicKey rsaPublicKey, @NonNull String jksAlgorithm,
                       @NonNull JwkEndpointPermissionService jwkEndpointPermissionService, @Nullable String kid) throws InvocationTargetException, IllegalAccessException {
        this.jwkEndpointPermissionService = jwkEndpointPermissionService;
        RSAKey key = new RSAKey.Builder(rsaPublicKey).build();
        JWKSet jwkSet = new JWKSet(key);
        // 改用反射方式调用, 增加对 nimbus-jose-jwt:9.x.x/8.x.x 的兼容性
        Method toJsonObjectMethod = ReflectionUtils.findMethod(JWKSet.class, "toJSONObject");
        Object jwk = toJsonObjectMethod.invoke(jwkSet);
        Map<String, Object> publicKey;
        JSONObject jsonObject;
        if (jwk instanceof JSONObject) 
        {
            jsonObject = ((JSONObject) jwk);
            publicKey = (Map<String, Object>) ((List<Object>) jsonObject.get("keys")).get(0);
        }
        // 对 nimbus-jose-jwt:9.1.x 的兼容.
        else if (jwk instanceof Map) {
            publicKey = (Map<String, Object>) jwk;
            publicKey = (Map<String, Object>) ((List<Object>) publicKey.get("keys")).get(0);
        }
        else {
            throw new RuntimeException("生成 jws set json string 错误");
        }

        publicKey.put("alg", jksAlgorithm);
        if (StringUtils.hasText(kid)) {
            publicKey.put("kid", kid);
        }
        this.jwsSetJsonString = JsonUtil.toJsonString(jwk);
    }

    @RequestMapping(path = JWS_SET_URI, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String getKey(HttpServletRequest request) {
        if (!this.jwkEndpointPermissionService.hasPermission(request)) {
            throw new JwkSetUriAccessDeniedException(ErrorCodeEnum.NOT_FOUND, getMdcTraceId());
        }
        return this.jwsSetJsonString;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 在 mvc 中做 Uri 映射等动作
        ReflectionUtil.registerController("jwkEndpoint",
                                          (GenericApplicationContext) this.applicationContext,
                                          JwkEndpoint.class);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
