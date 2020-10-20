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

package top.dcenter.ums.security.social.view;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.AbstractView;
import top.dcenter.ums.security.social.api.banding.ShowConnectViewService;
import top.dcenter.ums.security.social.banding.DefaultShowConnectViewServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 回显绑定与解绑结果状态的基本通用的视图.<br><br>
 *     想更改通用视图的回显内容，实现接口 {@link ShowConnectViewService}, 并且注入 IOC 容器即可，自动会替换
 *     {@link DefaultShowConnectViewServiceImpl}。
 * @author zhailiang
 */
public class ConnectView extends AbstractView {

	private final ShowConnectViewService showConnectViewService;

	public ConnectView(ShowConnectViewService showConnectViewService) {
		this.showConnectViewService = showConnectViewService;
		setContentType(MediaType.TEXT_HTML_VALUE);
	}

	/**
	 * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel
	 * (java.util.Map, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void renderMergedOutputModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request,
	                                       @NotNull HttpServletResponse response) throws Exception {

		showConnectViewService.renderMergedOutputModel(model, request, response);
	}

}