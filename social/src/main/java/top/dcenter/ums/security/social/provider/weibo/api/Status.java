package top.dcenter.ums.security.social.provider.weibo.api;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author edva8332
 * @version V1.0  Created by 2020-06-18 23:22
 */
public class Status implements Serializable {

	private static final long serialVersionUID = 8193117346766045008L;
	private Long id;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "en_US")
	private Date createdAt;
	private String text;
	private String source;
	private boolean favorited;
	private boolean truncated;
	private String inReplyToStatusId;
	private String inReplyToUserId;
	private String inReplyToScreenName;
	private String mid;
	private WeiboUserInfo user;
	private int repostsCount;
	private int commentsCount;
	private Status originalStatus;

	public Status() {
	}

	public Status(Long id, Date createdAt, String text, String source,
	              boolean favorited, boolean truncated, int repostsCount,
	              int commentsCount) {
		super();
		this.id = id;
		this.createdAt = createdAt;
		this.text = text;
		this.source = source;
		this.favorited = favorited;
		this.truncated = truncated;
		this.repostsCount = repostsCount;
		this.commentsCount = commentsCount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "en_US")
	public Date getCreatedAt() {
		return createdAt;
	}
	@JsonFormat(pattern = "EEE MMM dd HH:mm:ss ZZZ yyyy", locale = "en_US")
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}

	public boolean isTruncated() {
		return truncated;
	}

	public void setTruncated(boolean truncated) {
		this.truncated = truncated;
	}

	public String getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public void setInReplyToStatusId(String inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	public String getInReplyToUserId() {
		return inReplyToUserId;
	}

	public void setInReplyToUserId(String inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public int getRepostsCount() {
		return repostsCount;
	}

	public void setRepostsCount(int repostsCount) {
		this.repostsCount = repostsCount;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public WeiboUserInfo getUser() {
		return user;
	}

	public void setUser(WeiboUserInfo user) {
		this.user = user;
	}

	public Status getOriginalStatus() {
		return originalStatus;
	}

	public void setOriginalStatus(Status repost) {
		this.originalStatus = repost;
	}

}