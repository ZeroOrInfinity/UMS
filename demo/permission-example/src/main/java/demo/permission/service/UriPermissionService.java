package demo.permission.service;

import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.permission.enums.PermissionSuffixType;

import java.util.List;

/**
 * uri 权限服务
 * @author zyw
 * @version V1.0  Created by 2020-09-26 22:41
 */
public interface UriPermissionService {


    /**
     * 给角色添加权限
     * @param role                      角色
     * @param uri                       注意: 此 uri 不包含 servletContextPath .
     * @param permissionSuffixTypeList  权限后缀类型列表
     * @return  是否添加成功
     */
    boolean addUriPermission(@NonNull String role, @NonNull String uri,
                                    @NonNull List<PermissionSuffixType> permissionSuffixTypeList);

    /**
     * 删除角色指定 uri 权限
     * @param role                      角色
     * @param uri                       注意: 此 uri 不包含 servletContextPath .
     * @param permissionSuffixTypeList  权限后缀类型列表
     * @return  是否删除成功
     */
    boolean delUriPermission(String role, String uri, List<PermissionSuffixType> permissionSuffixTypeList);
}