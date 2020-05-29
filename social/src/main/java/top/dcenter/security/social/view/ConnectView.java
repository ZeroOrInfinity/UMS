package top.dcenter.security.social.view;

import org.springframework.web.servlet.view.AbstractView;
import top.dcenter.security.social.api.callback.ShowConnectViewService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 回显绑定与解绑结果状态的基本通用的视图, 要替换指定第三方的视图时，实现 AbstractView 子类且在注册 IOC 容器时 beanName 必须是 {providerId}ConnectedView。<br>
 *     另外如果只是想更改通用视图的回显内容，实现接口 {@link ShowConnectViewService}, 并且注入 IOC 容器即可，自动会替换
 *     {@link top.dcenter.security.social.banding.DefaultShowConnectViewService}。
 * @author zhailiang
 */
public class ConnectView extends AbstractView {

	private final ShowConnectViewService showConnectViewService;

	public ConnectView(ShowConnectViewService showConnectViewService) {
		this.showConnectViewService = showConnectViewService;

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
