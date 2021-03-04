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

package top.dcenter.ums.security.core.premission.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.dcenter.ums.security.core.premission.enums.ResourcesType;

/**
 * 更新角色权限事件
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/2 19:14
 */
public class UpdateRolesResourcesEvent extends ApplicationEvent {
    private static final long serialVersionUID = 6858134429988117542L;

    @Getter
    private final ResourcesType type;
    @Getter
    private final Integer tenantId;
    @Getter
    private final String role;
    /**
     * Create a new {@code ApplicationEvent}.
     * @param isUpdate  是否更新
     * @param type      资源类型
     * @param tenantId  租户 ID, 当 type 的类型为 ROLE, TENANT, SCOPE, MENU, RESOURCES, PERMISSION 不为 null
     * @param role      角色(带 ROLE_ 前缀), 当 type 的类型为 ROLE, MENU, RESOURCES, PERMISSION 不为 null
     */
    public UpdateRolesResourcesEvent(@NonNull Boolean isUpdate, @NonNull ResourcesType type,
                                     @Nullable Integer tenantId, @Nullable String role) {
        super(isUpdate);
        this.type = type;
        this.tenantId = tenantId;
        this.role = role;
    }
}