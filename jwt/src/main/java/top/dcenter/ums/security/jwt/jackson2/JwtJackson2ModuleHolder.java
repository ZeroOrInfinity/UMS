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
package top.dcenter.ums.security.jwt.jackson2;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.lang.NonNull;
import top.dcenter.ums.security.common.api.jackson2.SimpleModuleHolder;

/**
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.3.2 23:08
 */
public class JwtJackson2ModuleHolder implements SimpleModuleHolder {

    private final JwtJackson2Module jwtJackson2Module;

    public JwtJackson2ModuleHolder() {
        jwtJackson2Module = new JwtJackson2Module();
    }

    @NonNull
    @Override
    public SimpleModule getSimpleModule() {
        return jwtJackson2Module;
    }
}
