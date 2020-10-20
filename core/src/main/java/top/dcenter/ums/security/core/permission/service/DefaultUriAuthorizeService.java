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
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.permission.dto.UriResourcesDTO;
import top.dcenter.ums.security.core.vo.ResponseResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.responseWithJson;

/**
 * request 的 uri 访问权限控制服务.<br>
 * 实现 {@link AbstractUriAuthorizeService} 抽象类并注入 IOC 容器即可替换此类.
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/8 21:54
 */
@Slf4j
public class DefaultUriAuthorizeService extends AbstractUriAuthorizeService {

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Optional<Map<String, Map<String, UriResourcesDTO>>> getRolesAuthorities() {

        log.warn("AbstractUriAuthorizeService 抽象类未实现, 使用权限服务必须实现此抽象类.");
        return Optional.empty();
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