package top.dcenter.ums.security.common.consts;

/**
 * 权限类常量
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.3.5 22:08
 */
public interface RbacConstants {

    /**
     * 角色权限前缀
     */
    String DEFAULT_ROLE_PREFIX = "ROLE_";
    /**
     * 角色组权限前缀
     */
    String DEFAULT_GROUP_PREFIX = "GROUP_";
    /**
     * 资源权限前缀
     */
    String DEFAULT_SCOPE_PREFIX = "SCOPE_";

    /**
     * 权限分隔符
     */
    String PERMISSION_SEPARATOR = "_";

    /**
     * 数据库 AUTHORITY 分隔符
     */
    String DATABASE_AUTHORITY_DELIMITER = ",";
}
