/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package demo.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 * @author YongWu zheng
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
    /**
     * 性别: 1为男, 0为女, 其他未知
     */
    private Integer gender;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 用户类型: 超级管理员(2)、管理员(1)、普通用户(0)
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
     * 用户状态: 0 为 正常, 1 为删除
     */
    @Column(name = "status", columnDefinition = "tinyint(4) DEFAULT '0' COMMENT '用户状态: 0 为 正常, 1 为删除'")
    private Integer status;
    @CreationTimestamp
    private Date createTime;
    @UpdateTimestamp
    private Date updateTime;
}