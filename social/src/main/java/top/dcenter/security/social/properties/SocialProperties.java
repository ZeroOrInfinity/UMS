package top.dcenter.security.social.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import top.dcenter.security.social.api.repository.UsersConnectionRepositoryFactory;
import top.dcenter.security.social.controller.SocialController;

import javax.servlet.http.HttpServletRequest;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_SIGN_UP_PROCESSING_URL_SOCIAL;

/**
 * social 第三方登录属性
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 19:52
 * @medifiedBy zyw
 */
@SuppressWarnings("jol")
@Getter
@Setter
@ConfigurationProperties("security.social")
public class SocialProperties {

    @NestedConfigurationProperty
    private final QqProperties qq = new QqProperties();
    @NestedConfigurationProperty
    private final GiteeProperties gitee = new GiteeProperties();
    @NestedConfigurationProperty
    private final WeixinProperties weixin = new WeixinProperties();

    // ================= 第三方登录注册相关功能 =================

    /**
     * social 第三方登录注册功能是否开启，默认为 false
     */
    private Boolean socialSignInIsOpen = false;
    /**
     * 第三方登录用户从 signUpUrl 提交的用户信息表单，默认由 /authentication/social 进行处理，由 Social 处理，不需要用户实现
     */
    private String socialUserRegistUrl = DEFAULT_SIGN_UP_PROCESSING_URL_SOCIAL;
    /**
     * 第三方登录用户授权成功且未注册，则跳转的注册页面， 默认为 /signUp.html，
     * autoSignIn=true 且实现 ConnectionSignUp 接口则自动登录时 signUpUrl 会失效
     */
    private String signUpUrl = "/signUp.html";

    /**
     * 第三方登录页面， 默认为 /signIn.html
     */
    private String signInUrl = "/signIn.html";
    /**
     * 第三方登录用户授权成功且未注册，跳转的注册页面时，需要获取的 SocialUserInfo 信息， 默认从 /social/user/info 获取.<br>
     * 注意：此 url 是 permitAll 权限. 修改此 uri 时，同时要修改 signUpUrl 的 ajax 请求 url
     */
    private String socialUserInfo = "/social/user/info";

    /**
     * QQ 登录时是否自动注册：如果为 true 且实现 ConnectionSignUp 接口则自动登录，而且 signUpUrl 失效，否则不自动登录, 默认为 true
     */
    private Boolean autoSignIn = true;


    /**
     * 第三方登录用户注册时: 用户唯一 ID 字段名称， 默认为 userId
     */
    private String userIdParamName = "userId";
    /**
     * 第三方登录用户注册时: 密码 字段名称， 默认为 password
     */
    private String passwordParamName = "password";

    /**
     * 第三方录用户注册时: 服务商 providerId 字段名称， 默认为 providerId
     */
    private String providerIdParamName = "providerId";
    /**
     * 第三方登录用户注册时: 在服务商那的用户唯一ID providerUserId 字段名称， 默认为 providerUserId
     */
    private String providerUserIdParamName = "providerUserId";
    /**
     * 第三方登录用户注册时: 用户头像 avatarUrl 字段名称， 默认为 providerUserId
     */
    private String avatarUrlParamName = "avatarUrl";


    // ================= 第三方登录绑定相关功能 =================
    /**
     * 第三方登录绑定页面， 默认为 /banding.html
     */
    private String bandingUrl = "/banding.html";
    /**
     * 用户绑定第三方账号的 List 的参数名称
     */
    private String bandingProviderConnectionListName = "connections";


    // ================= 第三方登录相关功能 =================
    /**
     * 第三方登录回调处理 url ，也是 RedirectUrl 的前缀，也是统一的回调地址入口，默认为 /auth/callback.<br>
     *    如果更改此 url，更改后的必须要实现 {@link SocialController#authCallbackRouter(HttpServletRequest)} 的功能
     */
    private String callbackUrl = "/auth/callback";

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackUrl() {
        return this.callbackUrl;
    }
    /**
     * 第三方登录回调的域名, 例如：https://localhost 默认为 "http://127.0.0.1"，
     * redirectUrl 直接由 domain/callbackUrl/(security.social.providerId 中的 providerId 组成：如 qq、wechat)组成
     */
    private String domain = "http://127.0.0.1";

    /**
     * 第三方登录用户授权失败跳转页面， 默认为 "/signIn.html"
     */
    private String failureUrl = "/signIn.html";



    // ================= 第三方登录 key 与 secret 加密相关 =================
    /**
     * 第三方登录用户数据库表的字段 key 与 secret 加密专用密码
     */
    private String textEncryptorPassword = "7ca5d913a17b4942942d16a974e3fecc";
    /**
     * 第三方登录用户数据库表的字段 key 与 secret 加密专用 salt
     */
    private String textEncryptorSalt = "cd538b1b077542aca5f86942b6507fe2";


