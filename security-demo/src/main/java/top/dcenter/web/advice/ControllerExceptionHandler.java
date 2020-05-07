package top.dcenter.web.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.dcenter.security.core.excception.ParameterErrorException;
import top.dcenter.security.core.excception.UserNotExistException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/2 15:35
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    @ExceptionHandler(UserNotExistException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleUserNOtException(UserNotExistException ex) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("id", ex.getId());
        map.put("message", ex.getMessage());
        log.error(ex.getMessage(), ex);
        return map;
    }

    @ExceptionHandler(ParameterErrorException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> parameterErrorException(ParameterErrorException ex) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("message", ex.getMessage());
        log.error(ex.getMessage(), ex);
        return map;
    }

}
