package top.dcenter.ums.security.core.oauth.job;

/**
 * 刷新 accessToken 定时任务接口
 * @author zyw
 * @version V2.0  Created by 2020/10/14 14:03
 */
public interface RefreshTokenJob {

    /**
     * 刷新第三方授权登录的 accessToken 有效期的定时任务
     */
    void refreshTokenJob();

}