    /* ==========================自定义第三方登录用户表及相关 CURD 语句============================= */

    /**
     * 第三方登录用户数据库表名称，<br>
     */
    private String tableName = "social_UserConnection";

    /**
     * 第三方登录用户数据库用户表用户唯一 ID 字段名称， 默认为 userId
     */
    private String userIdColumnName = "userId";
    /**
     * 第三方登录用户数据库用户表服务商 providerId 字段名称， 默认为 providerId
     */
    private String providerIdColumnName = "providerId";
    /**
     * 第三方登录用户数据库用户表服务商用户 providerUserId 字段名称， 默认为 providerUserId
     */
    private String providerUserIdColumnName = "providerUserId";
    /**
     * 第三方登录用户数据库用户表 rank 字段名称， 默认为 `rank`。<br>
     *     注意：因为 MySQL 8.0 版本 rank 是个关键字。一定要用 ` 包裹。
     */
    private String rankColumnName = "`rank`";

    /**
     * 第三方登录用户数据库用户表用户显示名称 displayName 字段名称， 默认为 displayName
     */
    private String displayNameColumnName = "displayName";
    /**
     * 第三方登录用户数据库用户表用户主页 profileUrl 字段名称， 默认为 profileUrl
     */
    private String profileUrlColumnName = "profileUrl";
    /**
     * 第三方登录用户数据库用户表用户头像 imageUrl 字段名称， 默认为 imageUrl
     */
    private String imageUrlColumnName = "imageUrl";
    /**
     * 第三方登录用户数据库用户表用户 accessToken 字段名称， 默认为 accessToken
     */
    private String accessTokenColumnName = "accessToken";
    /**
     * 第三方登录用户数据库用户表用户 secret 字段名称， 默认为 secret
     */
    private String secretColumnName = "secret";
    /**
     * 第三方登录用户数据库用户表用户显 refreshToken 字段名称， 默认为 refreshToken
     */
    private String refreshTokenColumnName = "refreshToken";
    /**
     * 第三方登录用户数据库用户表用户过期时间 expireTime 字段名称， 默认为 expireTime
     */
    private String expireTimeColumnName = "expireTime";

    /**
     * 第三方登录用户数据库用户表创建语句。 <br>
     * 修改第三方登录用户数据库用户表创建语句时，要注意：修改字段名称可以直接修改上面的字段名称即可，不用修改建表语句，不可以减少字段，但可以另外增加字段。<br>
     * 用户需要对第三方登录的用户表与 curd 的 sql 语句结构进行更改时, 必须实现对应的 {@link UsersConnectionRepositoryFactory}，
     * 如果需要，请实现 {@link UsersConnectionRepositoryFactory}，可以参考 {@link OAuth2UsersConnectionRepositoryFactory}。<br>
     * 注意： sql 语句中的 %s 必须写上，且 %s 的顺序必须与后面的字段名称所对应的含义对应 :<br>
     * tableName、<br>
     * userIdColumnName、<br>
     * providerIdColumnName、<br>
     * providerUserIdColumnName、<br>
     * rankColumnName、<br>
     * displayNameColumnName、<br>
     * profileUrlColumnName、<br>
     * imageUrlColumnName、<br>
     * accessTokenColumnName、<br>
     * secretColumnName、<br>
     * refreshTokenColumnName、<br>
     * expireTimeColumnName、<br>
     * userIdColumnName、<br>
     * providerIdColumnName、<br>
     * providerUserIdColumnName、<br>
     * userIdColumnName、<br>
     * providerIdColumnName、<br>
     * rankColumnName
     */
    @SuppressWarnings("JavadocReference")
    private String creatUserConnectionTableSql = "create table %s (%s " +
            "varchar(255) not null,\n" +
            "\t%s varchar(255) not null,\n" +
            "\t%s varchar(255),\n" +
            "\t%s int not null,\n" +
            "\t%s varchar(255),\n" +
            "\t%s varchar(512),\n" +
            "\t%s varchar(512),\n" +
            "\t%s varchar(512) not null,\n" +
            "\t%s varchar(512),\n" +
            "\t%s varchar(512),\n" +
            "\t%s bigint,\n" +
            "\tprimary key (%s, %s, %s),\n" +
            "\tunique index UserConnectionRank(%s, %s, %s));";

    /**
     * 第三方登录用户数据库用户表查询 userIds 的查询语句。 <br>
     * 注意： sql 语句中的 %s 必须写上，问号必须与指定的 %s 相对应,%s按顺序会用对应的 :<br>
     * databaseName、<br>
     * tableName
     */
    private String queryUserConnectionTableExistSql = "SELECT COUNT(1) FROM information_schema.tables WHERE " +
            "table_schema='%s' AND table_name = '%s'";


