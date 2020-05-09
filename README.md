# 无侵入式模块化权限模型脚手架
> 1. 引入模块依赖：
> 2. 通过 application.yml 或 application.properties 配置:

## 验证码功能
- 基本功能：在 core 包中；
- ```properties
  # 表单登录页
  security.browser.loginPage=/login.html
  # 登录失败跳转页面
  security.browser.failure-url=/login.html
  # 两种登录模式：JSON 与 REDIRECT
  security.browser.loginType=REDIRECT
  # Map 类型：必须配置；设置 uri 的特定后缀对应的跳转登录页, 例如：key=/**: value=/security/login.html。 默认为空
  # 支持通配符，匹配规则： /user/aa/bb/cc.html 匹配 pattern：/us?r/**/*.html, /user/**, /user/*/bb/c?.html, /user/**/*.*
  security.browser.auth-jump-suffix-condition["/*.html"]=/login.html
  security.browser.auth-jump-suffix-condition["/hello"]=/login.html
  security.browser.auth-jump-suffix-condition["/user/**"]=/login.html
  security.browser.auth-jump-suffix-condition["/order/**"]=/login.html
  security.browser.auth-jump-suffix-condition["/file/**"]=/login.html
  # List 类型：设置需要短信校验码认证的 uri，多个 uri 用 “，”号分开，支持通配符，如：/hello,/user/*；默认为空
  #security.code.sms.auth-urls=/authentication/form
  security.code.sms.request-param-sms-code-name=smsCode
  security.code.sms.request-param-mobile-name=mobile
  security.code.sms.expire=120
  security.code.sms.length=6
  security.code.sms.remember-me-seconds=3600
  # List 类型：设置需要图片校验码认证的 uri，多个 uri 用 “，”号分开，支持通配符，如：/hello,/user/*；默认为 /authentication/form
  security.code.image.auth-urls=/authentication/form,/authentication/mobile
  security.code.image.expire=1200
  security.code.image.length=4
  security.code.image.height=60
  security.code.image.width=270
  security.code.image.request-para-height-name=height
  security.code.image.request-para-width-name=width
  security.code.image.request-param-image-code-name=imageCode

## 短信登录
- 在 core 模块
- ```properties
  # 手机验证码登录是否开启, 默认 false
  security.smsCodeLogin.sms-code-login-is-open=true
  # 手机验证码登录请求处理url, 默认 /authentication/mobile
  security.smsCodeLogin.login-processing-url-mobile=/authentication/mobile
  # 提交短信验证码请求时，请求中带的手机号变量名，默认 mobile
  security.smsCodeLogin.request-param-mobile-name=/mobile

## 第三方登录

## SSO

## RBAC