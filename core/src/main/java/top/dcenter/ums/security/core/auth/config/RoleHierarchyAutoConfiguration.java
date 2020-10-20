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

package top.dcenter.ums.security.core.auth.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;

import java.util.List;

/**
 * 用户角色层级配置
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/11 15:30
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
public class RoleHierarchyAutoConfiguration {

    private final ClientProperties clientProperties;

    public RoleHierarchyAutoConfiguration(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        // 用户配置角色层级列表
        List<String> roleHierarchyList = clientProperties.getRoleHierarchy();
        // 转换用户配置角色层级列表为 RoleHierarchyImpl 支持的字符串格式
        String roleHierarchyStringRepresentation = String.join("\n", roleHierarchyList);
        // 设置角色层级字符串
        roleHierarchy.setHierarchy(roleHierarchyStringRepresentation);
        return roleHierarchy;
    }

    @Bean
    public RoleHierarchyVoter roleHierarchyVoter(RoleHierarchy roleHierarchy) {
        // 设置角色层级
        return new RoleHierarchyVoter(roleHierarchy);
    }
}