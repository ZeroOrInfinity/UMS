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
package top.dcenter.ums.security.core.redis.jackson2;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import top.dcenter.ums.security.core.oauth.deserializes.AnonymousAuthenticationTokenJsonDeserializer;
import top.dcenter.ums.security.core.oauth.deserializes.Auth2AuthenticationTokenJsonDeserializer;
import top.dcenter.ums.security.core.oauth.deserializes.AuthUserJsonDeserializer;
import top.dcenter.ums.security.core.oauth.deserializes.TemporaryUserDeserializer;
import top.dcenter.ums.security.core.oauth.deserializes.UserDeserializer;
import top.dcenter.ums.security.core.oauth.token.Auth2AuthenticationToken;
import top.dcenter.ums.security.core.oauth.userdetails.TemporaryUser;
import top.dcenter.ums.security.jwt.jackson2.deserializer.BaseJwtMixin;
import top.dcenter.ums.security.jwt.jackson2.deserializer.BearerTokenAuthenticationDeserializer;
import top.dcenter.ums.security.jwt.jackson2.deserializer.BearerTokenAuthenticationTokenDeserializer;
import top.dcenter.ums.security.jwt.jackson2.deserializer.DefaultOAuth2AuthenticatedPrincipalDeserializer;
import top.dcenter.ums.security.jwt.jackson2.deserializer.JwtAuthenticationTokenDeserializer;

/**
 * Auth2 Jackson2 Module
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/28 10:58
 */
public class Auth2Jackson2Module extends SimpleModule {

	public Auth2Jackson2Module() {
		super(Auth2Jackson2Module.class.getName(), new Version(1, 0, 0, null, null, null));
	}

	@Override
	public void setupModule(SetupContext context) {
		SecurityJackson2Modules.enableDefaultTyping(context.getOwner());
	    context.setMixInAnnotations(Auth2AuthenticationToken.class,
                                    Auth2AuthenticationTokenJsonDeserializer.Auth2AuthenticationTokenMixin.class);
		context.setMixInAnnotations(Jwt.class,
		                            BaseJwtMixin.class);
		context.setMixInAnnotations(JwtAuthenticationToken.class,
		                            JwtAuthenticationTokenDeserializer.JwtAuthenticationTokenMixin.class);
		context.setMixInAnnotations(DefaultOAuth2AuthenticatedPrincipal.class,
		                            DefaultOAuth2AuthenticatedPrincipalDeserializer.DefaultOAuth2AuthenticatedPrincipalMixin.class);
		context.setMixInAnnotations(BearerTokenAuthentication.class,
		                            BearerTokenAuthenticationDeserializer.BearerTokenAuthenticationMixin.class);
		context.setMixInAnnotations(BearerTokenAuthenticationToken.class,
		                            BearerTokenAuthenticationTokenDeserializer.BearerTokenAuthenticationTokenMixin.class);
		context.setMixInAnnotations(AnonymousAuthenticationToken.class,
		                            AnonymousAuthenticationTokenJsonDeserializer.AnonymousAuthenticationTokenMixin.class);
		context.setMixInAnnotations(User.class,
		                            UserDeserializer.UserMixin.class);
		context.setMixInAnnotations(TemporaryUser.class,
		                            TemporaryUserDeserializer.TemporaryUserMixin.class);
		context.setMixInAnnotations(AuthUser.class,
		                            AuthUserJsonDeserializer.AuthUserMixin.class);
	}
}