package demo.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.security.core.permission.config.EnableUriAuthorize;

/**
 * UMS 脚手架演示类
 *
 * @author zyw
 * @version V1.0  Created by 2020/5/1 19:06
 */
@SpringBootApplication
@RestController
@EnableUriAuthorize()
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class);
    }


}
