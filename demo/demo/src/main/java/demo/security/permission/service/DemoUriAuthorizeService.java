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

package demo.security.permission.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.core.api.permission.entity.UriResourcesDTO;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * request 的 uri 访问权限控制服务.<br>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/8 21:54
 */
@Component
@Slf4j
public class DemoUriAuthorizeService extends AbstractUriAuthorizeService {

    @Override
    public Optional<Map<String, Map<String, UriResourcesDTO>>> getRolesAuthorities() {

        // 生产环境: 从数据源获取 RolesAuthorities

        // 示例代码
        Map<String, Map<String, UriResourcesDTO>> rolesAuthorities = new HashMap<>(2);
        Map<String, UriResourcesDTO> uriAuthority = new HashMap<>(1);
        UriResourcesDTO uriResourcesDTO = new UriResourcesDTO("/test/permission/**", "/test/permission:add");

        uriAuthority.put("/test/permission/**", uriResourcesDTO);
        uriAuthority.put("/test/pass/**", uriResourcesDTO);

        rolesAuthorities.put("ROLE_USER", uriAuthority);
        rolesAuthorities.put("ROLE_ANONYMOUS", uriAuthority);
        return Optional.of(rolesAuthorities);
    }

    @Override
    public void handlerError(int status, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        try (PrintWriter writer = response.getWriter())
        {
            writer.write("{\"msg\":\"demo: 您没有访问权限或未登录\"}");
            writer.flush();
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
        }
    }

}