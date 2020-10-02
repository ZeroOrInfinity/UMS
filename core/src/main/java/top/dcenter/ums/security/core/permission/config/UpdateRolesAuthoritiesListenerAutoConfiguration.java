package top.dcenter.ums.security.core.permission.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.core.permission.listener.UpdateRolesAuthoritiesListener;

/**
 * 更新权限监听器
 * @author zyw
 * @version V1.0  Created by 2020/10/2 20:09
 */
@Configuration
@EnableAsync
public class UpdateRolesAuthoritiesListenerAutoConfiguration {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public UpdateRolesAuthoritiesListener updateRolesAuthoritiesListener(AbstractUriAuthorizeService abstractUriAuthorizeService) {
        return new UpdateRolesAuthoritiesListener(abstractUriAuthorizeService);
    }
}
