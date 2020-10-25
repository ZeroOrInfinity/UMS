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

package demo.security.permission;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import top.dcenter.ums.security.common.api.config.HttpSecurityAware;
import top.dcenter.ums.security.common.bean.UriHttpMethodTuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static top.dcenter.ums.security.common.bean.UriHttpMethodTuple.tuple;

/**
 * spring session 相关配置
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/28 14:06
 */
@Configuration
public class UriAuthorizeConfigurerAware implements HttpSecurityAware {

    @Override
    public void postConfigure(HttpSecurity http) {
        // dto nothing
    }

    @Override
    public void configure(WebSecurity web) {
        // TODO

    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // TODO

    }

    @Override
    public void preConfigure(HttpSecurity http) {
        // dto nothing
    }

    @Override
    public Map<String, Map<UriHttpMethodTuple, Set<String>>> getAuthorizeRequestMap() {

         // 也可以在 application.yml 中配置, 不用硬编码
         // security:
         //   client:
         //     # 不需要认证的 uri(可以带 HttpMethod 后缀; 用:隔开), 例如: /user/** 或 /user/**:post, 默认为 空 Set.
         //     permit-urls:
         //       - /**/*.html:GET
         //       - /testSign:GET
         //       - /testSignOfLastSevenDays/**:GET

        final Map<UriHttpMethodTuple, Set<String>> permitAllMap = new HashMap<>(16);
        // 放行要测试 permission 的链接, 以免干扰 permission 测试.
        permitAllMap.put(tuple(GET, "/test/permission/**"), null);
        permitAllMap.put(tuple(GET, "/test/deny/**"), null);
        permitAllMap.put(tuple(GET, "/test/pass/**"), null);

        Map<String, Map<UriHttpMethodTuple, Set<String>>> resultMap = new HashMap<>(1);

        resultMap.put(HttpSecurityAware.PERMIT_ALL, permitAllMap);

        return resultMap;
    }

}