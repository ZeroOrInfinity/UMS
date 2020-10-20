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

package top.dcenter.ums.security.social.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;
import top.dcenter.ums.security.core.exception.ParameterErrorException;
import top.dcenter.ums.security.core.util.MvcUtil;
import top.dcenter.ums.security.social.callback.RedirectUrlHelperServiceImpl;
import top.dcenter.ums.security.social.properties.SocialProperties;

import javax.servlet.http.HttpServletRequest;

import static top.dcenter.ums.security.core.consts.RegexConstants.RFC_6819_CHECK_REGEX;
import static top.dcenter.ums.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_CODE;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_IDENTIFIER;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_STATE;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.REDIRECT_URL_PARAMETER_ERROR;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.REDIRECT_URL_PARAMETER_ILLEGAL;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.TAMPER_WITH_REDIRECT_URL_PARAMETER;

/**
 * social 第三方登录控制器
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/12 11:37
 */
@Slf4j
@ResponseBody
public class SocialController implements InitializingBean {


    private final RedirectUrlHelperServiceImpl redirectUrlHelper;
    @Autowired
    private GenericApplicationContext applicationContext;
    @Autowired
    private SocialProperties socialProperties;

    public SocialController(RedirectUrlHelperServiceImpl redirectUrlHelper) {
        this.redirectUrlHelper = redirectUrlHelper;
    }


    /**
     * 统一回调地址路由入口
     * @param request   {@link HttpServletRequest}
     * @return {@link RedirectView}
     */
    @RequestMapping(value = "${ums.social.callbackUrl}")
    public RedirectView authCallbackRouter(HttpServletRequest request) {

        String state = request.getParameter(URL_PARAMETER_STATE);
        String queryString = request.getQueryString();
        String ip = request.getRemoteAddr();
        String sid = request.getSession(true).getId();
        String uri = request.getRequestURI();

        if (StringUtils.isNotBlank(state))
        {
            // 解密 state 获取真实的回调地址
            String redirectUrl = redirectUrlHelper.decodeRedirectUrl(state);

            if (StringUtils.isNotBlank(redirectUrl))
            {
                // RFC 6819 安全检查：https://oauth.net/advisories/2014-1-covert-redirect/
                if (redirectUrl.matches(RFC_6819_CHECK_REGEX))
                {
                    log.error("统一回调地址路由-state被篡改: ip={}, sid={}, uri={}, state={}, queryString={}, redirectUrl={}",
                              ip, sid, uri, state, queryString, redirectUrl);
                    throw new ParameterErrorException(REDIRECT_URL_PARAMETER_ILLEGAL, redirectUrl,
                                                      sid);
                }
                if (StringUtils.isNotBlank(redirectUrl))
                {
                    String code = request.getParameter(URL_PARAMETER_CODE);
                    // 重新组装 url 参数, 不带 ServletContextPath
                    redirectUrl = String.format("%s%s%s%s%s%s%s%s%s",
                                                redirectUrl,
                                                URL_PARAMETER_IDENTIFIER,
                                                URL_PARAMETER_CODE,
                                                KEY_VALUE_SEPARATOR,
                                                code,
                                                URL_PARAMETER_SEPARATOR,
                                                URL_PARAMETER_STATE,
                                                KEY_VALUE_SEPARATOR,
                                                state);

                    log.info("统一回调地址路由: ip={}, sid={}, uri={}, state={}, queryString={}, redirectUrl={}",
                             ip, sid, uri, state, queryString, redirectUrl);
                    // 会自动添加 ServletContextPath
                    return new RedirectView(redirectUrl, true);
                }
                log.error("统一回调地址路由-state被篡改: ip={}, sid={}, uri={}, state={}, queryString={}, redirectUrl={}",
                          ip, sid, uri, state, queryString, redirectUrl);
                throw new ParameterErrorException(REDIRECT_URL_PARAMETER_ERROR, redirectUrl, sid);
            }

        }
        log.warn("统一回调地址路由-state为空: ip={}, sid={}, uri={}, state={}, queryString={}",
                  ip, sid, uri, state, queryString);
        throw new ParameterErrorException(TAMPER_WITH_REDIRECT_URL_PARAMETER, state, sid);

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // 1. 动态注入 requireAuthentication() requestMapping 的映射 uri
        String methodName = "authCallbackRouter";
        MvcUtil.setRequestMappingUri(methodName,
                                     socialProperties.getCallbackUrl(),
                                     this.getClass(),
                                     HttpServletRequest.class);

        // 2. 在 mvc 中做 Uri 映射等动作
        MvcUtil.registerController("socialController", applicationContext, null);
    }
}