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
 * 用户
 * @author zyw
 * @version V1.0  Created by 2020-09-26 15:47
 */
@SuppressWarnings("jol")
@Data
@Table(indexes = {@Index(name = "idx_username", columnList = "username", unique = true),
                  @Index(name = "idx_mobile", columnList = "mobile", unique = true)})
@Entity
public class SysUser implements Serializable {

    private static final long serialVersionUID = 5552682183604767L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String nickname;
    private String mobile;
    /**
     * 权限, 多个权限用逗号分隔
     */
    private String authorities;
    private String email;
    private String qq;
    private Date birthday;
    private Integer gender;
    private String avatar;
    /**
     * 超级管理员(2)、管理员(1)、普通用户(0)
     */
    private Integer userType;
    private String company;
    private String blog;
    private String location;
    /**
     * 用户来源, 如:'GITEE','WEIBO','QQ','WEIXIN'
     */
    private Integer source;
    /**
     * 第三方 uuid
     */
    private String uuid;
    /**
     * 隐私
     */
    private Integer privacy;
    /**
     * 通知：是否显示详情
     */
    private Integer notification;
    /**
     * 积分, 金币等类似的值
     */
    private Integer score;
    /**
     * 经验值
     */
    private Integer experience;
    private String regIp;
    private String lastLoginIp;
    private Date lastLoginTime;
    private Integer loginCount;
    private String remark;
    /**
     * 用户状态
     */
    private Integer status;
    @Transient
    private Date createTime;
    @Transient
    private Date updateTime;
}
