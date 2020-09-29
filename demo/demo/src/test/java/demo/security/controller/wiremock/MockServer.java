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
import static top.dcenter.ums.security.core.consts.SecurityConstants.CHARSET_UTF8;

/**
 * wireMock 模拟服务器
 * @author zhailiang
 * @author  zyw
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
