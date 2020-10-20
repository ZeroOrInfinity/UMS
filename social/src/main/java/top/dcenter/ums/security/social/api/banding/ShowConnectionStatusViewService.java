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

package top.dcenter.ums.security.social.api.banding;

import top.dcenter.ums.security.social.view.ConnectView;
import top.dcenter.ums.security.social.view.ConnectionStatusView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 自定义 {@link ConnectionStatusView} 的显示
 * @author YongWu zheng
 * @version V1.0
 * Created by 2020/5/26 13:47
 */
public interface ShowConnectionStatusViewService {
    /**
     * implement this method to actually render the connectView.
     * The first step will be preparing the request: In the JSP case, this would mean setting model objects as request attributes. The second step will be the actual rendering of the view, for example including the JSP via a RequestDispatcher.
     * @see ConnectView
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                            HttpServletResponse response) throws Exception;
}