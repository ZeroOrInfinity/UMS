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
package top.dcenter.ums.security.jwt.enums;

/**
 * 自定义的 claim names
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.9 16:08
 */
public enum JwtCustomClaimNames {
    /**
     * 用户 ID
     */
    USER_ID("userId", "用户 ID"),
    /**
     * 用户名
     */
    USERNAME("username", "用户名"),
    /**
     * 租户 ID
     */
    TENANT_ID("tenantId", "租户 ID"),
    /**
     * client id
     */
    CLIENT_ID("clientId", "client id"),
    /**
     * user details
     */
    USER_DETAILS("userDetails", "userDetails"),
    /**
     * 用户权限
     */
    AUTHORITIES("authorities", "用户权限"),
    /**
     * scope 权限, 与 scp 意义一样
     */
    SCOPE("scope", "scope 权限, 与 scp 意义一样"),
    /**
     * scope 权限, 与 scope 意义一样
     */
    SCP("scp", "scope 权限, 与 scope 意义一样");



    /**
     * claim name
     */
    private final String claimName;
    /**
     * claim name 的描述
     */
    private final String description;

    JwtCustomClaimNames(String claimName, String description) {
        this.claimName = claimName;
        this.description = description;
    }

    public String getClaimName() {
        return claimName;
    }

    public String getDescription() {
        return description;
    }

}
