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
    private String type;
    private String url;
    private String permission;
    private Long parentId;
    private Integer sort;
    private Boolean external;
    private Boolean available;
    private String icon;
    @Transient
    private Date createTime;
    @Transient
    private Date updateTime;

}