    /**
     * 第三方登录用户数据库用户表查询 userIds 的查询语句。 <br>
     * 注意： sql 语句中的 %s 必须写上，问号必须与指定的 %s 相对应,%s按顺序会用对应的 :<br>
     * userIdColumnName、<br>
     * tableName、<br>
     * providerIdColumnName、<br>
     * providerUserIdColumnName
     */
    private String findUserIdsWithConnectionSql = "select %s from %s where %s = ? and %s = ?";

    /**
     * 通过第三方服务提供商提供的 providerId 与 providerUserIds 从数据库用户表查询 userIds 的查询语句。 <br>
     * 注意： sql 语句中的 %s 必须写上，问号必须与指定的 %s 相对应,%s按顺序会用对应的 :<br>
     * userIdColumnName、<br>
     * tableName、<br>
     * providerIdColumnName、<br>
     * providerUserIdColumnName
     */
    private String findUserIdsConnectedToSql = "select %s from %S where %s = :%s and %s in (:%s)";

    /**
     * 通过第三方服务提供商提供的 providerId 与 providerUserIds 从数据库用户表查询 userIds 的查询语句。 <br>
     * 注意： sql 语句中的 %s 必须写上，问号必须与指定的 %s 相对应,%s按顺序会用对应的 :<br>
     * userIdColumnName、<br>
     * providerIdColumnName、<br>
     * providerUserIdColumnName、<br>
     * displayNameColumnName、<br>
     * profileUrlColumnName、<br>
     * imageUrlColumnName、<br>
     * accessTokenColumnName、<br>
     * secretColumnName、<br>
     * refreshTokenColumnName、<br>
     * expireTimeColumnName、<br>
     * tableName
     */
    private String selectFromUserConnectionSql = "select %s, %s, %s, %s, %s, %s, %s, %s, %s, %s from %s";

    /**
     * 第三方登录用户数据库用户表更新语句。 <br>
     * 注意： sql 语句中的 %s 必须写上，问号必须与指定的 %s 相对应,%s按顺序会用对应的 :<br>
     * tableName、<br>
     * displayNameColumnName、<br>
     * profileUrlColumnName、<br>
     * imageUrlColumnName、<br>
     * accessTokenColumnName、<br>
     * secretColumnName、<br>
     * refreshTokenColumnName、<br>
     * expireTimeColumnName、<br>
     * userIdColumnName、<br>
     * providerIdColumnName、<br>
     * providerUserIdColumnName
     */
    private String updateConnectionSql = "update %s " +
            "set %s = ?, " +
            "%s = ?, " +
            "%s = ?, " +
            "%s = ?, " +
            "%s = ?, " +
            "%s = ?, " +
            "%s = ? " +
            "where %s = ? and " +
            "%s = ? and " +
            "%s = ?";

    /**
     * 第三方登录用户数据库用户表添加用户语句。 <br>
     * 注意： sql 语句中的 %s 必须写上，问号必须与指定的 %s 相对应,%s按顺序会用对应的 :<br>
     * tableName、<br>
     * userIdColumnName、<br>
     * providerIdColumnName、<br>
     * providerUserIdColumnName、<br>
     * rankColumnName、<br>
     * displayNameColumnName、<br>
     * profileUrlColumnName、<br>
     * imageUrlColumnName、<br>
     * accessTokenColumnName、<br>
     * secretColumnName、<br>
     * refreshTokenColumnName、<br>
     * expireTimeColumnName
     */
    private String addConnectionSql = "insert into %s(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * 第三方登录用户数据库用户表查询添加用户时的所需 rank 的值。 <br>
     * 注意： sql 语句中的 %s 必须写上，问号必须与指定的 %s 相对应,%s按顺序会用对应的 :<br>
     * rankColumnName、<br>
     * rankColumnName、<br>
     * tableName、<br>
     * userIdColumnName、<br>
     * providerIdColumnName
     */
    private String addConnectionQueryForRankSql = "select coalesce(max(%s) + 1, 1) as %s from %s where %s = ? and %s = ?";

    /**
     * 第三方登录用户数据库用户表根据 userId 与 providerId 删除多个用户。 <br>
     * 注意： sql 语句中的 %s 必须写上，问号必须与指定的 %s 相对应,%s按顺序会用对应的 :<br>
     * tableName、<br>
     * userIdColumnName、<br>
     * providerIdColumnName
     */
    private String removeConnectionsSql = "delete from %s where %s = ? and %s = ?";
    /**
     * 第三方登录用户数据库用户表根据 userId、providerId、providerUserId 删除一个用户。 <br>
     * 注意： sql 语句中的 %s 必须写上，问号必须与指定的 %s 相对应,%s按顺序会用对应的 :<br>
     * tableName、<br>
     * userIdColumnName、<br>
     * providerIdColumnName、<br>
     * providerUserIdColumnName
     */
    private String removeConnectionSql = "delete from %s where %s = ? and %s = ? and %s = ?";



