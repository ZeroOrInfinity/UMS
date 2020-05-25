package top.dcenter.security.browser.api.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.dcenter.security.core.excception.IllegalAccessUrlException;
import top.dcenter.security.core.excception.ParameterErrorException;
import top.dcenter.security.core.excception.UserNotExistException;
import top.dcenter.security.core.excception.ValidateCodeException;
import top.dcenter.security.core.excception.ValidateCodeParamErrorException;
import top.dcenter.security.core.excception.ValidateCodeProcessException;
import top.dcenter.security.core.vo.SimpleResponse;

/**
 * 核心错误处理器,如需自定义，继承此类并注入 IOC 容器即可
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/2 15:35
 */
@Slf4j
public class SecurityControllerExceptionHandler {

    @ExceptionHandler(UserNotExistException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleResponse handleUserNOtException(UserNotExistException ex) {
        SimpleResponse simpleResponse = SimpleResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        simpleResponse.setData(ex.getId());
        log.error(ex.getMessage(), ex);
        return simpleResponse;
    }

    @ExceptionHandler(ParameterErrorException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleResponse parameterErrorException(ParameterErrorException ex) {
        log.error(ex.getMessage(), ex);
        return SimpleResponse.fail(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
    
    @ExceptionHandler(ValidateCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public SimpleResponse validateCodeException(ValidateCodeException ex) {
        log.error(ex.getMessage(), ex);
        return SimpleResponse.fail(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }

    @ExceptionHandler(ValidateCodeParamErrorException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleResponse validateCodeParamErrorException(ValidateCodeParamErrorException ex) {
        log.error(ex.getMessage(), ex);
        return SimpleResponse.fail(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(ValidateCodeProcessException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleResponse validateCodeProcessException(ValidateCodeProcessException ex) {
        log.error(ex.getMessage(), ex);
        return SimpleResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }
    @ExceptionHandler(IllegalAccessUrlException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleResponse illegalAccessUrlException(IllegalAccessUrlException ex) {
        String errorMsg = ex.getMessage();
        log.error(errorMsg, ex);
        return SimpleResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMsg);
    }

}