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
 * @author zyw
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