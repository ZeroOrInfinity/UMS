package top.dcenter.ums.security.social.provider.gitee.api;

import lombok.Data;

/**
 * gitee 用户信息
 * @author zyw
 * @version V1.0  Created by 2020/5/8 20:12
 */
@SuppressWarnings({"ALL"})
@Data
public class GiteeUserInfo {
    /**
     * 用户 ID
     */
    private Integer id;
    /**
     * 登录名称
     */
    private String login;
    /**
     * 用户名称
     */
    private String name;
    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * 职业
     */
    private String bio;
    /**
     * 用户博客
     */
    private String blog;
    /**
     * 创建时间
     */
    private String createdAt;
    /**
     * email
     */
    private String email;
    /**
     *
     */
    private String eventsUrl;
    /**
     * 粉丝
     */
    private String followers;
    /**
     *
     */
    private String followersUrl;
    /**
     * 关注
     */
    private String following;
    /**
     *
     */
    private String followingUrl;
    /**
     *
     */
    private String gistsUrl;
    /**
     * 用户概览
     */
    private String htmlUrl;
    /**
     * 组织机构
     */
    private String organizationsUrl;
    /**
     *
     */
    private String publicGists;
    /**
     * 公共仓库
     */
    private String publicRepos;
    /**
     * 通知事件
     */
    private String receivedEventsUrl;
    /**
     * 仓库 url
     */
    private String reposUrl;
    /**
     * 是否管理员
     */
    private String siteAdmin;
    /**
     * stared 统计
     */
    private String stared;
    /**
     * start 网址
     */
    private String starredUrl;
    /**
     * 订阅网址
     */
    private String subscriptionsUrl;
    /**
     * 用户类型
     */
    private String type;
    /**
     * 用户状态更新时间
     */
    private String updatedAt;
    /**
     * 用户信息的 json 信息
     */
    private String url;
    /**
     * 观察数量统计
     */
    private String watched;
    /**
     * 微博
     */
    private String weibo;

}
