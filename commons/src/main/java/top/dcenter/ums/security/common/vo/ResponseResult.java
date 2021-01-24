/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;

import java.time.LocalDateTime;

/**
 * 简单的 Vo 对象封装, code = 200 表示处理成功信息，其他表示失败。<br><br>
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 19:39
 * @author YongWu zheng
 */
public class ResponseResult {
    /**
     * 200 表示处理成功信息，其他表示失败
     */
    private int code;
    private String msg;
    /**
     * 失败的情况下, 大部分情况返回日志链路追踪 ID
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;

    private ResponseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
        this.timestamp = LocalDateTime.now();
    }

    private ResponseResult(int code) {
        this(code, null);
    }

    private ResponseResult(int code, String msg, Object data, LocalDateTime timestamp) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = timestamp;
    }

    /**
     * 默认成功返回 code = 200
     * @return  ResponseResult
     */
    public static ResponseResult success() {
        return ResponseResult.success(null);
    }

    /**
     * 默认成功返回 code = 200 与 msg
     * @param msg   msg
     * @return  ResponseResult
     */
    public static ResponseResult success(String msg) {
        return ResponseResult.success(msg, null);
    }

    /**
     * 默认成功返回 code = 200, msg, data
     * @param msg   msg
     * @param data  data
     * @return  ResponseResult
     */
    public static ResponseResult success(String msg, Object data) {
        ResponseResult responseResult = new ResponseResult(200);
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
        return new ResponseResult(errorCodeEnum.getCode(), errorCodeEnum.getMsg(), data, LocalDateTime.now());
    }

    /**
     * @param errorMsg          errorMsg
     * @param errorCodeEnum     errorCodeEnum
     * @return  ResponseResult
     */
    public static ResponseResult fail(String errorMsg, ErrorCodeEnum errorCodeEnum) {
        return new ResponseResult(errorCodeEnum.getCode(), errorMsg, null, LocalDateTime.now());
    }

    /**
     * @param errorMsg      errorMsg
     * @param errorCodeEnum errorCodeEnum
     * @param data          data
     * @return  ResponseResult
     */
    public static ResponseResult fail(String errorMsg, ErrorCodeEnum errorCodeEnum, Object data) {
        return new ResponseResult(errorCodeEnum.getCode(), errorMsg, data, LocalDateTime.now());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}