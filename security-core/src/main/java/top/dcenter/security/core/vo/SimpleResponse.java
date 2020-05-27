package top.dcenter.security.core.vo;

import lombok.Data;

/**
 * 简单的 Vo 对象封装, code = 0 表示处理成功信息，其他表示失败。<br>
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 19:39
 * @medifiedBy zyw
 */
@Data
public class SimpleResponse {
    /**
     * 0 表示处理成功信息，其他表示失败
     */
    private int code;
    private String msg;
    private Object data;

    public SimpleResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    public SimpleResponse(int code) {
        this(code, null);
    }

    /**
     * 默认成功返回 code = 0
     * @return
     */
    public static SimpleResponse success() {
        return SimpleResponse.success(null);
    }

    /**
     * 默认成功返回 code = 0
     * @param data
     * @return
     */
    public static SimpleResponse success(Object data) {
        return SimpleResponse.success(null, data);
    }

    /**
     * 默认成功返回 code = 0
     * @param msg
     * @return
     */
    public static SimpleResponse success(String msg) {
        return SimpleResponse.success(msg, null);
    }

    /**
     * 默认成功返回 code = 0
     * @param msg
     * @param data
     * @return
     */
    public static SimpleResponse success(String msg, Object data) {
        SimpleResponse simpleResponse = new SimpleResponse(0);
        simpleResponse.setMsg(msg);
        simpleResponse.setData(data);
        return simpleResponse;
    }

    /**
     * @param code
     * @param msg
     * @return
     */
    public static SimpleResponse fail(int code, String msg) {
        return SimpleResponse.fail(code, msg, null);
    }

    /**
     * @param code
     * @param msg
     * @param data
     * @return
     */
    public static SimpleResponse fail(int code, String msg, Object data) {
        SimpleResponse simpleResponse = new SimpleResponse(code, msg);
        simpleResponse.setData(data);
        return simpleResponse;
    }

}
