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

package demo.permission.service.impl;

import demo.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.core.permission.dto.UriResourcesDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

/**
 * request 的 uri 访问权限控制服务.<br>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/8 21:54
 */
@Service
@Slf4j
public class UriAuthorizeServiceImpl extends AbstractUriAuthorizeService {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 获取角色的 uri 的权限 map.<br>
     *     返回值为: Map(role, Map(uri, UriResourcesDTO))
     * @return Map(String, Map(String, String)) 的 key 为必须包含"ROLE_"前缀的角色名称(如: ROLE_ADMIN), value 为 UriResourcesDTO map
     * (key 为 uri, 此 uri 可以为 antPath 通配符路径,如 /user/**; value 为 UriResourcesDTO).
     */
    @Override
    public Optional<Map<String, Map<String, UriResourcesDTO>>> getRolesAuthorities() {

        // 从数据源获取 RolesAuthorities
        Map<String, Map<String, UriResourcesDTO>> rolesAuthorities = sysRoleService.getRolesAuthorities();

        return Optional.of(rolesAuthorities);
    }

    /**
     * @param status   返回状态
     * @param response response
     */
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