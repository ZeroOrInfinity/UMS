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
package top.dcenter.ums.security.common.consts;

import org.springframework.core.annotation.Order;

/**
 * 关于事务的优先级常量.
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/13 17:22
 */
public interface TransactionalConstants {
    /**
     * {@link Order} 值为 1, 方便调整 AOP 执行顺序
     */
    int ONE_PRECEDENCE = 1;
    /**
     * 事务的 {@link Order} 默认值 - 2, 方便调整 AOP 执行顺序
     */
    int TRANSACTIONAL_PRECEDENCE_BEFORE2 = 998;
    /**
     * 事务的 {@link Order} 默认值 - 1, 方便调整 AOP 执行顺序
     */
    int TRANSACTIONAL_PRECEDENCE_BEFORE = 999;
    /**
     * 事务的 {@link Order} 默认值, 方便调整 AOP 执行顺序
     */
    int TRANSACTIONAL_DEFAULT_PRECEDENCE = 1000;

    /**
     * 事务的 {@link Order} 默认值 + 1, 方便调整 AOP 执行顺序
     */
    int TRANSACTIONAL_PRECEDENCE_AFTER = 1001;

    /**
     * 事务的 {@link Order} 默认值 + 2, 方便调整 AOP 执行顺序
     */
    int TRANSACTIONAL_PRECEDENCE_AFTER2 = 1002;
    /**
     * {@link Order} 值为 {@code Integer.MAX_VALUE}, 方便调整 AOP 执行顺序
     */
    int MAX_VALUE_PRECEDENCE = Integer.MAX_VALUE;

}
