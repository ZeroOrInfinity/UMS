package top.dcenter.security.social.banding;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.social.connect.Connection;
import top.dcenter.security.core.enums.LoginProcessType;
import top.dcenter.security.core.properties.ClientProperties;
import top.dcenter.security.core.vo.ResponseResult;
import top.dcenter.security.social.properties.SocialProperties;
import top.dcenter.security.social.api.callback.ShowConnectViewService;
import top.dcenter.security.social.vo.SocialUserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static top.dcenter.security.core.consts.SecurityConstants.CHARSET_UTF8;

/**
 * 默认的绑定与解绑信息回显,这里是简单实现，
 * @author zyw
 * @version V1.0  Created by 2020/5/26 13:52
 */
public class DefaultShowConnectViewService implements ShowConnectViewService {

    private final ClientProperties clientProperties;
    private final ObjectMapper objectMapper;
    private final SocialProperties socialProperties;

    public DefaultShowConnectViewService(ClientProperties clientProperties, ObjectMapper objectMapper, SocialProperties socialProperties) {
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
        // JSON
        if (LoginProcessType.JSON.equals(clientProperties.getLoginProcessType()))
        {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF8);
            if (userInfoList == null || userInfoList.isEmpty()) {
                response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.success("解绑成功")));
            } else {
                response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.success("绑定成功", userInfoList)));
            }
            return;
        }
        // HTML
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setCharacterEncoding(CHARSET_UTF8);
        if (userInfoList == null || userInfoList.isEmpty()) {
            response.getWriter().write("<h3>解绑成功</h3>");
        } else {
            StringBuilder sb = new StringBuilder("<h3>绑定成功</h3>\n");
            sb.append("<ul>\n");
            userInfoList.forEach(userInfo -> sb.append("<li>\n")
              .append("<img src=\"")
              .append(userInfo.getAvatarUrl())
              .append("\" style=\"width: 100px; height: 100px\"/>\n")
              .append("<p>userId = ")
              .append(userInfo.getUserId())
              .append("</p>\n")
              .append("<p>providerId = ")
              .append(userInfo.getProviderId())
              .append("</p>\n")
              .append("<p>providerUserId = ")
              .append(userInfo.getProviderUserId())
              .append("</p>\n")
              .append("</li>\n"));
            sb.append("</ul>\n");
            response.getWriter().write(sb.toString());
        }
    }
}
