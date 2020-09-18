package top.dcenter.ums.security.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import top.dcenter.ums.security.core.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.enums.LoginProcessType;
import top.dcenter.ums.security.core.exception.AbstractResponseJsonAuthenticationException;
import top.dcenter.ums.security.core.properties.ClientProperties;
import top.dcenter.ums.security.core.vo.ResponseResult;
import top.dcenter.ums.security.core.consts.SecurityConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * auth util
 *
 * @author zyw
 * @version V1.0  Created by 2020/6/1 22:39
 */
public class AuthenticationUtil {

    /**
     * 在提取 User-Agent 时, 需要被移除掉的字符的正则表达式
     */
    public static final String EXTRACT_USER_AGENT_REGEX = "[\\.|\\d|\\s|\\(|\\)]";


    /**
     * 判断 exception 是不是 {@link AbstractResponseJsonAuthenticationException} 的子类, 如果不是, 则返回 null.
     * @param exception not null
     * @return  如果不是 {@link AbstractResponseJsonAuthenticationException} 的子类, 则返回 null.
     */
    public static AbstractResponseJsonAuthenticationException getAbstractResponseJsonAuthenticationException(AuthenticationException exception) {
        AbstractResponseJsonAuthenticationException e = null;
        if (exception instanceof AbstractResponseJsonAuthenticationException)
        {
            e = (AbstractResponseJsonAuthenticationException) exception;
        }
        return e;
    }


    /**
     * 认证失败处理
     * {@link AbstractResponseJsonAuthenticationException} 类的异常, Response 会返回 Json 数据.
     * @param response          {@link javax.servlet.http.HttpServletRequest}
     * @param exception         {@link HttpServletResponse}
     * @param objectMapper      {@link ObjectMapper}
     * @param clientProperties {@link ClientProperties}
     * @return 如果通过 {@link HttpServletResponse} 返回 JSON 数据则返回 true, 否则 false
     * @throws IOException IOException
     */
    public static boolean authenticationFailureProcessing(HttpServletResponse response, AuthenticationException exception,
                                                          AbstractResponseJsonAuthenticationException e, String acceptHeader,
                                                          ObjectMapper objectMapper, ClientProperties clientProperties) throws IOException {

        boolean isJsonProcessType = LoginProcessType.JSON.equals(clientProperties.getLoginProcessType());
        boolean isAcceptHeader =
                StringUtils.isNotBlank(acceptHeader) && (acceptHeader.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE) || acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE));
        // 判断是否返回 json 类型
        if (isJsonProcessType || isAcceptHeader)
        {

            int status = HttpStatus.UNAUTHORIZED.value();
            ResponseResult result;
            if (e != null)
            {
                status = e.getErrorCodeEnum().getCode();
                result = ResponseResult.fail(e.getErrorCodeEnum(), e.getData());
            }
            else
            {
                result = ResponseResult.fail(exception.getMessage(), ErrorCodeEnum.UNAUTHORIZED);
            }

            responseWithJson(response, status, objectMapper.writeValueAsString(result));
            return true;
        }
        return false;
    }

    /**
     * 向客户端响应 json 格式
     * @param response  response
     * @param status    响应的状态码
     * @param result    相应的结果字符串
     * @throws IOException IOException
     */
    public static void responseWithJson(HttpServletResponse response, int status,
                                         String result) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(SecurityConstants.CHARSET_UTF8);
        PrintWriter writer = response.getWriter();
        writer.write(result);
        writer.flush();
    }

    /**
     * 去除 User-Agent 中的(\.|\s|\(|\)|\d), 再返回剩余的字符
     *
     * @param userAgent User-Agent
     * @return 去掉 User-Agent 中的(\.|\s|\(|\)|\d), 再返回剩余的字符
     */
    public static String extractUserAgent(String userAgent) {
        return userAgent.replaceAll(EXTRACT_USER_AGENT_REGEX, "");
    }

    /**
     * 根据 LoginProcessType 进行 logout 转发处理
     * @param request   request
     * @param response  response
     * @param clientProperties  clientProperties
     * @param objectMapper  objectMapper
     * @param redirectStrategy  redirectStrategy
     * @param errorCodeEnum errorCodeEnum
     * @throws IOException IOException
     */
    public static void redirectProcessingLogoutByLoginProcessType(HttpServletRequest request,
                                                                HttpServletResponse response,
                                                            ClientProperties clientProperties, ObjectMapper objectMapper,
                                                            RedirectStrategy redirectStrategy, ErrorCodeEnum errorCodeEnum) throws IOException {

        redirectProcessing(request, response, clientProperties, objectMapper, redirectStrategy, errorCodeEnum,
                           clientProperties.getLogoutSuccessUrl());
    }

    /**
     * 根据 LoginProcessType 进行转发处理
     * @param request   request
     * @param response  response
     * @param clientProperties  clientProperties
     * @param objectMapper  objectMapper
     * @param redirectStrategy  redirectStrategy
     * @param errorCodeEnum errorCodeEnum
     * @param redirectUrl   redirectUrl
     * @throws IOException IOException
     */
    public static void redirectProcessingByLoginProcessType(HttpServletRequest request, HttpServletResponse response,
                                                            ClientProperties clientProperties, ObjectMapper objectMapper,
                                                            RedirectStrategy redirectStrategy, ErrorCodeEnum errorCodeEnum,
                                                            String redirectUrl) throws IOException {

        String referer = Objects.requireNonNullElse(request.getHeader(SecurityConstants.HEADER_REFERER), redirectUrl);

        redirectProcessing(request, response, clientProperties, objectMapper, redirectStrategy, errorCodeEnum, referer);
    }


    private static void redirectProcessing(HttpServletRequest request, HttpServletResponse response, ClientProperties clientProperties, ObjectMapper objectMapper, RedirectStrategy redirectStrategy, ErrorCodeEnum errorCodeEnum, String redirectUrl) throws IOException {
        if (LoginProcessType.JSON.equals(clientProperties.getLoginProcessType()))
        {
            int status = HttpStatus.UNAUTHORIZED.value();
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(SecurityConstants.CHARSET_UTF8);

            // 在 ResponseResult 中的 data 为请求头中的 referer
            response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.fail(errorCodeEnum,
                                                                                           redirectUrl)));
            return;
        }

        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}
