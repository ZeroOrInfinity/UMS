/**
 * 
 */
package top.dcenter.security.social.view;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.ui.Model;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 查看用户所有的第三方登录数据<br>
 * 继承 {@link ConnectionStatusView} 后且注册到 IOC容器后，会替换此类。注意：beanName 必须是 "connect/status".
 * @see ConnectController#connectionStatus(NativeWebRequest, Model)
 * @author zhailiang
 */
@Slf4j
public class ConnectionStatusView extends AbstractView {
	
	private final ObjectMapper objectMapper;

	public ConnectionStatusView(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map<String, List<Connection<?>>> connections = (Map<String, List<Connection<?>>>) model.get("connectionMap");
		Object providerIds = model.get("providerIds");
		log.info("providerIds = {}", providerIds);
		log.info("connections = {}", connections);
		Map<String, Boolean> result = new HashMap<>();
		Iterator<Map.Entry<String, List<Connection<?>>>> iterator = connections.entrySet().iterator();
		while (iterator.hasNext())
		{
			Map.Entry<String, List<Connection<?>>> next = iterator.next();
			result.put(next.getKey(), CollectionUtils.isNotEmpty(next.getValue()));
		}

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(result));
	}

}
