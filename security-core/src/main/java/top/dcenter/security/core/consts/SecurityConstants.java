package top.dcenter.security.core.consts;

/**
 * social 常量
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/6 21:29
 */
public class SecurityConstants {

    /**
     * 当请求需要身份认证时，默认跳转的url
     */
    public static final String DEFAULT_UNAUTHENTICATION_URL = "/authentication/require";
    /**
     * 默认的用户名密码登录请求处理url
     */
    public static final String DEFAULT_LOGIN_PROCESSING_URL_FORM = "/authentication/form";
    /**
     * 默认的手机验证码登录请求处理url
     */
    public static final String DEFAULT_LOGIN_PROCESSING_URL_MOBILE = "/authentication/mobile";
    /**
     * 默认的 social OAuth2 注册请求处理url
     */
    public static final String DEFAULT_LOGIN_PROCESSING_URL_SOCIAL = "/authentication/social";
    /**
     * 默认登录页面
     */
    public static final String DEFAULT_LOGIN_PAGE_URL = "/security/login.html";
    /**
     * session失效默认的跳转地址
     */
    public static final String DEFAULT_SESSION_INVALID_URL = "/session/invalid";


    /**
     * 服务器内部错误信息
     */
    public static final String INTERNAL_SERVER_ERROR_MSG = "服务器开小差，请重试";

    /**
     * 需要验证码校验的 authUrls 之间的分隔符
     */
    public static final String URI_SEPARATOR = ",";
    /**
     * 默认的处理验证码的url前缀
     */
    public static final String DEFAULT_VALIDATE_CODE_URL_PREFIX = "/code";


    /**
     * 图片验证码的 SESSION KEY
     */
    public static final String SESSION_KEY_IMAGE = "SESSION_KEY_IMAGE_CODE";
    /**
     * 提交图片校验码请求时，请求中带点图片校验码变量名，默认 imageCode
     */
    public static final String DEFAULT_REQUEST_PARAM_IMAGE_CODE_NAME = "imageCode";

    /**
     * 提交短信校验码请求时，请求中带的短信校验码变量名，默认 smsCode
     */
    public static final String DEFAULT_REQUEST_PARAM_SMS_CODE_NAME = "smsCode";
    /**
     * 提交短信校验码请求时，请求中带的手机号变量名，默认 mobile
     */
    public static final String DEFAULT_REQUEST_PARAM_MOBILE_NAME = "mobile";
    /**
     * 短信验证码的 SESSION KEY
     */
    public static final String SESSION_KEY_SMS = "SESSION_KEY_SMS_CODE";


    // =============== url 相关 ===============
    /**
     * request GET Method
     */
    public static final String GET_METHOD = "GET";
    /**
     * request POST Method
     */
    public static final String POST_METHOD = "POST";
    /**
     * url 参数标识符
     */
    public static final String URL_PARAMETER_IDENTIFIER = "?";
    /**
     * url 路径之间分隔符
     */
    public static final String URL_SEPARATOR = "/";
    /**
     * url 请求上带的参数之间分隔符
     */
    public static final String URL_PARAMETER_SEPARATOR = "&";
    /**
     * key value 键值对分隔符
     */
    public static final String KEY_VALUE_SEPARATOR = "=";


    /**
     * The default name for remember me parameter name and remember me cookie name
     */
    public static final String DEFAULT_REMEMBER_ME_NAME = "remember-me";

    /**
     * 查询表返回的结果集 ResultSet 的 COUNT(1) 索引
     */
    public static final int QUERY_TABLE_EXIST_SQL_RESULT_SET_COLUMN_INDEX = 1;

    /**
     * 查询数据库名称
     */
    public static final String QUERY_DATABASE_NAME_SQL = "select database();";



}
