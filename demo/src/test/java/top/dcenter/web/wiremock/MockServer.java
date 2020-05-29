package top.dcenter.web.wiremock;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.removeAllMappings;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static top.dcenter.security.core.consts.SecurityConstants.CHARSET_UTF8;

/**
 * wireMock 模拟服务器
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 9:23
 */
public class MockServer {

    public static void main(String[] args) throws IOException {
        configureFor(8062);
        removeAllMappings();
        mock("/mock/1", "01.txt");
        mock("/mock/2", "02.txt");
    }

    private static void mock(String uri, String fileName) throws IOException {
        ClassPathResource pathResource = new ClassPathResource("mock/response/" + fileName);
        String content = StringUtils.join(FileUtils.readLines(pathResource.getFile(), CHARSET_UTF8).toArray(), "\\n");
        stubFor(get(urlPathEqualTo(uri)).willReturn(aResponse()
                                            .withBody(content)
                                            .withStatus(200)));

    }

}
