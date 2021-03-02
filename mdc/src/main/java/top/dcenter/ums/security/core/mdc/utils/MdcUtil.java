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
package top.dcenter.ums.security.core.mdc.utils;

import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.dcenter.ums.security.core.api.mdc.MdcIdGenerator;
import top.dcenter.ums.security.core.mdc.MdcIdType;

import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

/**
 * mdc 工具
 * @author YongWu zheng
 * @version V2.0  Created by 2020.11.27 20:53
 */
public final class MdcUtil {

    /**
     * 在输出日志中加上指定的 MDC_TRACE_ID
     */
    public static final String MDC_KEY = "MDC_TRACE_ID";

    private MdcUtil() {}

    /**
     * 获取当前线程的 MDC 日志链路追踪 ID.
     * @return  返回 MDC 日志链路追踪 ID, 如果不存在, 返回 null
     */
    @Nullable
    public static String getMdcTraceId() {
        try {
            return MDC.get(MDC_KEY);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前线程的 MDC 日志链路追踪 ID; 如没有日志链路追踪 ID, 生成日志链路追踪 ID.
     * @param type          MDC id 类型
     * @param idGenerator   MDC id 生成器.
     * @return  返回 MDC 日志链路追踪 ID, 如果不存在, 返回 null
     */
    @NonNull
    public static String getMdcTraceId(@NonNull MdcIdType type, @Nullable MdcIdGenerator idGenerator) {
        try {
            String id = MDC.get(MDC_KEY);
            if (hasText(id)) {
            	return id;
            }
            return getMdcId(type, idGenerator);
        }
        catch (Exception e) {
            return getMdcId(type, idGenerator);
        }
    }

    /**
     * 获取基于 SLF4J MDC 机制实现日志链路追踪 ID
     * @param type          MDC id 类型
     * @param idGenerator   MDC id 生成器.
     * @return  返回 MDC 日志链路追踪 ID
     */
    @NonNull
    public static String getMdcId(@NonNull MdcIdType type, @Nullable MdcIdGenerator idGenerator) {
        if (MdcIdType.CUSTOMIZE_ID.equals(type) && idGenerator != null) {
            return idGenerator.getMdcId();
        }
        return type.getMdcId();
    }

    /**
     * 装饰 task, 如没有日志链路追踪 ID, 生成日志链路追踪 ID.
     * @param task          任务
     * @param idType        MDC id 类型
     * @param idGenerator   mdc id 生成器
     * @return  返回装饰后的 task
     */
    @NonNull
    public static Runnable decorateTasks(@NonNull Runnable task, @NonNull MdcIdType idType,
                                         @Nullable MdcIdGenerator idGenerator) {
        return () -> {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            boolean isRemoveMdcId = false;
            if (contextMap == null) {
                MDC.put(MDC_KEY, getMdcId(idType, idGenerator));
                isRemoveMdcId = true;
            }

            task.run();

            if (isRemoveMdcId) {
                MDC.remove(MDC_KEY);
            }
        };
    }
}
