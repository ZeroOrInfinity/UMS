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

package top.dcenter.ums.security.social.api.callback;

import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * 解析 state，返回真实的回调地址，支持通过统一的回调地址路由到多个回调地址的解析助手。<br><br>
 *     注意：如果修改回调地址解密逻辑，同时要修改 {@link BaseOAuth2ConnectionFactory#buildReturnToUrl(HttpServletRequest, Set)} 与
 *     {@link BaseOAuth2ConnectionFactory#generateState(String)}的加密逻辑。<br><br>
 *     自定义此逻辑: 实现并注入 IOC 容器即可替换
 *
 * @author YongWu zheng
 * @version V1.0
 * Created by 2020/9/15 21:46
 */
public interface RedirectUrlHelperService {

    /**
     * 解析 state，返回真实的回调地址
     * @param state redirectUrl 中的 state 参数的值
     * @return 返回真实回调地址, 如果传入 state 为格式不正确，返回 null
     */
    String decodeRedirectUrl(@NonNull String state);

}