package top.dcenter.security.social.api.view;

import org.springframework.web.servlet.view.AbstractView;

/**
 * 查看用户所有的第三方登录数据信息，要自定义此功能，
 * 继承此类后且注册到 IOC容器后，会替换 {@link top.dcenter.security.social.view.ConnectionStatusView}。
 * @author zyw
 * @version V1.0  Created by 2020/5/23 23:22
 */
public abstract class BaseConnectionStatusView extends AbstractView {
}
