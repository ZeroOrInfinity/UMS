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

package top.dcenter.ums.security.core.api.permission.entity;

import lombok.Data;

/**
 * uri 权限资源实体
 * @author YongWu zheng
 */
@Data
public class UriResourcesDTO {


    public UriResourcesDTO() {
    }

    public UriResourcesDTO(String url, String permission) {
        this.url = url;
        this.permission = permission;
    }

    public UriResourcesDTO(Long roleId, Long resourcesId, Long roleResourcesId, String url, String permission) {
        this.roleId = roleId;
        this.resourcesId = resourcesId;
        this.roleResourcesId = roleResourcesId;
        this.url = url;
        this.permission = permission;
    }

    /**
     * 角色 Id
     */
    private Long roleId;
    /**
     * 资源 Id
     */
    private Long resourcesId;

    /**
     * 角色资源 Id
     */
    private Long roleResourcesId;

    /**
     * uri
     */
    private String url;
    /**
     * 权限, 多个权限用逗号分隔
     */
    private String permission;

}