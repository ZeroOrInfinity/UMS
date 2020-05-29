# 无侵入式模块化权限模型脚手架
> 1. 引入模块依赖：
> 2. 通过 application.yml 或 application.properties 配置:
> 3. 打包项目：mvn clean package -Dmaven.test.skip=true -Pdev

## 验证码功能
- 基本功能：在 core 包中；
- ```properties
  # 表单登录页
  security.browser.loginPage=/login.html
  # 登录失败跳转页面
  security.browser.failure-url=/login.html
  # 两种登录模式：JSON 与 REDIRECT
  security.browser.loginType=REDIRECT
  
  # 数据库名称
  security.browser.database-name=sso-demo
  
  # Map 类型：必须配置；设置 uri 的特定后缀对应的跳转登录页, 例如：key=/**: value=/security/login.html。 默认为空
  # 支持通配符，匹配规则： /user/aa/bb/cc.html 匹配 pattern：/us?r/**/*.html, /user/**, /user/*/bb/c?.html, /user/**/*.*
  security.browser.auth-redirect-suffix-condition["/*.html"]=/login.html
  security.browser.auth-redirect-suffix-condition["/hello"]=/login.html
  security.browser.auth-redirect-suffix-condition["/user/**"]=/login.html
  security.browser.auth-redirect-suffix-condition["/order/**"]=/login.html
  security.browser.auth-redirect-suffix-condition["/file/**"]=/login.html
  # /authentication/form 为用户名密码方式注册,/authentication/social 为第三方登录方式注册,/authentication/mobile 为手机登录注册
  # List 类型：设置需要短信校验码认证的 uri，多个 uri 用 “，”号分开，支持通配符，如：/hello,/user/*；默认为空
  #security.code.sms.auth-urls=/authentication/form,/authentication/social,/authentication/mobile
  security.code.sms.request-param-sms-code-name=smsCode
  security.code.sms.request-param-mobile-name=mobile
  security.code.sms.expire=120
  security.code.sms.length=6
  # 设置记住我功能的 session 的缓存时长，默认 7 天. If a duration suffix is not specified, seconds will be used.
  security.browser.remember-me-timeout=60
  # List 类型：设置需要图片校验码认证的 uri，多个 uri 用 “，”号分开，支持通配符，如：/hello,/user/*；默认为 /authentication/form
  security.code.image.auth-urls=/authentication/form,/authentication/mobile
  security.code.image.expire=1200
  security.code.image.length=4
  security.code.image.height=60
  security.code.image.width=270
  security.code.image.request-para-height-name=height
  security.code.image.request-para-width-name=width
  security.code.image.request-param-image-code-name=imageCode

## 手机登录
- 在 core 模块
- ```properties
  # 手机验证码登录是否开启, 默认 false，
  # 手机验证码登录开启后 必须配置 security.code.sms.auth-urls=/authentication/mobile
  security.smsCodeLogin.sms-code-login-is-open=true
  # 手机验证码登录请求处理url, 默认 /authentication/mobile
  security.smsCodeLogin.login-processing-url-mobile=/authentication/mobile
  # 提交短信验证码请求时，请求中带的手机号变量名，默认 mobile
  security.smsCodeLogin.request-param-mobile-name=/mobile

