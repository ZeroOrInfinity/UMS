package top.dcenter.ums.security.core.oauth.enums;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池拒绝策略枚举
 * @author zyw
 * @version V2.0  Created by 2020/10/15 12:44
 */
@SuppressWarnings("unused")
public enum RejectedExecutionHandlerPolicy {
    /**
     * @see java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
     */
    CALLER_RUNS {
        @Override
        public RejectedExecutionHandler getRejectedHandler() {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        }
    },
    /**
     * @see java.util.concurrent.ThreadPoolExecutor.AbortPolicy
     */
    ABORT
            {
                @Override
                public RejectedExecutionHandler getRejectedHandler() {
                    return new ThreadPoolExecutor.AbortPolicy();
                }
            },
    /**
     * @see java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy
     */
    DISCARD_OLDEST
            {
                @Override
                public RejectedExecutionHandler getRejectedHandler() {
                    return new ThreadPoolExecutor.DiscardOldestPolicy();
                }
            },
    /**
     * @see java.util.concurrent.ThreadPoolExecutor.DiscardPolicy
     */
    DISCARD
            {
                @Override
                public RejectedExecutionHandler getRejectedHandler() {
                    return new ThreadPoolExecutor.DiscardPolicy();
                }
            };

    public abstract RejectedExecutionHandler getRejectedHandler();
}
