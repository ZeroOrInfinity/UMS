package top.dcenter.ums.security.social.provider.weibo.api;

import java.io.IOException;

/**
 * weibo 获取优化信息接口
 * @author zyw
 * @version V1.0  Created by 2020-06-18 12:04
 */
public interface Weibo {

	/**
	 * 根据 uid 获取 Weibo 用户信息
	 * @param uid provideUserId
	 * @return  WeiboUserInfo 如果没有此用户则返回 null
	 * @throws IOException
	 */
	WeiboUserInfo getUserInfo(String uid) throws IOException;

}