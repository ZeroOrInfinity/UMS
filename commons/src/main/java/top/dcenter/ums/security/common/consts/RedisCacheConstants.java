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

/**
 * redis cache 缓存.
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.21 23:30
 */
public final class RedisCacheConstants {

    private RedisCacheConstants() { }

    /**
     * 第三方授权登录信息 kv 缓存
     */
    public static final String USER_CONNECTION_CACHE_NAME = "UCC";
    /**
     * 第三方授权登录信息 hash 缓存, 当清除缓存时精确清除, 用按 hash key field 清除.
     */
    public static final String USER_CONNECTION_HASH_CACHE_NAME = "UCHC";
    /**
     * 第三方授权登录信息 hash 缓存, 当清除缓存时模糊清除, 用按 hash key 清除.
     */
    public static final String USER_CONNECTION_HASH_ALL_CLEAR_CACHE_NAME = "UCHACC";
}
