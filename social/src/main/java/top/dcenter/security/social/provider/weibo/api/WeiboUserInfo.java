package top.dcenter.security.social.provider.weibo.api;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * A data class representing Basic user information element
 * @author edva8332
 */
@SuppressWarnings("jol")
public class WeiboUserInfo {
    /**
     * 是否允许所有人给我发私信
     */
    private boolean allowAllActMsg;
    /**
     * 是否允许所有人对我的微博进行评论
     */
    private boolean allowAllComment;
    /**
     * 大头像地址
     */
    private String avatarLarge;
    /**
     * 互粉数
     */
    private int biFollowersCount;
    /**
     * 城市编码（参考城市编码表）
     */
    private int city;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "en_US")
    private Date createdAt;
    /**
     * 个人描述
     */
    private String description;
    /**
     * 收藏数
     */
    private int favouritesCount;
    /**
     * 粉丝数
     */
    private int followersCount;
    /**
     * 保留字段,是否已关注(此特性暂不支持)
     */
    private boolean following;
    /**
     * 此用户是否关注我
     */
    private boolean followMe;
    /**
     * 关注数
     */
    private int friendsCount;
    /**
     * 性别,m--男，f--女,n--未知
     */
    private String gender;
    /**
     * 用户UID
     */
    private Long id;
    /**
     * 用户UID
     */
    private String idstr;
    /**
     * 地址
     */
    private String location;
    /**
     * 友好显示名称，如Bill Gates,名称中间的空格正常显示(此特性暂不支持)
     */
    private String name;
    /**
     * 用户在线状态
     */
    private int onlineStatus;
    /**
     * 自定义图像
     */
    private String profileImageUrl;
    /**
     * 省份编码（参考省份编码表）
     */
    private int province;
    /**
     * 微博昵称
     */
    private String screenName;
    /**
     * 用户最新一条微博
     */
    private Status status = null;
    /**
     * 微博数
     */
    private int statusesCount;
    /**
     * 用户博客地址
     */
    private String url;
    /**
     * 用户个性化URL
     */
    private String domain;
    /**
     * 加V标示，是否微博认证用户
     */
    private boolean verified;
    /**
     * 认证原因
     */
    private String verifiedReason;

    public WeiboUserInfo() {
    }

    public WeiboUserInfo(Long id, String screename, String name, String url,
                         String profileImageUrl, String description, String location,
                         Date createAt) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.screenName = screename;
        this.profileImageUrl = profileImageUrl;
        this.description = description;
        this.location = location;
        this.createdAt = createAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        WeiboUserInfo other = (WeiboUserInfo) obj;
        if (id != null)
        {
            return id.equals(other.id);
        }
        else
        {
            return other.id == null;
        }
    }

    public String getProfileUrl() {
        return "http://www.weibo.com/u/" + getId();
    }

    public String getAvatarLarge() {
        return avatarLarge;
    }

    public int getBiFollowersCount() {
        return biFollowersCount;
    }

    public int getCity() {
        return city;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "en_US")
    public Date getCreatedAt() {
        return createdAt;
    }

    public String getDescription() {
        return description;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public String getGender() {
        return gender;
    }

    public Long getId() {
        return id;
    }

    public String getIdstr() {
        return idstr;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public int getProvince() {
        return province;
    }

    public String getScreenName() {
        return screenName;
    }

    public Status getStatus() {
        return status;
    }

    public int getStatusesCount() {
        return statusesCount;
    }

    public String getUrl() {
        return url;
    }

    public String getDomain() {
        return domain;
    }

    public String getVerifiedReason() {
        return verifiedReason;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    public boolean isallowAllActMsg() {
        return allowAllActMsg;
    }

    public boolean isallowAllComment() {
        return allowAllComment;
    }

    public boolean isFollowing() {
        return following;
    }

    public boolean isfollowMe() {
        return followMe;
    }

    public boolean isVerified() {
        return verified;
    }

    /*
     * private void init(JSONObject json) throws WeiboException { if (json !=
     * null) { try { id = json.getString("id"); screenName =
     * json.getString("screen_name"); name = json.getString("name"); province =
     * json.getInt("province"); city = json.getInt("city"); location =
     * json.getString("location"); description = json.getString("description");
     * url = json.getString("url"); profileImageUrl =
     * json.getString("profile_image_url"); domain = json.getString("domain");
     * gender = json.getString("gender"); followersCount =
     * json.getInt("followers_count"); friendsCount =
     * json.getInt("friends_count"); favouritesCount =
     * json.getInt("favourites_count"); statusesCount =
     * json.getInt("statuses_count"); createdAt =
     * parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
     * following = getBoolean("following", json); verified =
     * getBoolean("verified", json); verifiedType = json.getInt("verifiedType");
     * allowAllActMsg = json.getBoolean("allowAllActMsg"); allowAllComment =
     * json.getBoolean("allowAllComment"); followMe =
     * json.getBoolean("followMe"); avatarLarge = json.getString("avatarLarge");
     * onlineStatus = json.getInt("onlineStatus"); biFollowersCount =
     * json.getInt("biFollowersCount"); remark = json.getString("remark"); lang
     * = json.getString("lang"); verifiedReason =
     * json.getString("verified_reason"); if (!json.isNull("status")) { status =
     * new Status(json.getJSONObject("status")); } } catch (JSONException jsone)
     * { throw new WeiboException(jsone.getMessage() + ":" + json.toString(),
     * jsone); } } }
     *
     *
     * public static List<WeiboProfileMixin> constructUser(Response res) throws
     * WeiboException {
     *
     * JSONObject json = res.asJSONObject(); JSONArray list = null; try { if
     * (!json.isNull("users")) { list = json.getJSONArray("users"); } else {
     * list = res.asJSONArray(); } int size = list.length();
     * List<WeiboProfileMixin> users = new ArrayList<WeiboProfileMixin>(size);
     * for (int i = 0; i < size; i++) { users.add(new
     * WeiboProfileMixin(list.getJSONObject(i))); } return users; } catch
     * (JSONException je) { throw new WeiboException(je); }
     *
     * }
     *
     *
     * public static String[] constructIds(Response res) throws WeiboException {
     * try { JSONArray list = res.asJSONObject().getJSONArray("ids"); String
     * temp = list.toString().substring(1, list.toString().length() - 1);
     * String[] ids = temp.split(","); return ids; } catch (JSONException jsone)
     * { throw new WeiboException(jsone); } catch (WeiboException te) { throw
     * te; } }
     *
     * public static List<WeiboProfileMixin> constructUsers(Response res) throws
     * WeiboException { try { JSONArray list = res.asJSONArray(); int size =
     * list.length(); List<WeiboProfileMixin> users = new
     * ArrayList<WeiboProfileMixin>(size); for (int i = 0; i < size; i++) {
     * users.add(new WeiboProfileMixin(list.getJSONObject(i))); } return users;
     * } catch (JSONException jsone) { throw new WeiboException(jsone); } catch
     * (WeiboException te) { throw te; } }
     *
     *
     * public static UserWapper constructWapperUsers(Response res) throws
     * WeiboException { JSONObject jsonUsers = res.asJSONObject(); //
     * asJSONArray(); try { JSONArray user = jsonUsers.getJSONArray("users");
     * int size = user.length(); List<WeiboProfileMixin> users = new
     * ArrayList<WeiboProfileMixin>(size); for (int i = 0; i < size; i++) {
     * users.add(new WeiboProfileMixin(user.getJSONObject(i))); } long
     * previousCursor = jsonUsers.getLong("previous_curosr"); long nextCursor =
     * jsonUsers.getLong("next_cursor"); if (nextCursor == -1) { // 兼容不同标签名称
     * nextCursor = jsonUsers.getLong("nextCursor"); } return new
     * UserWapper(users, previousCursor, nextCursor); } catch (JSONException
     * jsone) { throw new WeiboException(jsone); } }
     *
     *
     * static List<WeiboProfileMixin> constructResult(Response res) throws
     * WeiboException { JSONArray list = res.asJSONArray(); try { int size =
     * list.length(); List<WeiboProfileMixin> users = new
     * ArrayList<WeiboProfileMixin>(size); for (int i = 0; i < size; i++) {
     * users.add(new WeiboProfileMixin(list.getJSONObject(i))); } return users;
     * } catch (JSONException e) { } return null; }
     */

    public void setAllowAllActMsg(boolean allowAllActMsg) {
        this.allowAllActMsg = allowAllActMsg;
    }

    public void setAllowAllComment(boolean allowAllComment) {
        this.allowAllComment = allowAllComment;
    }

    public void setAvatarLarge(String avatarLarge) {
        this.avatarLarge = avatarLarge;
    }

    public void setBiFollowersCount(int biFollowersCount) {
        this.biFollowersCount = biFollowersCount;
    }

    public void setCity(int city) {
        this.city = city;
    }
    @JsonFormat(pattern = "EEE MMM dd HH:mm:ss ZZZ yyyy", locale = "en_US")
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public void setFollowMe(boolean followMe) {
        this.followMe = followMe;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStatusesCount(int statusesCount) {
        this.statusesCount = statusesCount;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserDomain(String userDomain) {
        this.domain = userDomain;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setVerified_reason(String verifiedReason) {
        this.verifiedReason = verifiedReason;
    }

    @Override
    public String toString() {
        return "WeiboProfileMixin [" + "id=" + id + ", screenName="
                + screenName + ", name=" + name + ", province=" + province
                + ", city=" + city + ", location=" + location
                + ", description=" + description + ", url=" + url
                + ", profileImageUrl=" + profileImageUrl + ", domain=" + domain
                + ", gender=" + gender + ", followersCount=" + followersCount
                + ", friendsCount=" + friendsCount + ", statusesCount="
                + statusesCount + ", favouritesCount=" + favouritesCount
                + ", createdAt=" + createdAt + ", following=" + following
                + ", verified=" + verified + ", allowAllActMsg="
                + allowAllActMsg + ", allowAllComment=" + allowAllComment
                + ", followMe=" + followMe + ", avatarLarge=" + avatarLarge
                + ", onlineStatus=" + onlineStatus + ", status=" + status
                + ", biFollowersCount=" + biFollowersCount + ", remark="
                + ", verifiedReason=" + verifiedReason + "]";
    }

}