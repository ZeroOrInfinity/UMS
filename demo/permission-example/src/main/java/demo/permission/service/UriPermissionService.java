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

package demo.permission.service;

import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.permission.enums.PermissionSuffixType;

/**
 * uri 权限服务
 * @author YongWu zheng
 * @version V1.0  Created by 2020-09-26 22:41
 */
public interface UriPermissionService {


    /**
     * 给角色添加权限
     * @param role                      角色
     * @param uri                       注意: 此 uri 不包含 servletContextPath .
     * @param permissionSuffixType      权限后缀类型列表
     * @return  是否添加成功
     */
    boolean addUriPermission(@NonNull String role, @NonNull String uri,
                                    @NonNull PermissionSuffixType permissionSuffixType);

    /**
     * 删除角色指定 uri 权限
     * @param role                      角色
     * @param uri                       注意: 此 uri 不包含 servletContextPath .
     * @param permissionSuffixType      权限后缀类型列表
     * @return  是否删除成功
     */
    boolean delUriPermission(String role, String uri, PermissionSuffixType permissionSuffixType);
}