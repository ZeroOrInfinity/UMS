package top.dcenter.ums.security.core.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;

/**
 * 简单的 Vo 对象封装, code = 0 表示处理成功信息，其他表示失败。<br><br>
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 19:39
 * @author zyw
 */
@Getter
@Setter
@AllArgsConstructor
public class ResponseResult {
    /**
     * 0 表示处理成功信息，其他表示失败
     */
    private int code;
    private String msg;
    private Object data;

    public ResponseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    public ResponseResult(int code) {
        this(code, null);
    }

    /**
     * 默认成功返回 code = 0
     * @return  ResponseResult
     */
    public static ResponseResult success() {
        return ResponseResult.success(null);
    }

    /**
     * 默认成功返回 code = 0 与 msg
     * @param msg   msg
     * @return  ResponseResult
     */
    public static ResponseResult success(String msg) {
        return ResponseResult.success(msg, null);
    }

    /**
     * 默认成功返回 code = 0, msg, data
     * @param msg   msg
     * @param data  data
     * @return  ResponseResult
     */
    public static ResponseResult success(String msg, Object data) {
        ResponseResult responseResult = new ResponseResult(0);
        responseResult.setMsg(msg);
        responseResult.setData(data);
        return responseResult;
    }

    /**
     * @param errorCodeEnum errorCodeEnum
     * @return  ResponseResult
     */
    public static ResponseResult fail(ErrorCodeEnum errorCodeEnum) {
        return ResponseResult.fail(errorCodeEnum, null);
    }

    /**
     * @param errorCodeEnum errorCodeEnum
     * @param data          data
     * @return  ResponseResult
     */
    public static ResponseResult fail(ErrorCodeEnum errorCodeEnum, Object data) {
        return new ResponseResult(errorCodeEnum.getCode(), errorCodeEnum.getMsg(), data);
    }

    /**
     * @param errorMsg          errorMsg
     * @param errorCodeEnum     errorCodeEnum
     * @return  ResponseResult
     */
    public static ResponseResult fail(String errorMsg, ErrorCodeEnum errorCodeEnum) {
        return new ResponseResult(errorCodeEnum.getCode(), errorMsg, null);
    }

    /**
     * @param errorMsg      errorMsg
     * @param errorCodeEnum errorCodeEnum
     * @param data          data
     * @return  ResponseResult
     */
    public static ResponseResult fail(String errorMsg, ErrorCodeEnum errorCodeEnum, Object data) {
        return new ResponseResult(errorCodeEnum.getCode(), errorMsg, data);
    }

}
