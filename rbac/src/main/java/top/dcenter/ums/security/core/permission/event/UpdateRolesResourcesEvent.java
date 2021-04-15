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

package top.dcenter.ums.security.core.permission.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.dcenter.ums.security.core.permission.dto.UpdateRoleResourcesDto;

/**
 * 更新角色权限事件
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/2 19:14
 */
public class UpdateRolesResourcesEvent extends ApplicationEvent {
    private static final long serialVersionUID = 6858134429988117542L;

    @Getter
    private final UpdateRoleResourcesDto<Object> updateRoleResourcesDto;
    @Getter
    private final ApplicationEvent remoteApplicationEvent;
    /**
     * Create a new {@code ApplicationEvent}.
     * @param source                    source
     * @param updateRoleResourcesDto    更新权限资源 DTO
     * @param remoteApplicationEvent    发送权限更新事件(如: 向 中间件/MQ 发送权限更新消息).
     *                                  微服务应用时, 通常需要发送权限更新事件, 方便使用权限的微服务更新权限;
     *                                  当为单体应用时, 不需要再发送权限更新事件, 可以为 null.
     */
    public UpdateRolesResourcesEvent(@NonNull Object source,
                                     @NonNull UpdateRoleResourcesDto<Object> updateRoleResourcesDto,
                                     @Nullable ApplicationEvent remoteApplicationEvent) {
        super(source);
        this.updateRoleResourcesDto = updateRoleResourcesDto;
        this.remoteApplicationEvent = remoteApplicationEvent;
    }
}