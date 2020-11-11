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

package top.dcenter.ums.security.core.permission.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.core.api.permission.service.UpdateAndCacheAuthoritiesService;
import top.dcenter.ums.security.core.vo.ResponseResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.responseWithJson;

/**
 * request 的 uri 访问权限控制服务. 此类的目的是提示用户必须实现 {@link AbstractUriAuthorizeService} <br>
 * 实现 {@link AbstractUriAuthorizeService} 抽象类并注入 IOC 容器即可替换此类. <br>
 * 另外推荐实现 {@link AbstractUriAuthorizeService} 同时实现 {@link UpdateAndCacheAuthoritiesService} 更新与缓存权限服务, 有助于提高授权服务性能.
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/8 21:54
 */
@Slf4j
public class DefaultUriAuthorizeService extends AbstractUriAuthorizeService {

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @NonNull
    public Map<String, Map<String, Set<String>>> getRolesAuthorities() {

        log.error("使用基于 角色 的权限服务必须实现此接口 AbstractUriAuthorizeService 或 UriAuthorizeService.");
        throw new RuntimeException("未实现获取所有角色的 uri(资源) 的权限的接口 AbstractUriAuthorizeService 或 UriAuthorizeService");
    }

    @Override
    @NonNull
    public Map<String, Map<String, Set<String>>> getRolesAuthoritiesOfTenant(String tenantAuthority) {
        log.error("使用 多租户 权限服务必须实现此接口 AbstractUriAuthorizeService 或 UriAuthorizeService.");
        throw new RuntimeException("未实现获取多租户的所有角色的 uri(资源) 的权限的接口 AbstractUriAuthorizeService 或 UriAuthorizeService");
    }

    @Override
    @NonNull
    public Map<String, Map<String, Set<String>>> getRolesAuthoritiesOfScope(Set<String> scopeSet) {
        log.error("使用 SCOPE 权限服务必须实现此接口 AbstractUriAuthorizeService 或 UriAuthorizeService.");
        throw new RuntimeException("未实现获取 SCOPE 的所有角色的 uri(资源) 的权限的接口 AbstractUriAuthorizeService 或 UriAuthorizeService");
    }

    @Override
    public void handlerError(int status, HttpServletResponse response) {

        try
        {
            responseWithJson(response, status, objectMapper.writeValueAsString(ResponseResult.fail(ErrorCodeEnum.PERMISSION_DENY)));
        }
        catch (IOException e)
        {
            log.error(String.format("权限控制错误响应异常: status=%s, error=%s", status, e.getMessage()), e);
        }
    }

}