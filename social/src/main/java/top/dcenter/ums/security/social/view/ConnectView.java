package top.dcenter.ums.security.social.view;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.AbstractView;
import top.dcenter.ums.security.social.api.banding.ShowConnectViewService;
import top.dcenter.ums.security.social.banding.DefaultShowConnectViewService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 回显绑定与解绑结果状态的基本通用的视图.<br><br>
 *     想更改通用视图的回显内容，实现接口 {@link ShowConnectViewService}, 并且注入 IOC 容器即可，自动会替换
 *     {@link DefaultShowConnectViewService}。
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
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		showConnectViewService.renderMergedOutputModel(model, request, response);
	}

}
