package top.dcenter.ums.security.core.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import top.dcenter.ums.security.core.enums.ErrorCodeEnum;

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
     * @return
     */
    public static ResponseResult success() {
        return ResponseResult.success(null);
    }

    /**
     * 默认成功返回 code = 0
     * @param data
     * @return
     */
    public static ResponseResult success(Object data) {
        return ResponseResult.success(null, data);
    }

    /**
     * 默认成功返回 code = 0
     * @param msg
     * @return
     */
    public static ResponseResult success(String msg) {
        return ResponseResult.success(msg, null);
    }

    /**
     * 默认成功返回 code = 0
     * @param msg
     * @param data
     * @return
     */
    public static ResponseResult success(String msg, Object data) {
        ResponseResult responseResult = new ResponseResult(0);
        responseResult.setMsg(msg);
        responseResult.setData(data);
        return responseResult;
    }

    /**
     * @param errorCodeEnum
     * @return
     */
    public static ResponseResult fail(ErrorCodeEnum errorCodeEnum) {
        return ResponseResult.fail(errorCodeEnum, null);
    }

    /**
     * @param errorCodeEnum
     * @param data
     * @return
     */
    public static ResponseResult fail(ErrorCodeEnum errorCodeEnum, Object data) {
        return new ResponseResult(errorCodeEnum.getCode(), errorCodeEnum.getMsg(), data);
    }

    /**
     * @param errorMsg
     * @param errorCodeEnum
     * @return
     */
    public static ResponseResult fail(String errorMsg, ErrorCodeEnum errorCodeEnum) {
        return new ResponseResult(errorCodeEnum.getCode(), errorMsg, null);
    }

    /**
     * @param errorMsg
     * @param errorCodeEnum
     * @param data
     * @return
     */
    public static ResponseResult fail(String errorMsg, ErrorCodeEnum errorCodeEnum, Object data) {
        return new ResponseResult(errorCodeEnum.getCode(), errorMsg, data);
    }

}
