package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.dcenter.ums.security.core.permission.config.EnableUriAuthorize;

/**
 * ums ums.client 简单配置
 * @author zyw
 * @version V1.0  Created by 2020/9/20 11:20
 */
@SpringBootApplication
@Controller
@EnableUriAuthorize()
public class BasicExampleApp {

    public static void main(String[] args) {
        SpringApplication.run(BasicExampleApp.class, args);
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello world!";
    }
}
