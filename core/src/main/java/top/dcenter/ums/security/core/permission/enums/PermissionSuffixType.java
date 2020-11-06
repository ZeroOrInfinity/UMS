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
 * 权限后缀类型
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/17 9:33
 */
public enum PermissionSuffixType {
    /**
     * 查询
     */
    GET {
        @Override
        public String getPermissionSuffix() {
            return ":list";
        }
    },
    /**
     * 添加
     */
    POST {
        @Override
        public String getPermissionSuffix() {
            return ":add";
        }
    },
    /**
     * 更新
     */
    PUT {
        @Override
        public String getPermissionSuffix() {
            return ":edit";
        }
    },
    /**
     * 删除
     */
    DELETE {
        @Override
        public String getPermissionSuffix() {
            return ":del";
        }
    },
    /**
     * HEAD
     */
    HEAD {
        @Override
        public String getPermissionSuffix() {
            return ":head";
        }
    },
    /**
     * PATCH
     */
    PATCH {
        @Override
        public String getPermissionSuffix() {
            return ":patch";
        }
    },
    /**
     * OPTIONS
     */
    OPTIONS {
        @Override
        public String getPermissionSuffix() {
            return ":options";
        }
    },
    /**
     * TRACE
     */
    TRACE {
        @Override
        public String getPermissionSuffix() {
            return ":trace";
        }
    };


    /**
     * 获取权限后缀
     * @return 返回权限后缀
     */
    public abstract String getPermissionSuffix();

    /**
     * 根据 requestMethod 获取权限后缀
     * @param method    requestMethod
     * @return  权限后缀, 如果 method 不匹配, 返回 null
     */
    @Nullable
    public static String getPermissionSuffix(@NonNull HttpMethod method) {
        PermissionSuffixType permissionType = getPermissionType(method);
        if (permissionType == null)
        {
            return null;
        }
        return permissionType.getPermissionSuffix();
    }

    /**
     * 根据 requestMethod 获取权限后缀
     * @param method    requestMethod
     * @return  权限后缀, 如果 method 不匹配, 返回 null
     */
    @Nullable
    public static PermissionSuffixType getPermissionType(@NonNull HttpMethod method) {
        PermissionSuffixType[] types = values();
        for (PermissionSuffixType type : types)
        {
            if (type.name().equalsIgnoreCase(method.name()))
            {
                return type;
            }
        }
        return null;
    }

}