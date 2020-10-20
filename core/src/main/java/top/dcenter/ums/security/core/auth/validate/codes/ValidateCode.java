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

package top.dcenter.ums.security.core.auth.validate.codes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验证码封装
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/3 23:38
 */
@Getter
@Setter
@ToString
public class ValidateCode implements Serializable {

    private static final long serialVersionUID = 8564646192066649173L;

    private String code;
    private LocalDateTime expireTime;
    /**
     * 是否复用, 如果复用, 不会重新产生验证码, 仍使用验证码失败的验证码
     */
    private Boolean reuse;

    /**
     * 验证码构造器: 默认 <pre>reuse = false;</pre> 不复用
     * @param code      验证码
     * @param expireIn  秒
     */
    public ValidateCode(String code, int expireIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
        reuse = false;
    }

    public void setExpireTime(int expireIn) {
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    /**
     * 验证码构造器: 默认不复用
     * @param code      验证码
     * @param expireIn  过期日期
     * @param reuse     是否复用, 如果复用, 不会重新产生验证码, 仍使用验证码失败的验证码, 默认: false 即不复用
     */
    public ValidateCode(String code, int expireIn, Boolean reuse) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
        this.reuse = reuse;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}