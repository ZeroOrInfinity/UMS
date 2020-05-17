package top.dcenter.security.core.vo;

import lombok.Data;

/**
 * 简单的 Vo 对象封装
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 19:39
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

    public static SimpleResponse success() {
        return new SimpleResponse(0);
    }
    public static SimpleResponse success(Object data) {
        SimpleResponse simpleResponse = new SimpleResponse(0);
        simpleResponse.setData(data);
        return simpleResponse;
    }

    public static SimpleResponse fail(int code, String msg) {
        return new SimpleResponse(code, msg);
    }

}
