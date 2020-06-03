package top.dcenter.security.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import top.dcenter.security.core.enums.ErrorCodeEnum;
import top.dcenter.security.core.enums.LoginProcessType;
import top.dcenter.security.core.exception.RegisterUserFailureException;
import top.dcenter.security.core.exception.ValidateCodeException;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.vo.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.security.core.consts.SecurityConstants.CHARSET_UTF8;
import static top.dcenter.security.core.consts.SecurityConstants.HEADER_REFERER;

/**
 * authentication util
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
     * 认证失败处理
     *
     * @param response          {@link javax.servlet.http.HttpServletRequest}
     * @param exception         {@link HttpServletResponse}
     * @param objectMapper      {@link ObjectMapper}
     * @param browserProperties {@link BrowserProperties}
     * @return 如果通过 {@link HttpServletResponse} 返回 JSON 数据则返回 true, 否则 false
     * @throws IOException
     */
    public static boolean authenticationFailureProcessing(HttpServletResponse response, AuthenticationException exception,
                                                          ObjectMapper objectMapper, BrowserProperties browserProperties) throws IOException {
        // 注册时，用户名重名处理, 返回 Json 格式
        if (exception instanceof RegisterUserFailureException)
        {
            RegisterUserFailureException e = (RegisterUserFailureException) exception;
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF8);
            response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.fail(e.getMessage(),
                                                                                           e.getErrorCodeEnum())));
            return true;
        }

        // 验证码异常时，返回 JSON 格式
        if (exception instanceof ValidateCodeException)
        {
            ValidateCodeException e = (ValidateCodeException) exception;
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF8);
            response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.fail(e.getMessage(),
                                                                                           e.getErrorCodeEnum())));
            return true;
        }

        if (LoginProcessType.JSON.equals(browserProperties.getLoginProcessType()))
        {
            int status = HttpStatus.UNAUTHORIZED.value();
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF8);
            response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.fail(exception.getMessage(),
                                                                                           ErrorCodeEnum.UNAUTHORIZED)));
            return true;
        }

        return false;
    }

    public static void redirectProcessingByLoginProcessType(HttpServletRequest request, HttpServletResponse response,
                                                            BrowserProperties browserProperties, ObjectMapper objectMapper,
                                                            RedirectStrategy redirectStrategy, ErrorCodeEnum errorCodeEnum,
                                                            String redirectUrl) throws IOException {

        String referer = request.getHeader(HEADER_REFERER);
        if (LoginProcessType.JSON.equals(browserProperties.getLoginProcessType()))
        {
            int status = HttpStatus.UNAUTHORIZED.value();
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF8);

            response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.fail(errorCodeEnum,
                                                                                           referer)));
            return;
        }

        redirectStrategy.sendRedirect(request, response, redirectUrl);
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
}
