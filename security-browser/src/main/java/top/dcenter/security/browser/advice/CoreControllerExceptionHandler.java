package top.dcenter.security.browser.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.dcenter.security.core.excception.ValidateCodeParamErrorException;
import top.dcenter.security.core.vo.SimpleResponse;
import top.dcenter.security.core.excception.ParameterErrorException;
import top.dcenter.security.core.excception.UserNotExistException;
import top.dcenter.security.core.excception.ValidateCodeException;
import top.dcenter.security.core.excception.ValidateCodeProcessException;

/**
 * 核心错误处理器
 * @author zyw
 * @version V1.0  Created by 2020/5/2 15:35
 */
@ControllerAdvice
@Slf4j
public class CoreControllerExceptionHandler {

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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleResponse validateCodeException(ValidateCodeException ex) {
        log.error(ex.getMessage(), ex);
        return SimpleResponse.fail(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
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
}