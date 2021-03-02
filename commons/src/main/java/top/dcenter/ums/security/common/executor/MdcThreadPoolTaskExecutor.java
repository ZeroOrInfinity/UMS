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
import java.util.Map;
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
 * {@link MdcThreadPoolTaskExecutor#remove(Runnable)} 类内部调用有效, 通过实例调用此方法失效
 * @author YongWu zheng
 * @version V2.0  Created by  2020-12-22 10:52
 */
public class MdcThreadPoolTaskExecutor extends ThreadPoolExecutor {

    public MdcThreadPoolTaskExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public MdcThreadPoolTaskExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public MdcThreadPoolTaskExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public MdcThreadPoolTaskExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        // 获取父线程 MDC 中的内容
        final Map<String, String> context = MDC.getCopyOfContextMap();
        final Runnable r = decorateRunnable(runnable, context);
        super.execute(r);
    }

    @Override
    @NonNull
    public Future<?> submit(@NonNull Runnable task) {
        // 获取父线程 MDC 中的内容
        final Map<String, String> context = MDC.getCopyOfContextMap();
        final Runnable r = decorateRunnable(task, context);
        return super.submit(r);
    }

    @Override
    @NonNull
    public <T> Future<T> submit(@NonNull Callable<T> task) {
        // 获取父线程 MDC 中的内容
        final Map<String, String> context = MDC.getCopyOfContextMap();
        final Callable<T> c = decorateCallable(task, context);
        return super.submit(c);
    }

    @Override
    @NonNull
    public <T> Future<T> submit(Runnable task, T result) {
        // 获取父线程 MDC 中的内容
        final Map<String, String> context = MDC.getCopyOfContextMap();
        final Runnable r = decorateRunnable(task, context);
        return super.submit(r, result);
    }

    @Override
    @NonNull
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        // 获取父线程 MDC 中的内容
        final Map<String, String> context = MDC.getCopyOfContextMap();
        return super.invokeAny(tasks.stream().map(task -> decorateCallable(task, context)).collect(Collectors.toList()));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // 获取父线程 MDC 中的内容
        final Map<String, String> context = MDC.getCopyOfContextMap();
        return super.invokeAny(tasks.stream().map(task -> decorateCallable(task, context)).collect(Collectors.toList()),
                               timeout, unit);
    }

    @Override
    @NonNull
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        // 获取父线程 MDC 中的内容
        final Map<String, String> context = MDC.getCopyOfContextMap();
        return super.invokeAll(tasks.stream().map(task -> decorateCallable(task, context)).collect(Collectors.toList()));
    }

    @Override
    @NonNull
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        // 获取父线程 MDC 中的内容
        final Map<String, String> context = MDC.getCopyOfContextMap();
        return super.invokeAll(tasks.stream().map(task -> decorateCallable(task, context)).collect(Collectors.toList()),
                               timeout, unit);
    }

    /**
     * 子线程任务
     *
     * @param runnable {@link Runnable}
     * @param context  父线程 MDC 内容
     */
    private void run(@NonNull Runnable runnable, @Nullable Map<String, String> context) {
        // 设置 MDC 内容给子线程
        if (context != null) {
            MDC.setContextMap(context);
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
     * @param context 父线程 MDC 内容
     */
    @NonNull
    private <V> V call(@NonNull Callable<V> task, @Nullable Map<String, String> context) throws Exception {
        // 设置 MDC 内容给子线程
        if (context != null) {
            MDC.setContextMap(context);
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
    private Runnable decorateRunnable(@NonNull Runnable runnable, @Nullable Map<String, String> context) {
        return () -> run(runnable, context);
    }

    @NonNull
    private <V> Callable<V> decorateCallable(@NonNull Callable<V> task, @Nullable Map<String, String> context) {
        return () -> call(task, context);
    }
}