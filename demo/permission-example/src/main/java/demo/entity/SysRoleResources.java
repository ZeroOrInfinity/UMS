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
 * 角色资源
 * @author zyw
 * @version V1.0  Created by 2020-09-26 15:46
 */
@Data
@Table(indexes = {@Index(name = "idx_roleId_resourcesId", columnList = "roleId,resourcesId")})
@Entity
public class SysRoleResources implements Serializable {
    private static final long serialVersionUID = -4426152457773441387L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roleId;
    private Long resourcesId;
    @Transient
    private Date createTime;
    @Transient
    private Date updateTime;
}
