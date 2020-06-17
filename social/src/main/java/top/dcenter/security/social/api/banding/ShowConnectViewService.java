package top.dcenter.security.social.api.banding;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 自定义 {@link top.dcenter.security.social.view.ConnectView} 的显示
 * @author zyw23
 * @version V1.0
 * Created by 2020/5/26 13:47
 */
public interface ShowConnectViewService {
    /**
     * implement this method to actually render the connectView.
     * The first step will be preparing the request: In the JSP case, this would mean setting model objects as request attributes. The second step will be the actual rendering of the view, for example including the JSP via a RequestDispatcher.
     * @see top.dcenter.security.social.view.ConnectView
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                            HttpServletResponse response) throws Exception;
}
