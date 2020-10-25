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

package top.dcenter.ums.security.core.auth.validate.codes;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeProcessor;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.exception.AbstractResponseJsonAuthenticationException;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.util.ConvertUtil;
import top.dcenter.ums.security.core.util.MvcUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static top.dcenter.ums.security.common.consts.SecurityConstants.GET_METHOD;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.ILLEGAL_VALIDATE_CODE_TYPE;


/**
 * 验证码过滤器
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/4 9:29
 */
@Slf4j
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {

    private final ValidateCodeProperties validateCodeProperties;
    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;

    private final AntPathMatcher pathMatcher;

    /**
     * 验证码处理器 Holder
     */
    private final ValidateCodeProcessorHolder validateCodeProcessorHolder;

    /**
     *  验证码认证 map&#60;uri, validateCodeType&#62;
     */
    @Getter
    private final Map<String, ValidateCodeType> authUrlMap = new HashMap<>();

    public ValidateCodeFilter(ValidateCodeProcessorHolder validateCodeProcessorHolder,
                              BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                              ValidateCodeProperties validateCodeProperties) {
        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        pathMatcher = new AntPathMatcher();
        this.validateCodeProcessorHolder = validateCodeProcessorHolder;
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        // 添加图片验证码 urls
        ConvertUtil.list2Map(validateCodeProperties.getImage().getAuthUrls(), ValidateCodeType.IMAGE, authUrlMap);
        // 添加滑块验证码 urls
        ConvertUtil.list2Map(validateCodeProperties.getSlider().getAuthUrls(), ValidateCodeType.SLIDER, authUrlMap);
        authUrlMap.put(validateCodeProperties.getSlider().getSliderCheckUrl(), ValidateCodeType.SLIDER);
        // 添加轨迹验证码 urls
        ConvertUtil.list2Map(validateCodeProperties.getTrack().getAuthUrls(), ValidateCodeType.TRACK, authUrlMap);
        // 添加选择类验证码 urls
        ConvertUtil.list2Map(validateCodeProperties.getSelection().getAuthUrls(), ValidateCodeType.SELECTION, authUrlMap);
        // 添加自定义验证码 urls
        ConvertUtil.list2Map(validateCodeProperties.getCustomize().getAuthUrls(), ValidateCodeType.CUSTOMIZE, authUrlMap);
        // 添加短信验证码 urls
        ConvertUtil.list2Map(validateCodeProperties.getSms().getAuthUrls(), ValidateCodeType.SMS, authUrlMap);

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String requestUri = request.getRequestURI();
        // 验证码逻辑，当短信验证码与图片验证码 url 相同时，优先使用短信验证码逻辑。
        ValidateCodeType validateCodeType = getValidateCodeType(request);

        try {
            if (validateCodeType != null)
            {
                String typeName = validateCodeType.name();
                ValidateCodeProcessor validateCodeProcessor = validateCodeProcessorHolder.findValidateCodeProcessor(validateCodeType);
                if (validateCodeProcessor != null)
                {
                    validateCodeProcessor.validate(new ServletWebRequest(request, response));
                } else
                {
                    String ip = request.getRemoteAddr();
                    log.warn("违法的验证码类型: error={}, ip={}, sid={}, type={}",
                                 ILLEGAL_VALIDATE_CODE_TYPE.getMsg(),
                             ip,
                             request.getSession(true).getId(),
                             typeName);
                    throw new ValidateCodeException(ILLEGAL_VALIDATE_CODE_TYPE, ip,
                                                    typeName);
                }
            }

        } catch (Exception e) {
            String ip = request.getRemoteAddr();
            log.warn("验证码错误: error={}, ip={}, sid={}, uri={}",
                     e.getMessage(),
                     ip,
                     request.getSession(true).getId(),
                     requestUri);

            AbstractResponseJsonAuthenticationException ex;
            if (e instanceof AbstractResponseJsonAuthenticationException)
            {
                ex = (AbstractResponseJsonAuthenticationException) e;
            }
            else
            {
                ex = new ValidateCodeException(ErrorCodeEnum.VALIDATE_CODE_ERROR, e, ip, validateCodeType.name());
            }
            baseAuthenticationFailureHandler.onAuthenticationFailure(request, response, ex);
            return;
        }

        doFilter(request, response, filterChain);
    }

    /**
     * 获取验证码的类型，如果当前请求不需要校验，则返回null
     *
     * @param request   HttpServletRequest
     * @return  ValidateCodeType
     */
    private ValidateCodeType getValidateCodeType(HttpServletRequest request) {
        ValidateCodeType result;
        String method = request.getMethod();
        if (!GET_METHOD.equalsIgnoreCase(method)) {
            // 去除 ServletContextPath 的 uri
            String requestUri = MvcUtil.getUrlPathHelper().getPathWithinApplication(request);
            result = authUrlMap.getOrDefault(requestUri, null);
            if (result != null)
            {
                return result;
            }

            for (Map.Entry<String, ValidateCodeType> entry : authUrlMap.entrySet())
            {
                if (pathMatcher.match(entry.getKey(), requestUri))
                {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

}