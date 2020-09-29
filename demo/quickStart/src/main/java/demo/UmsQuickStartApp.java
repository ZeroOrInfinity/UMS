package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * UMS Quick Start
 * @author zyw
 * @version V1.0  Created by 2020/9/20 11:20
 */
@SpringBootApplication
@Controller
public class UmsQuickStartApp {

    public static void main(String[] args) {
        SpringApplication.run(UmsQuickStartApp.class, args);
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
//        "https://gitee.com/oauth/authorize?client_id=6308c00c5ace21417d6f5069a4f110909f2de2c281266262588abc541381f61e&response_type=code&redirect_uri=http://localhost:9090/demo/auth/callback&state=95C3E939341A4FE98FF2cGF0aD0vYXV0aC9jYWxsYmFjay9naXRlZQ=="
        return "hello world!";
    }
}
