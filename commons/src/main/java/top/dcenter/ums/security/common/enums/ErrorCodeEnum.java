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

package top.dcenter.ums.security.common.enums;

import org.springframework.http.HttpStatus;
import top.dcenter.ums.security.common.consts.SecurityConstants;

/**
 * 错误代码
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/30 12:49
 */
@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
public enum ErrorCodeEnum {


    LOGOUT_SUCCESS(0, "登出成功"),

    PERMISSION_DENY(403, "您没有访问权限或未登录"),

    NOT_FOUND(404, "not found"),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "您未登录 或 操作未授权"),
    INVALID_SESSION(HttpStatus.UNAUTHORIZED.value(), "session 失效"),
    EXPIRED_SESSION(HttpStatus.UNAUTHORIZED.value(), "session 过期"),
    CONCURRENT_SESSION(HttpStatus.UNAUTHORIZED.value(), "你的账号在其他客户端上登录, 此客户端退出登录状态, 如非本人, 请更改密码"),
    SESSION_ENHANCE_CHECK(HttpStatus.UNAUTHORIZED.value(), "session 非法"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器异常 或 功能还在开发中"),
    INTERNAL_SERVER_ERROR(HttpStatus.NOT_FOUND.value(), SecurityConstants.INTERNAL_SERVER_ERROR_MSG),

    VALIDATE_CODE_PARAM_ERROR(600, "验证码参数错误"),
    VALIDATE_CODE_NOT_EMPTY(601, "验证码的值不能为空"),
    VALIDATE_CODE_EXPIRED(602, "验证码已失效, 请刷新"),
    VALIDATE_CODE_ERROR(603, "验证码错误"),
    ILLEGAL_VALIDATE_CODE_TYPE(604, "非法的验证码类型"),
    GET_VALIDATE_CODE_FAILURE(605, "获取验证码失败，请重试"),
    VALIDATE_CODE_FAILURE(606, "验证码校验不通过，请重试"),
    VALIDATE_CODE_NOT_EXISTS_IN_CACHE(607, "缓存中没有对应的验证码，请重新获取验证码"),
    CSRF_ERROR(608, "CSRF 错误"),

    SMS_CODE_PARAMETER_ERROR(610, "短信验证码参数错误"),
    SMS_CODE_ERROR(611, "短信验证码错误"),
    SMS_CODE_REPEATED_REQUEST(612, "手机验证码还在有效期内, 请勿重复获取"),

    MOBILE_NOT_EMPTY(620, "手机号不能为空"),
    MOBILE_PARAMETER_ERROR(621, "手机号参数错误"),
    MOBILE_FORMAT_ERROR(622, "手机号格式错误，请检查你的手机号码"),


    IMAGE_CODE_ERROR(630, "图片验证码错误"),

    TRACK_CODE_ERROR(640, "轨迹验证码错误"),

    SLIDER_CODE_ERROR(650, "滑块验证码错误"),

    SELECTION_CODE_ERROR(660, "选择验证码错误"),

    CUSTOMIZE_CODE_ERROR(670, "验证码错误"),

    PARAMETER_ERROR(700, "参数错误"),
    BUSINESS_ERROR(701, "业务异常"),

    ADD_ROLE_PERMISSION_FAILURE(710, "添加角色资源权限失败"),
    DEL_ROLE_PERMISSION_FAILURE(720, "删除角色资源权限失败"),
    UPDATE_ROLE_PERMISSIONS_FAILURE(730, "更新角色资源权限失败"),
    UPDATE_ROLE_PERMISSIONS_ILLEGAL(731, "非法操作: 越权更新角色资源"),
    QUERY_ROLE_PERMISSIONS_FAILURE(740, "查询角色资源信息失败"),
    QUERY_ROLE_PERMISSIONS_ILLEGAL(741, "非法操作: 越权查询角色资源信息"),

    ADD_RESOURCE_FAILURE(751, "添加资源权限失败"),
    ADD_RESOURCE_METHOD_FORMAT_ERROR(752, "添加资源权限失败, 方法格式错误"),


    REDIRECT_URL_PARAMETER_ILLEGAL(800, "非法的回调地址"),
    REDIRECT_URL_PARAMETER_ERROR(801, "回调地址不正确"),
    TAMPER_WITH_REDIRECT_URL_PARAMETER(802, "回调参数被篡改"),
    ILLEGAL_ACCESS_URL_ERROR(803, "非法访问"),

    CREATE_JWT_ERROR(810, "创建 JWT 异常"),
    JWT_EXPIRED(811, "JWT 过期"),
    JWT_INVALID(812, "JWT 失效"),
    JWT_REFRESH_TOKEN_INVALID(813, "JWT refreshToken 失效"),
    JWT_REFRESH_TOKEN_NOT_FOUND(814, "JWT refreshToken 没有发现"),
    REFRESH_JWT_POLICY_MISMATCH(815, "JWT 刷新策略不匹配"),
    REFRESH_TOKEN_DUPLICATE(816, "连续三次生成 refreshToken 都重复, 可以去买彩票了"),
    SAVE_REFRESH_TOKEN_ERROR(817, "缓存 refreshToken 时发生错误"),
    JWT_RE_AUTH(818, "JWT 需要重新登录认证"),

    USERNAME_USED(900, "用户名已存在"),
    USER_NOT_EXIST(901, "用户不存在"),
    USERNAME_NOT_EMPTY(902, "用户名不能为空"),
    PASSWORD_NOT_EMPTY(903, "密码不能为空"),
    QUERY_USER_INFO_ERROR(904, "未能获取到用户信息，请重试"),
    USER_REGISTER_FAILURE(905, "用户注册失败"),
    GET_REQUEST_PARAMETER_FAILURE(906, "获取注册信息失败"),
    USERNAME_OR_PASSWORD_ERROR(909, "用户名或密码错误"),
    UPDATE_CONNECTION_DATA_FAILURE(940, "更新第三方授权登录用户信息失败"),
    REFRESH_TOKEN_FAILURE(950, "refresh Token 刷新失败"),

    AUTH2_PROVIDER_NOT_SUPPORT(960, "此服务商的第三方授权登录不支持"),
    UN_BINDING_ERROR(961, "解绑第三方账号异常"),

    TENANT_ID_NOT_FOUND(970, "获取不到租户 ID "),

    ACCOUNT_DISABLED(981, "账号无效"),
    ACCOUNT_EXPIRED(982, "账号过期"),
    ACCOUNT_LOCKED(983, "账号已锁定"),
    CREDENTIALS_EXPIRED(984, "凭证过期"),


    USER_REGISTER_OAUTH2_FAILURE(999, "本地用户注册成功, 第三方信息保存失败");


    /**
     * 错误代码
     */
    private final Integer code;
    /**
     * 错误消息
     */
    private final String msg;

    ErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}