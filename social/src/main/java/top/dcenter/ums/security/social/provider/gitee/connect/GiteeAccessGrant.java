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

package top.dcenter.ums.security.social.provider.gitee.connect;

import lombok.Getter;
import org.springframework.social.oauth2.AccessGrant;

/**
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/19 1:45
 */
@Getter
public class GiteeAccessGrant extends AccessGrant {

    private String tokenType;
    /**
     * 秒
     */
    private Long createdAt;
    /**
     * 秒
     */
    private Long expiresIn;

    public GiteeAccessGrant() {
        super("");
    }

    public GiteeAccessGrant(String accessToken) {
        super(accessToken);
    }

    public GiteeAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn, Long createdAt,
                            String tokenType) {
        super(accessToken, scope, refreshToken, expiresIn);
        this.createdAt = createdAt;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;

    }

}