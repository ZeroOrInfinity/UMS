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
 * 角色
 * @author zyw
 * @version V1.0  Created by 2020-09-26 15:44
 */
@Data
@Table
@Entity
public class SysRole implements Serializable {
    private static final long serialVersionUID = -2247129352211520649L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @Transient
    private Date createTime;
    @Transient
    private Date updateTime;

}
