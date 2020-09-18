package top.dcenter.ums.security.social.api.banding;

import top.dcenter.ums.security.social.view.ConnectView;
import top.dcenter.ums.security.social.view.ConnectionStatusView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 自定义 {@link ConnectionStatusView} 的显示
 * @author zyw
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
