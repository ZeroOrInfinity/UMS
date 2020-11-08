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

package top.dcenter.ums.security.core.permission.enums;

import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 权限类型, 包含接口:
 * 1. 获取对应的权限字符串.
 * 2. 根据 HttpMethod 的 method 字符串获取权限类型.
 * 3. 根据 HttpMethod 的 method 字符串获取权限字符串.
 * 4. 获取对应的权限描述.
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/17 9:33
 */
public enum PermissionType {
    /**
     * 查询
     */
    GET("list", "查询权限"),
    /**
     * 添加
     */
    POST("add", "添加权限"),
    /**
     * 更新
     */
    PUT("edit", "更新权限"),
    /**
     * 删除
     */
    DELETE("delete", "删除权限"),
    /**
     * HEAD
     */
    HEAD("head", "HEAD权限"),
    /**
     * PATCH
     */
    PATCH("patch", "PATCH权限"),
    /**
     * OPTIONS
     */
    OPTIONS("options", "OPTIONS权限"),
    /**
     * TRACE
     */
    TRACE("trace", "TRACE权限");

    /**
     * 权限描述
     */
    private final String description;
    /**
     * 权限字符串
     */
    private final String permission;

    PermissionType(String permission, String description) {
        this.permission = permission;
        this.description = description;
    }

    /**
     * 获取权限描述
     * @return 返回权限描述
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 获取权限字符串
     * @return 返回权限字符串
     */
    public String getPermission() {
        return this.permission;
    }

    /**
     * 根据 requestMethod 获取权限字符串
     * @param method    requestMethod
     * @return  返回 requestMethod 相对应的权限字符串, 如果 method 不匹配, 返回 null
     */
    @Nullable
    public static String getPermission(@NonNull HttpMethod method) {
        PermissionType permissionType = getPermissionType(method);
        if (permissionType == null)
        {
            return null;
        }
        return permissionType.getPermission();
    }

    /**
     * 根据 requestMethod 获取权限类型
     * @param method    requestMethod
     * @return  返回 requestMethod 相对应的权限类型, 如果 method 不匹配, 返回 null
     */
    @Nullable
    public static PermissionType getPermissionType(@NonNull HttpMethod method) {
        PermissionType[] types = values();
        for (PermissionType type : types)
        {
            if (type.name().equalsIgnoreCase(method.name()))
            {
                return type;
            }
        }
        return null;
    }

}