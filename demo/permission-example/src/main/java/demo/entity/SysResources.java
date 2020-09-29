package demo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * 资源
 * @author zyw
 * @version V1.0  Created by 2020-09-26 15:42
 */
@Data
@Table(indexes = {@Index(name = "idx_sys_resource_parent_id", columnList = "parentId")})
@Entity
public class SysResources implements Serializable {

    private static final long serialVersionUID = -4321862331623956112L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    /**
     * 如: 菜单, 按钮
     */
    private String type;
    /**
     * url
     */
    private String url;
    /**
     * 权限
     */
    private String permission;
    /**
     * 父菜单
     */
    private Long parentId;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 是否在新标签打开
     */
    private Boolean external;
    private Boolean available;
    /**
     * 菜单图标
     */
    private String icon;
    @Transient
    private Date createTime;
    @Transient
    private Date updateTime;

}
