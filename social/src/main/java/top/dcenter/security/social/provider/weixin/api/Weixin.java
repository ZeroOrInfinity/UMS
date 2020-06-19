/**
 * 
 */
package top.dcenter.security.social.provider.weixin.api;

/**
 * 微信API调用接口
 * 
 * @author zhailiang
 *
 */
public interface Weixin {

	/**
	 * 根据 openId 获取微信用户信息
	 * @see com.ymt.pz365.framework.security.social.api.SocialUserProfileService#getUserProfile(java.lang.String)
	 */
	@SuppressWarnings("JavadocReference")
	WeixinUserInfo getUserInfo(String openId);
	
}
