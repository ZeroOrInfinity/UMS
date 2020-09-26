package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ums security:client 详细配置
 * @author zyw
 * @version V1.0  Created by 2020/9/20 11:20
 */
@SpringBootApplication
@Controller
public class SocialSimpleExampleApp {

    public static void main(String[] args) {
        SpringApplication.run(SocialSimpleExampleApp.class, args);
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello world!";
    }
}
