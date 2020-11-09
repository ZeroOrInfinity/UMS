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

package top.dcenter.ums.security.core.permission.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import top.dcenter.ums.security.core.api.permission.service.UpdateAndCacheAuthoritiesService;
import top.dcenter.ums.security.core.permission.event.UpdateRolesAuthoritiesEvent;

/**
 * uri 权限更新监听器
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/2 19:53
 */
public class UpdateRolesAuthoritiesListener implements ApplicationListener<UpdateRolesAuthoritiesEvent> {

    private final UpdateAndCacheAuthoritiesService updateAndCacheAuthoritiesService;

    public UpdateRolesAuthoritiesListener(UpdateAndCacheAuthoritiesService updateAndCacheAuthoritiesService) {
        this.updateAndCacheAuthoritiesService = updateAndCacheAuthoritiesService;
    }

    @Async
    @Override
    public void onApplicationEvent(UpdateRolesAuthoritiesEvent event) {
        Object source = event.getSource();
        if (source instanceof Boolean && ((Boolean) source))
        {
            switch(event.getType()) {
                case ROLE:
                    this.updateAndCacheAuthoritiesService.updateAuthoritiesOfAllRoles();
                    break;
                case TENANT:
                    this.updateAndCacheAuthoritiesService.updateAuthoritiesOfAllTenant();
                    break;
                case SCOPE:
                    this.updateAndCacheAuthoritiesService.updateAuthoritiesOfAllScopes();
                    break;
                default:
                    break;
            }
        }
    }
}