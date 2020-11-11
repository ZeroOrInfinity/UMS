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
package demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.core.vo.ResponseResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.responseWithJson;

/**
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/11 17:09
 */
@Slf4j
public class DemoUriAuthorizeServiceImpl extends AbstractUriAuthorizeService {

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private ObjectMapper objectMapper;

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

    @Override
    @NonNull
    public Map<String, Map<String, Set<String>>> getRolesAuthorities() {
        // do nothing 具体看 permission-example 的 demo.permission.service.impl.UriAuthorizeServiceImpl
        return new HashMap<>(0);
    }

}
