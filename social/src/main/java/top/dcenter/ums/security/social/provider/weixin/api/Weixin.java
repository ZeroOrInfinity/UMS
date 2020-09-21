package top.dcenter.ums.security.social.provider.weixin.api;

/**
 * 微信API调用接口
 * 
 * @author zhailiang
 *
 */
public interface Weixin {

	/**
	 * 根据 openId 获取微信用户信息
	 * @see <pre>com.ymt.pz365.framework.security.social.api.SocialUserProfileService#getUserProfile(java.lang .String) </pre>
	 * @param openId    openId
	 * @return WeixinUserInfo
	 */
	@SuppressWarnings("JavadocReference")
	WeixinUserInfo getUserInfo(String openId);
	
}
