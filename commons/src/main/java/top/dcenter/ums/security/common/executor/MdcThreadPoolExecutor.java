/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package top.dcenter.ums.security.common.executor;

import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.dcenter.ums.security.common.utils.UuidUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static top.dcenter.ums.security.common.consts.MdcConstants.MDC_KEY;

/**
 * 实现 基于 SLF4J MDC 机制的日志链路追踪功能. <br>
 * {@link MdcThreadPoolExecutor#remove(Runnable)} 类内部调用有效, 通过实例调用此方法失效
 * @author YongWu zheng
 * @version V2.0  Created by  2020-12-22 10:52
 */
public class MdcThreadPoolExecutor extends ThreadPoolExecutor {

    public MdcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public MdcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public MdcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public MdcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        // 获取父线程 MDC 中的内容
        final String mdcTraceId = MDC.get(MDC_KEY);
        final Runnable r = decorateRunnable(runnable, mdcTraceId);
        super.execute(r);
    }

    @Override
    @NonNull
    public Future<?> submit(@NonNull Runnable task) {
        // 获取父线程 MDC 中的内容
        final String mdcTraceId = MDC.get(MDC_KEY);
        final Runnable r = decorateRunnable(task, mdcTraceId);
        return super.submit(r);
    }

    @Override
    @NonNull
    public <T> Future<T> submit(@NonNull Callable<T> task) {
        // 获取父线程 MDC 中的内容
        final String mdcTraceId = MDC.get(MDC_KEY);
        final Callable<T> c = decorateCallable(task, mdcTraceId);
        return super.submit(c);
    }

    @Override
    @NonNull
    public <T> Future<T> submit(Runnable task, T result) {
        // 获取父线程 MDC 中的内容
        final String mdcTraceId = MDC.get(MDC_KEY);
        final Runnable r = decorateRunnable(task, mdcTraceId);
        return super.submit(r, result);
    }

    @Override
    @NonNull
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        // 获取父线程 MDC 中的内容
        final String mdcTraceId = MDC.get(MDC_KEY);
        return super.invokeAny(tasks.stream().map(task -> decorateCallable(task, mdcTraceId)).collect(Collectors.toList()));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // 获取父线程 MDC 中的内容
        final String mdcTraceId = MDC.get(MDC_KEY);
        return super.invokeAny(tasks.stream().map(task -> decorateCallable(task, mdcTraceId)).collect(Collectors.toList()),
                               timeout, unit);
    }

    @Override
    @NonNull
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        // 获取父线程 MDC 中的内容
        final String mdcTraceId = MDC.get(MDC_KEY);
        return super.invokeAll(tasks.stream().map(task -> decorateCallable(task, mdcTraceId)).collect(Collectors.toList()));
    }

    @Override
    @NonNull
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        // 获取父线程 MDC 中的内容
        final String mdcTraceId = MDC.get(MDC_KEY);
        return super.invokeAll(tasks.stream().map(task -> decorateCallable(task, mdcTraceId)).collect(Collectors.toList()),
                               timeout, unit);
    }

    /**
     * 子线程任务
     *
     * @param runnable {@link Runnable}
     * @param mdcTraceId  mdc trace id
     */
    private void run(@NonNull Runnable runnable, @Nullable String mdcTraceId) {
        // 设置 MDC 内容给子线程
        if (mdcTraceId != null) {
            MDC.put(MDC_KEY, mdcTraceId);
        }
        else {
            MDC.put(MDC_KEY, UuidUtils.getUUID());
        }
        try {
            runnable.run();
        }
        finally {
            // 清空 MDC 内容
            MDC.remove(MDC_KEY);
        }
    }

    /**
     * 子线程任务
     *
     * @param task    {@link Callable}
     * @param mdcTraceId mdc trace id
     */
    @NonNull
    private <V> V call(@NonNull Callable<V> task, @Nullable String mdcTraceId) throws Exception {
        // 设置 MDC 内容给子线程
        if (mdcTraceId != null) {
            MDC.put(MDC_KEY, mdcTraceId);
        }
        else {
            MDC.put(MDC_KEY, UuidUtils.getUUID());
        }
        try {
            return task.call();
        }
        finally {
            // 清空 MDC 内容
            MDC.remove(MDC_KEY);
        }
    }

    @NonNull
    private Runnable decorateRunnable(@NonNull Runnable runnable, @Nullable String mdcTraceId) {
        return () -> run(runnable, mdcTraceId);
    }

    @NonNull
    private <V> Callable<V> decorateCallable(@NonNull Callable<V> task, @Nullable String mdcTraceId) {
        return () -> call(task, mdcTraceId);
    }
}