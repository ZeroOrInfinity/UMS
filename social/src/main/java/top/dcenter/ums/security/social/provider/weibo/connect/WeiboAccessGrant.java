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

package top.dcenter.ums.security.social.provider.weibo.connect;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.social.oauth2.AccessGrant;

/**
 * @author YongWu zheng
 * @version V1.0  Created by 2020/6/18 14:34
 */
@Getter
public class WeiboAccessGrant extends AccessGrant {

    private static final long serialVersionUID = 1360503645657443112L;

    /**
     * provider 的 uid
     */
    private String uid;
    /**
     * 提醒: 秒
     */
    private Long remindIn;
    /**
     * 秒
     */
    private Long expiresIn;
    /**
     * 是否实名认证
     */
    @JsonProperty("isRealName")
    private Boolean realName;

    public WeiboAccessGrant() {
        super("");
    }

    public WeiboAccessGrant(String accessToken) {
        super(accessToken);
    }

    public WeiboAccessGrant(String accessToken, String scope, String refreshToken,
                            Long expiresIn, Long remindIn, String uid, Boolean realName) {
        super(accessToken, scope, refreshToken, expiresIn);
        this.uid = uid;
        this.remindIn = remindIn;
        this.expiresIn = expiresIn;
        this.realName = realName;
    }
}