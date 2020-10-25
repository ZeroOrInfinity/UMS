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

package demo.security.controller.wiremock;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.removeAllMappings;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static top.dcenter.ums.security.common.consts.SecurityConstants.CHARSET_UTF8;

/**
 * wireMock 模拟服务器
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/3 9:23
 */
public class MockServer {

    public static void main(String[] args) throws IOException {
        // 先启动 wiremock-standalone-X.X.X.jar, 在运行 main, 上传配置
        configureFor("127.0.0.1", 8062);
        // 清空以前的配置
        removeAllMappings();

        // 添加配置
        mock("/mock/1", "01.txt");
        mock("/mock/2", "02.txt");

        // 运行完成, 就可以在浏览器访问 /mock/1 与 /mock/2
    }

    private static void mock(String uri, String fileName) throws IOException {
        ClassPathResource pathResource = new ClassPathResource("response/" + fileName);
        String content = StringUtils.join(FileUtils.readLines(pathResource.getFile(), CHARSET_UTF8).toArray(), "\\n");
        stubFor(get(urlPathEqualTo(uri)).willReturn(aResponse()
                                            .withBody(content)
                                            .withStatus(200)));

    }

}