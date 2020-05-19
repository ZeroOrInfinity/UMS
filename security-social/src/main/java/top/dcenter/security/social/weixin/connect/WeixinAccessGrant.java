/**
 * 
 */
package top.dcenter.security.social.weixin.connect;

import org.springframework.social.oauth2.AccessGrant;

import java.lang.reflect.Field;

/**
 * 微信的access_token信息。与标准OAuth2协议不同，微信在获取access_token时会同时返回openId,并没有单独的通过accessToke换取openId的服务
 * 
 * 所以在这里继承了标准AccessGrant，添加了openId字段，作为对微信access_token信息的封装。
 * 
 * @author zhailiang
 *
 */
public class WeixinAccessGrant extends AccessGrant {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7243374526633186782L;
	
	private String openId;
	/**
	 * 毫秒
	 */
	private Long expiresIn;

	public WeixinAccessGrant() {
		super("");
	}

	public WeixinAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn) {
		super(accessToken, scope, refreshToken, expiresIn);
		this.expiresIn = expiresIn * 1000L;
	}

	/**
	 * 通过 expiresIn 设置 expireTime
	 * @param expiresIn 毫秒
	 */
	public void setExpiresTime(Long expiresIn) {
		this.expiresIn = expiresIn;
		Class<? extends WeixinAccessGrant> clz = this.getClass();
		try
		{
			Field expireTimeField = clz.getField("expireTime");
			expireTimeField.setAccessible(true);
			expireTimeField.set(this, expiresIn != null ? System.currentTimeMillis() + expiresIn : null);
		}
		catch (Exception e) { }
	}

	/**
	 * @return the openId
	 */
	public String getOpenId() {
		return openId;
	}

	/**
	 * @param openId the openId to set
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	
}