## 第三方登录 OAuth2
- 在 social 模块
- ```properties
  # social 第三方登录注册功能是否开启，默认为 false
  security.social.social-sign-in-is-open=true
  # 第三方登录时是否自动注册：当为 true 且实现 ConnectionSignUp 接口，则开启自动注册，此时 signUpUrl 失效，否则不会开始自动注册，默认为 true
  security.social.auto-sign-in=false
  # 第三方登录时是否自动注册：当为 true 且实现 ConnectionSignUp 接口，则开启自动注册，此时 signUpUrl 失效，否则不会开始自动注册
  # autoSignIn=true 且实现 ConnectionSignUp 接口则自动注册，此时 signUpUrl 会失效
  # 第三方登录用户授权成功跳转页面，默认为 /signUp.html， 用户必需设置
  security.social.sign-up-url=/signUp.html
  # 第三方登录页面， 默认为 /signIn.html
  security.social.sign-in-url=/signIn.html
  # 第三方登录用户从 signUpUrl 提交的用户信息表单，默认由 /authentication/social 进行处理，由 Social 处理，不需要用户实现
  # security.social.social-user-regist-url=/authentication/social
  # 第三方登录用户授权成功且未注册，则跳转的注册页面时，需要获取的 SocialUserInfo 信息， 默认从 /social/user 获取。
  # 注意：此 url 是 permitAll 权限, 同时修改 signUpUrl 的 ajax 请求 url
  security.social.social-user-info=/social/user
  # 第三方登录用户授权失败跳转页面， 默认为 /signIn.html， 用户必需设置
  security.social.failure-url=/signIn.html
  
  # redirectUrl 直接由 domain/callbackUrl/(security.social.providerId 中的 providerId 组成：如 qq、wechat)组成
  # 第三方登录回调的域名
  security.social.domain=http://www.dcenter.top 
  # 第三方登录回调处理 url ，也是 RedirectUrl 的前缀，默认为 /auth/callback
  # 如果更改此 url，更改后的必须要实现 SocialController#authCallbackRouter(HttpServletRequest) 的功能
  security.social.filter-processes-url=/auth/callback
  
  # ================= 第三方登录 key 与 secret 加密相关 =================
  # 第三方登录用户数据库表的字段 key 与 secret 加密专用密码
  # security.social.textEncryptorPassword = "7ca5d913a17b4942942d16a974e3fecc";
  # 第三方登录用户数据库表的字段 key 与 secret 加密专用密码
  # security.social.textEncryptorSalt = "cd538b1b077542aca5f86942b6507fe2";

  # 自定义 social 表字段
  security.social.table-prefix=social_
  security.social.table-name=social_UserConnection
  security.social.userIdColumnName=userId
  security.social.providerIdColumnName=providerId
  security.social.providerUserIdColumnName=providerUserId
  security.social.rankColumnName=`rank`
  security.social.displayNameColumnName=displayName
  security.social.profileUrlColumnName=profileUrl
  security.social.imageUrlColumnName=imageUrl
  security.social.accessTokenColumnName=accessToken
  security.social.secretColumnName=secret
  security.social.refreshTokenColumnName=refreshToken
  security.social.expireTimeColumnName=expireTime
  # 修改第三方登录用户数据库用户表创建语句时，要注意：修改字段名称可以直接修改上面的字段名称即可，不用修改建表语句，不可以减少字段，但可以另外增加字段。
  # 用户需要对第三方登录的用户表与 curd 的 sql 语句结构进行更改时（curd 语句暂时没开放自定义），
  # 请实现 UsersConnectionRepositoryFactory，可以参考 OAuth2UsersConnectionRepositoryFactory
  # 但 sql 语句中的 %s 必须写上，且 %s 的顺序必须与后面的字段名称所对应的含义对应 : tableName、  userIdColumnName、 providerIdColumnName、
  # providerUserIdColumnName、  rankColumnName、  displayNameColumnName、  profileUrlColumnName、  imageUrlColumnName、  accessTokenColumnName、  secretColumnName、  refreshTokenColumnName、  expireTimeColumnName、  userIdColumnName、  providerIdColumnName、  providerUserIdColumnName、  userIdColumnName、  providerIdColumnName、  rankColumnName
  security.social.creatUserConnectionTableSql=create table %s (%s varchar(255) not null, %s varchar(255) not null, %s varchar(255), %s int not null, %s varchar(255), %s varchar(512), %s varchar(512), %s varchar(512) not null, %s varchar(512), %s varchar(512), %s bigint, primary key (%s, %s, %s), unique index UserConnectionRank(%s, %s, %s));
  
  # autoSignIn=true 且实现 ConnectionSignUp 接口则自动注册，此时 signUpUrl 会失效
  # QQ 登录时是否自动注册，当为 true 且实现 ConnectionSignUp 接口，则开启自动注册。
  # QQ 登录时是否自动注册：当为 true 且实现 ConnectionSignUp 接口，则开启自动注册，此时 signUpUrl 失效，否则不会开始自动注册
  security.social.qq.auto-sign-in=false
  # ConnectionSignUp 非常有用的扩展接口, 调用时机：在第三方服务商回调 redirectUrl 接口时，
  # 在确认数据库用户表(security.social.table-name)中没有用户记录调用且 autoSignIn 为 true时，调用此接口。
  
  
  # 用户设置 appId 后，也是第三方登录的开关，不同 providerId（如qq） 中的 appId 只有在设置值时才开启，默认都关闭
  security.social.qq.app-id=103450626
  security.social.qq.app-secret=dfd68509dfdf580531df64f8dfd
  # 用户设置 appId 后，也是第三方登录的开关，不同 providerId（如qq） 中的 appId 只有在设置值时才开启，默认都关闭
  security.social.wechat.app-id=wxa84cacfdfdff3fdfb
  security.social.wechat.app-secret=45fdffd933acfdbdf71ea5dfdf

## SSO
- 
## RBAC