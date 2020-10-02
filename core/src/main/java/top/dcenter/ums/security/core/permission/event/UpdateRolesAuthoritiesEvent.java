package top.dcenter.ums.security.core.permission.event;

import org.springframework.context.ApplicationEvent;

/**
 * 更新角色权限时间
 * @author zyw
 * @version V1.0  Created by 2020/10/2 19:14
 */
public class UpdateRolesAuthoritiesEvent extends ApplicationEvent {
    private static final long serialVersionUID = 6858134429988117542L;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param isUpdate 是否更新
     */
    public UpdateRolesAuthoritiesEvent(Boolean isUpdate) {
        super(isUpdate);
    }
}