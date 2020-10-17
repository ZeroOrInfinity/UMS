package top.dcenter.ums.security.core.oauth.repository;

import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.oauth.entity.AuthTokenPo;
import top.dcenter.ums.security.core.oauth.enums.EnableRefresh;

import java.util.List;

/**
 * A data access interface for managing a global store of users connections token to service providers.
 * Provides data access operations.
 * @author zyw
 * @version V2.0  Created by 2020-10-08 20:10
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface UsersConnectionTokenRepository {

	/**
	 * 根据 tokenId 获取 tokenId
	 * @param tokenId   tokenId
	 * @return  AuthTokenPo
	 * @throws Exception 查询错误
	 */
	AuthTokenPo findAuthTokenById(String tokenId) throws Exception;

	/**
	 * 持久化 authToken, 并把返回的 authToken id 保存在 authToken 中
	 * @param authToken     authToken
	 * @return  AuthTokenPo
	 * @throws Exception    持久化 authToken 异常
	 */
	AuthTokenPo saveAuthToken(AuthTokenPo authToken) throws Exception;

	/**
	 * 更新 {@link AuthTokenPo}
	 * @param authToken     更新 {@link AuthTokenPo}
	 * @return  AuthTokenPo
	 * @throws Exception    数据更新异常
	 */
	AuthTokenPo updateAuthToken(AuthTokenPo authToken) throws Exception;

	/**
	 * 删除 id = tokenId 的记录
	 * @param tokenId   tokenId
	 * @throws Exception 删除错误
	 */
	void delAuthTokenById(String tokenId) throws Exception;

	/**
	 * 统计总记录
	 * @return 总记录数
	 * @throws Exception sql 执行错误
	 */
	Long count() throws Exception;

	/**
	 * 获取 ID 范围在 startId(包含) 与 endId(包含) 之间且过期时间小于等于 expiredTime 且 enableRefresh=1 的 token 数据.<br>
	 *     用于定时 refreshToken 任务, 不做 spring cache 缓存处理
	 * @param expiredTime   过期时间
	 * @param startId       起始 id, 包含
	 * @param endId         结束 id, 包含
	 * @return  符合条件的 {@link AuthTokenPo} 列表
	 * @throws Exception   查询错误
	 */
	List<AuthTokenPo> findAuthTokenByExpireTimeAndBetweenId(@NonNull Long expiredTime, @NonNull Long startId,
	                                                        @NonNull Long endId) throws Exception;

	/**
	 * 根据 tokenId 更新 auth_token 表中的 enableRefresh 字段
	 * @param enableRefresh {@link EnableRefresh}
	 * @param tokenId       token id
	 * @throws Exception    更新异常
	 */
	void updateEnableRefreshByTokenId(@NonNull EnableRefresh enableRefresh, @NonNull Long tokenId) throws Exception;


}
