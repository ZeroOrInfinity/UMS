package demo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户角色
 * @author zyw
 * @version V1.0  Created by 2020-09-26 15:49
 */
@Data
@Table
@Entity
public class SysUserRole implements Serializable {
    private static final long serialVersionUID = -3201127172452753600L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long roleId;
    @Transient
    private Date createTime;
    @Transient
    private Date updateTime;
}