    public String getRemoveConnectionSql() {

        return String.format(removeConnectionSql,
                             tableName,
                             userIdColumnName,
                             providerIdColumnName,
                             providerUserIdColumnName);
    }

    public String getRemoveConnectionsSql() {

        return String.format(removeConnectionsSql,
                             tableName,
                             userIdColumnName,
                             providerIdColumnName);
    }

    public String getAddConnectionQueryForRankSql() {

        return String.format(addConnectionQueryForRankSql,
                             rankColumnName,
                             rankColumnName,
                             tableName,
                             userIdColumnName,
                             providerIdColumnName);
    }

    public String getAddConnectionSql() {

        return String.format(addConnectionSql,
                             tableName,
                             userIdColumnName,
                             providerIdColumnName,
                             providerUserIdColumnName,
                             rankColumnName,
                             displayNameColumnName,
                             profileUrlColumnName,
                             imageUrlColumnName,
                             accessTokenColumnName,
                             secretColumnName,
                             refreshTokenColumnName,
                             expireTimeColumnName);
    }

    public String getUpdateConnectionSql() {

        return String.format(updateConnectionSql,
                             tableName,
                             displayNameColumnName,
                             profileUrlColumnName,
                             imageUrlColumnName,
                             accessTokenColumnName,
                             secretColumnName,
                             refreshTokenColumnName,
                             expireTimeColumnName,
                             userIdColumnName,
                             providerIdColumnName,
                             providerUserIdColumnName);
    }

    public String getSelectFromUserConnectionSql() {
        return String.format(selectFromUserConnectionSql,
                             userIdColumnName,
                             providerIdColumnName,
                             providerUserIdColumnName,
                             displayNameColumnName,
                             profileUrlColumnName,
                             imageUrlColumnName,
                             accessTokenColumnName,
                             secretColumnName,
                             refreshTokenColumnName,
                             expireTimeColumnName,
                             tableName);
    }

    public String getCreatUserConnectionTableSql() {
        return String.format(creatUserConnectionTableSql,
                             tableName,
                             userIdColumnName,
                             providerIdColumnName,
                             providerUserIdColumnName,
                             rankColumnName,
                             displayNameColumnName,
                             profileUrlColumnName,
                             imageUrlColumnName,
                             accessTokenColumnName,
                             secretColumnName,
                             refreshTokenColumnName,
                             expireTimeColumnName,
                             userIdColumnName,
                             providerIdColumnName,
                             providerUserIdColumnName,
                             userIdColumnName,
                             providerIdColumnName,
                             rankColumnName);
    }

    public String getQueryUserConnectionTableExistSql(String databaseName) {

        return String.format(queryUserConnectionTableExistSql, databaseName, tableName);
    }

    public String getFindUserIdsWithConnectionSql() {

        return String.format(findUserIdsWithConnectionSql,
                             userIdColumnName,
                             tableName,
                             providerIdColumnName,
                             providerUserIdColumnName);
    }

    public String getFindUserIdsConnectedToSql() {

        return String.format(findUserIdsConnectedToSql,
                             userIdColumnName,
                             tableName,
                             providerIdColumnName,
                             providerIdColumnName,
                             providerUserIdColumnName,
                             providerUserIdColumnName);
    }

    @Getter
    @Setter
    public class QqProperties extends SocialBaseProperties {
        /**
         * 服务提供商标识, 默认为 qq
         */
        private String providerId = "qq";
        /**
         * 回调地址(格式必须是：domain/callbackUrl/providerId)，默认
         */
        private String redirectUrl = domain + callbackUrl + "/" + providerId;
    }

    @Getter
    @Setter
    public class GiteeProperties extends SocialBaseProperties {
        /**
         * 服务提供商标识, 默认为 gitee
         */
        private String providerId = "gitee";
        /**
         * 回调地址(格式必须是：domain/callbackUrl/providerId)，默认
         */
        private String redirectUrl = domain + callbackUrl + "/" + providerId;

    }

    /**
     * @author zhailiang
     */
    @Getter
    @Setter
    public class WeixinProperties extends SocialBaseProperties {

        /**
         * 第三方id，用来决定发起第三方登录的url，默认是 weixin。
         */
        private String providerId = "weixin";
        /**
         * 回调地址(格式必须是：domain/callbackUrl/providerId)，默认
         */
        private String redirectUrl = domain + callbackUrl + "/" + providerId;

    }


}
