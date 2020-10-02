package top.dcenter.ums.security.core.permission.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.core.permission.event.UpdateRolesAuthoritiesEvent;

/**
 * uri 权限更新监听器
 * @author zyw
 * @version V1.0  Created by 2020/10/2 19:53
 */
public class UpdateRolesAuthoritiesListener implements ApplicationListener<UpdateRolesAuthoritiesEvent> {

    private final AbstractUriAuthorizeService abstractUriAuthorizeService;

    public UpdateRolesAuthoritiesListener(AbstractUriAuthorizeService abstractUriAuthorizeService) {
        this.abstractUriAuthorizeService = abstractUriAuthorizeService;
    }

    @Async
    @Override
    public void onApplicationEvent(UpdateRolesAuthoritiesEvent event) {
        Object source = event.getSource();
        if (source instanceof Boolean)
        {
            Boolean isUpdate = ((Boolean) source);
            if (isUpdate)
            {
                abstractUriAuthorizeService.updateRolesAuthorities();
            }
        }
    }
}
