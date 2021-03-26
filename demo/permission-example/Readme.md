# 权限表
```sql
CREATE TABLE `sys_resources` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `available` bit(1) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
  `external` bit(1) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `permission` varchar(255) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新记录时间',
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sys_resource_parent_id` (`parent_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4

CREATE TABLE `sys_role` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `available` bit(1) DEFAULT NULL,
                            `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
                            `description` varchar(255) DEFAULT NULL,
                            `name` varchar(255) DEFAULT NULL,
                            `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新记录时间',
                            PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4

CREATE TABLE `sys_role_resources` (
                                      `resources_id` bigint(20) NOT NULL,
                                      `role_id` bigint(20) NOT NULL,
                                      `create_time` datetime DEFAULT NULL,
                                      `update_time` datetime DEFAULT NULL,
                                      PRIMARY KEY (`resources_id`,`role_id`),
                                      KEY `idx_role_id` (`role_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4

CREATE TABLE `sys_user` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `authorities` varchar(255) DEFAULT NULL,
                            `avatar` varchar(255) DEFAULT NULL,
                            `birthday` datetime DEFAULT NULL,
                            `blog` varchar(255) DEFAULT NULL,
                            `company` varchar(255) DEFAULT NULL,
                            `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
                            `email` varchar(255) DEFAULT NULL,
                            `experience` int(11) DEFAULT NULL,
                            `gender` int(11) DEFAULT NULL,
                            `last_login_ip` varchar(255) DEFAULT NULL,
                            `last_login_time` datetime DEFAULT NULL,
                            `location` varchar(255) DEFAULT NULL,
                            `login_count` int(11) DEFAULT NULL,
                            `mobile` varchar(255) DEFAULT NULL,
                            `nickname` varchar(255) DEFAULT NULL,
                            `notification` int(11) DEFAULT NULL,
                            `password` varchar(255) DEFAULT NULL,
                            `privacy` int(11) DEFAULT NULL,
                            `qq` varchar(255) DEFAULT NULL,
                            `reg_ip` varchar(255) DEFAULT NULL,
                            `remark` varchar(255) DEFAULT NULL,
                            `score` int(11) DEFAULT NULL,
                            `source` int(11) DEFAULT NULL,
                            `status` tinyint(4) DEFAULT '0' COMMENT '用户状态: 0 为 正常, 1 为删除',
                            `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新记录时间',
                            `user_type` int(11) DEFAULT NULL,
                            `username` varchar(255) DEFAULT NULL,
                            `uuid` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4

CREATE TABLE `sys_user_role` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
                                 `role_id` bigint(20) DEFAULT NULL,
                                 `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新记录时间',
                                 `user_id` bigint(20) DEFAULT NULL,
                                 PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4
```

# 权限更新及权限缓存实时更新时序图

![权限更新及权限缓存实时更新时序图](../../doc/RBAC%20权限更新及权限缓存实时更新流程.png)