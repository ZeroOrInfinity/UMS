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

package top.dcenter.ums.security.social.banding;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.social.connect.Connection;
import top.dcenter.ums.security.core.properties.ClientProperties;
import top.dcenter.ums.security.core.vo.ResponseResult;
import top.dcenter.ums.security.social.api.banding.ShowConnectViewService;
import top.dcenter.ums.security.social.properties.SocialProperties;
import top.dcenter.ums.security.social.vo.SocialUserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static top.dcenter.ums.security.core.consts.SecurityConstants.CHARSET_UTF8;

/**
 * 默认的,这里是简单实现，返回 Json格式
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/26 13:52
 */
public class DefaultShowConnectViewServiceImpl implements ShowConnectViewService {

    private final ClientProperties clientProperties;
    private final ObjectMapper objectMapper;
    private final SocialProperties socialProperties;

    public DefaultShowConnectViewServiceImpl(ClientProperties clientProperties, ObjectMapper objectMapper, SocialProperties socialProperties) {
        this.clientProperties = clientProperties;
        this.objectMapper = objectMapper;
        this.socialProperties = socialProperties;
    }

    @Override
    public void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        //noinspection unchecked
        List<Connection<?>> connections = (List<Connection<?>>) model.get(this.socialProperties.getBandingProviderConnectionListName());

        List<SocialUserInfo> userInfoList = null;
        if (connections != null && !connections.isEmpty()) {
            userInfoList = connections.stream()
                            .map(Connection::createData)
                            .map((connectionData) -> new SocialUserInfo(connectionData.getProviderId(),
                                                                        connectionData.getProviderUserId(),
                                                                        connectionData.getDisplayName(),
                                                                        connectionData.getImageUrl()))
                            .collect(Collectors.toList());
        }

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CHARSET_UTF8);
        if (userInfoList == null || userInfoList.isEmpty()) {
            response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.success("解绑成功")));
        } else {
            response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.success("绑定成功", userInfoList)));
        }

    }
}