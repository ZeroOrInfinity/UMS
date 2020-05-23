package top.dcenter.security.social.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.view.AbstractView;
import top.dcenter.security.core.enums.LoginType;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.vo.SimpleResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 查看绑定与解绑结果状态的基本的视图
 * @author zhailiang
 */
public class ConnectView extends AbstractView {

	private final BrowserProperties browserProperties;
	private final ObjectMapper objectMapper;

	public ConnectView(BrowserProperties browserProperties, ObjectMapper objectMapper) {
		this.browserProperties = browserProperties;
		this.objectMapper = objectMapper;
	}

	/**
	 * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel
	 * (java.util.Map, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO
		// JSON
		if (LoginType.JSON.equals(browserProperties.getLoginType()))
		{
			response.setStatus(HttpStatus.OK.value());
			response.setContentType("application/json;charset=UTF-8");
			if (model.get("connections") == null) {
				response.getWriter().write(objectMapper.writeValueAsString(SimpleResponse.fail(0, "解绑成功")));
			} else {
				response.getWriter().write(objectMapper.writeValueAsString(SimpleResponse.fail(1, "绑定成功")));
			}
			return;
		}
		// HTML
		response.setContentType("text/html;charset=UTF-8");
		if (model.get("connections") == null) {
			response.getWriter().write("<h3>解绑成功</h3>");
		} else {
			response.getWriter().write("<h3>绑定成功</h3>");
		}

	}

}
