package demo.validate.code.slider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.ums.security.core.vo.ResponseResult;


/**
 *  滑块验证码控制器
 * @author zyw
 * @version V1.0  Created by 2020-09-22 15:00
 */
@Slf4j
@RestController
@RequestMapping("/slider")
public class SliderCoderController {


    /**
     * 验证方法, 所有验证逻辑都通过 {@link top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeFilter} 处理:<br>
     *     1. 验证不通过, 过滤器直接抛出 {@link top.dcenter.ums.security.core.exception.ValidateCodeException } ,
     *        再通过 {@link top.dcenter.ums.security.core.api.advice.SecurityControllerExceptionHandler} 处理返回.
     *     2. 验证通过, 通过此方法返回.
     *
     * @return  ResponseResult
     */
    @RequestMapping(value = "check",method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult check() {
        return ResponseResult.success();
    }

}