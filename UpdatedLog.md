## 2.2.30
### Fixes and Improvements:
1. jwt/兼容性: 增加 JwtContext 与 UmsNimbusJwtDecoder 对 nimbus-jose-jwt:9.x.x/8.x.x 的兼容性.


## 2.2.30
### Fixes and Improvements:
1. jwt/兼容性: 增加对 nimbus-jose-jwt:9.x.x/8.x.x 的兼容性.
2. rbac/重构: getRolesByGroup 与 getRolesByGroupOfTenant 接口从 AbstractUriAuthorizeService 移动到 UriAuthorizeService. AbstractUriAuthorizeService 添加 updateAllGroupsOfAllTenant 接口.
3. oauth/优化: 依赖, 删除对 spring-security-oauth2-client 的依赖.

## 2.2.29
### Fixes and Improvements:
1. rbac/新增: 角色组(Group)的概念: 增加更新权限组接口, 更新权限组缓存接口, 更新权限组缓存切面, 更新权限组缓存监听器接口.
2. rbac/改进: AbstractUriAuthorizeService#getUriAuthoritiesOfUser(Authentication) 的业务逻辑. 避免在 Authentication.authorities 中没有 role 时的问题.
3. rbac/重构: 重命名 RolePermissionsService#updateResourcesByScopeId -> updateResourcesByRoleIdOfScopeId, 删除 RolePermissionsService#findAllResourcesByScopeIdOfTenant, 以及 RolePermissionsServiceAspect/UpdateCacheOfRolesResourcesService/UpdateRolesResourcesListener.java 中与其对应的方法重命名与删除.
4. rbac/重构: UpdateCacheOfRolesResourcesService 中 updateAuthoritiesOfAllxxx 三个方法移入 AbstractUriAuthorizeService 中.

## 2.2.28
### Fixes and Improvements:
1. rbac/重构: UpdateCacheOfRolesResourcesService 中 updateAuthoritiesOfAllxxx 三个方法移入 AbstractUriAuthorizeService 中.
2. rbac/优化:
3. ums/优化: 依赖.


## 2.2.27
### Fixes and Improvements:
1. rbac/新增: 权限资源更新与缓存更细化的接口, 支持以单个角色为最小单位的更新缓存; 新增 RolePermissionsServiceAspect.java 针对权限更新的切面接口, 方便发布角色权限更新事件.
2. rbac/改进: 改进权限资源的更新与缓存策略.
3. rbac/commons/迁移常量.
4. oauth/优化: 第三方登录中 state 缓存 key 策略.
5. ums/第三方登录demo/优化: 第三方登录成功获取 token 的流程.
6. demo/示例: 修复因版本更新而启动异常.


## 2.2.26
### Fixes and Improvements:
1. vc/修复: 校验码 redis 缓存设置过期时间问题.
2. vc/修复: 滑块校验码在缓存滑块图片时可能出现的 NPE 问题.
3. vc/修复: 创建验证码图片缓存时, 对于配置存储缓存的目录不存在时, 不能自动创建目录的问题.
4. vc/改进: 优化滑块验证码校验逻辑与 返回的 json 信息.
5. vc/优化: json 序列化的优化.
6. vc/示例: 优化.


## 2.2.25
### Fixes and Improvements:
1. ums/修复: 第三方登录成功时可能获取不到 refresh token 的问题以及 targetUrl 不带 ContextPath 的问题.
2. ums/重构: RedisCache 相关功能类.
3. commons/重命名: MdcScheduledThreadPoolExecutor.
4. oauth/新增: ConnectionService 查询当前账号下的所有绑定的第三方账号接口并实现.
5. mdc/优化.
6. jwt-example/改进.

## 2.2.24
### Fixes and Improvements:
1. core/修复: 手机登录自动注册失效的问题.
2. core/新增: 无效 session 时转发策略, 通过 ums.client.session.forwardOrRedirect = true 设置.
3. jwt/改进: 当在非认证服务器上时, 不在对 jwt 进行是否重新认证的校验.
4. commons/改进: 增加 json2Object(String, TypeReference<T>) 方法.
5. commons/优化: isAjaxOrJson(..) 逻辑.

## 2.2.23
### Fixes and Improvements:
1. oauth/修复: 当本地登录用户为临时登录用户时绑定异常, 当本地登录用户的 Authentication 为 JwtAuthenticationToken 时绑定异常.
2. jwt/添加: AuthenticationToUserDetailsConverter.java 接口并实现此接口.
3. oauth/改进: 添加 expireIn2Timestamp 重载方法.
4. oauth/新增: BindingException.java, 重命名 UnBindingException.java.
5. commons/优化: 接口添加 NonNull 注释标识.

## 2.2.22
### Fixes and Improvements:
1. jwt/兼容性: 增加对 springBoot 高版本的 JwtAuthenticationConverter 兼容性.
2. oauth/改进: ConnectionService.java 针对第三方增加解绑接口并添加默认实现.

## 2.2.21
### Fixes and Improvements:
1. vc/修复: 获取不到手机号的问题.
2. oauth/commons/修复: TemporaryUser 序列化问题.
3. vc-example/修复: 开启 csrf 且通过 json 格式提交表单时, 获取不到 csrfToken 的问题.

## 2.2.20
### Fixes and Improvements:
1. oauth/修复: 第三方登录不支持自动注册且开启 jwt 功能时, 不能跳转 signUpUrl 的问题.
2. jwt/改进: 生成 refreshToken 后响应的方式.
3. vc/改进: 短信验证码校验时增加对手机号的校验.

## 2.2.19
### Fixes and Improvements:
1. ums-oauth/修复: 修复 user_connection 与 auth_token 建表语句的bug.
2. ums/改进: 修复可能发生的 NPE 问题.
3. core/改进: 添加 refreshTokenExpiresIn 字段, 及 json 序列化优化.
4. commons/改进: 添加 json 序列化的支持.
5. ums/改进: 增加对自定义 RedisConnectionFactory 集群与哨兵模式的支持.
6. 优化: isRefreshJwtByRefreshToken() 逻辑.
7. 优化: AuthTokenVo.java, 区分 token 与 refresh token 的过期时间.
8. 优化: 给 nexus-staging-maven-plugin 添加超时设置.


## 2.2.18
### Fixes and Improvements:
1. 修复: 对 RedisConnection 进行操作后未进行关闭的问题.
2. 修复: 解析 jwt 时, getCollectionConverter(..) NPE.
3. 改进: jwt + session 模式, 当需要重新认证时, 增加对应的 redis lock, 避免重复执行删除 userId 用户所有客户端在 redis 的 tokenInfo 的删除动作(含scan).
4. 改进: 更新 TokenKey 格式为: tokenKeyPrefix + userId:jti.
5. 改进: 在 jwt + session 模式, 当用户需要 reAuth 时, 删除此用户所有的登陆 tokenKey 信息.
6. 优化: principalClaimName 注入 JwtContext.
7. 优化: principalClaimName 参数传递问题.
8. 添加: 数据库 schema sql 文件.
9. 优化: ResponseResult 返回成功 code 0 -> 200.


## 2.2.17
### Fixes and Improvements:
1. maven 仓库 v2.2.16 发布异常 重新发版.

## 2.2.16
### Fixes and Improvements:
1. 新增: ums-dependencies 模块, 便于版本管理.
2. 改进: UmsNimbusJwtDecoder 添加从 jwkSetUri 获取 jwk 的缓存功能, 缓存时间与频率通过 JwkSetUriConfig 配置.
3. 改进: 返回访问异常时, 增加 csrf 异常详细提示.


## 2.2.14
### Fixes and Improvements:
1. 添加: 用于从 jwk set uri 获取 JWk 时传递 header 的参数接口.
2. 改进: UmsNimbusJwtDecoder.RestOperationsResourceRetriever 类, 添加可以自定义设置 header 参数的功能.
3. 改进: 增加使 JWT 模块可自定义 RedisConnectionFactory, 通过注入 beanName 为 jwtRedisConnectionFactory 的 RedisConnectionFactory 即可.
4. 优化: 创建 UmsNimbusDecoder 流程.
5. 优化: UmsNimbusJwtDecoder 的字段 reAuthService 设置方式.

## 2.2.13
### Fixes and Improvements:
1. 修复: 初始化 JwtClaimTypeConverterSupplier 的 ConditionalOnMissingBean 的 type 错误.
2. 修复: 解析 Jwt 时, 用户权限解析错误.
3. 其他优化


## 2.2.12
### Fixes and Improvements:
1. 优化: 与 spring cloud: 2020.0.0 和 spring 2.4.x 集成时, 因配置文件的加载方式发送变化, 当使用 spring.factories 加载此类时, 会有如下错误提示: Found WebSecurityConfigurerAdapter as well as SecurityFilterChain. Please select just one 的问题.
2. 兼容性: 增加对 spring-boot:2.4.x 的兼容性.
3. 兼容性: 添加对 nimbus-jose-jwt:9.1.x 的兼容.

## 2.2.11
### Fixes and Improvements:
1. 修复: 当与 feign 模块同时使用时, 因父子容器的问题, 提前触发 ContextRefreshEvent 事件而导致的异常 bug.
2. 修复: 当 ums.jwt.enable=false 时还是加载 jwt 配置问题.
3. 优化: 定时任务. 异常提示信息. 接口重命名.


## 2.2.10
### Fixes and Improvements:
1. 修复: 当不开启 jwt 时, 直接异常的 bug.
2. 增强: 添加 UmsAuthenticationSuccessHandler.java, 增加第三方登录成功后, 获取 token 的方式及相关的配置属性, 使用示例看 justAuth-security-oauth2-example.
3. 优化: 优化成功处理器对 session 属性清除逻辑.
4. 优化: 优化 setBearerTokenAndRefreshTokenToHeader(..) 登录成功后 refreshToken 保存 session 方式, 以便第三方登录获取, 优化 getRefreshTokenKey(userId) 参数名称.
5. 示例: 优化第三方登录示例, 增加第三方成功登录时获取 token 的方式.
6. 优化: swagger.

## 2.2.9
### Fixes and Improvements:
1. 优化: 接口名称 updateUserConnectionAndAuthToken(..).
2. 改进: ConnectionService 接口添加 findConnectionByProviderIdAndProviderUserId(..) 方法, 这样 Auth2LoginAuthenticationProvider 只需引用 ConnectionService, 如需更改第三方用户信息的保存方式, 实现此类即可, 不再需要实现 UsersConnectionRepository 类.


## 2.2.8
### Fixes and Improvements:
1. 改进: 添加是否在启动时检查并自动创建 userConnectionTable 与 authTokenTable 控制开关属性.
2. 改进: 解码 refresh Jwt 时添加黑名单校验.
3. 优化: redis 操作.
4. 文档: 添加流程图.

## 2.2.7
### Fixes and Improvements:
1. 修复: 删除 refreshToken 时, 获取 refreshToken 的 redis key 错误问题.
2. 修复: jwt + session 模式缺少对 jwtString 进行解码时校验逻辑.
3. 接口: 添加 JwtCacheTransformService 接口, 如需缓存自定义对象, 通过实现此接口即可.
4. 添加: 自动 release 到 maven 中心库的插件.
5. 改进: jwt 模式与 jwt + session 模式. 添加 ums.jwt.blacklist.tokenInfoPrefix 属性, 完善 jwt + session 模式.
6. 改进: 添加 UmsBearerTokenResolver 忽略解析jwt 的 urls 字段 ignoreUrls 及相关处理. 添加 UmsBearerTokenResolver 的 ignoreUrls 的自动配置.
7. 优化: JwtCacheTransformService 接口.
8. 优化: getJwtByRequest() 方法; 重复校验 refreshTokenJwt 有效性. 以及其他一些优化.


## 2.2.6
### Fixes and Improvements:
1. 修复: 缓存验证码使用 redis 模式时, 二次校验获取不到缓存 code 的问题.
2. 改进: 增加 getJwtClaimsSetBuilderWithAuthorities(..) 接口, 规范对 Authorities ClaimsSet 的生成.


## 2.2.5
### Fixes and Improvements:
1. 修复: 删除不必要的文件, 放在 git 忽略列表, 上传 maven 时自动打包了, 导致项目启动报错.
2. 修复: 缓存验证码使用 redis 模式时, 获取不到 locationX 字段的问题.
3. 改进: 添加 ums.codes.slider.redundancyValue 属性.校验 SliderCode#getLocationX() 时的允许的差异值: 默认: 3 PX.

## 2.2.4
### Fixes and Improvements:
1. 修复: setOldJwtToBlacklist(..) 比较新旧 JWT 的 userId 时, 获取 userId 不一致的问题.
2. 修复: 拦截异常错乱的问题, 添加 JwtReAuthException 异常拦截.
3. 添加: JwtReAuthException 与相关错误码枚举.
4. 添加: ReAuthService 接口实现 UmsReAuthServiceImpl.
5. 添加: 自定义的 REFRESH_TOKEN_JTI(rJti) ClaimsName
6. 属性: 添加 ums.jwt.blacklist.reAuthPrefix 属性, 是否需要重新登录认证的 redis key 前缀.
7. 接口: isRefresh(..) 接口添加 principalClaimName 参数.
8. 接口: 添加 JWTClaimsSet toClaimsSet(UserDetails userDetails); 接口与实现.
9. 接口: 添加 JWTClaimsSet generateClaimsSet(UserDetails, Jwt); 接口与实现.
10. 接口: 添加接口参数 JWTClaimsSet generateClaimsSet(Authentication, Jwt).
11. 接口: 添加 getTenantId(UserDetails) 与 getTenantId(Collection<? extends GrantedAuthority>) 默认接口.
12. 改进: 优化 UmsNimbusJwtDecoder 实例化, 增加只针对 refreshToken 的 decode 方法, 以提高效率
13. 改进: JwtContext 创建与刷新 Jwt 逻辑, 增加 ReAuthService 的相关方法, 其他的一些优化及一些问题的修复.
14. 优化: JWT 自动续期策略时判断逻辑.
15. 优化: 修改 getJwtGrantedAuthoritiesConverter() 为 getJwtAuthenticationConverter().
16. 其他优化:
 


## 2.2.3
### Fixes and Improvements:
1. 修复: jwtTokenString 因没有去除 bearer 前缀而解析错误的问题.
2. 修复: 通过 refreshToken 刷新 jwt 时, NPE 问题. 去除重复保存到 redis 的语句.
3. 修复: jwt 异常被 FrameworkServlet 拦截的问题.
4. 更新: JustAuth 依赖到 1.15.9 版本.
5. 新增: 飞书, 喜马拉雅, 企业微信网页 第三方登录.
6. 增加: 支付宝内置的代理自定义设置.
7. 升级: facebook api 版本到 9.0.
8. 修改: 原来的企业微信为 企业微信二维码登录.
9. 修改: AuthToken 添加了 refreshTokenExpireIn 字段, 相应的修改数据库操作.
10. 重命名: ums.repository.tableName 为 ums.repository.userConnectionTableName.
11. 新增: auth_token 建表及查询表是否存在的 sql 语句属性(ums.repository.xxx).
12. 新增: 查询数据库名称的 sql 语句属性(ums.repository.queryDatabaseNameSql), 方便根据不同数据库自定义查询语句. 
13. 新增: 添加 JwtIdService 接口
14. 新增: 添加 ums.jwt.alwaysRefresh 属性, 控制通过 refreshToken 刷新 jwt 时, 是否总是返 newJwt.
15. 添加: JwtIdService 接口, 便于自定义 jti 与 refreshToken,
16. 添加: Jwt + session(缓存 redis) 与 jwt 两种模式, 有 ums.jwt.blacklist.enable 属性控制, enable = true 时为 jwt 模式, enable = false 时为 jwt + session 模式.
17. 改进: refreshToken 生成格式: ums.jwt.blacklist.enable 属性, enable = true 时 refreshToken 生成 jwt, enable = false 时 refreshToken 生成 UUID.
18. 添加: GenerateClaimsSetService 添加 getJwtGrantedAuthoritiesConverter 接口.
19. 添加: JwtJackson2Module redis jackson2 序列化模块.
20. 改进: 添加 SaveRefreshTokenException 异常.
21. 优化: 增加 JwtIdServiceAutoConfiguration, 消除循环引用问题.
22. 优化: redis 缓存序列化问题.
23. 优化: 重命名 JwtCacheProperties 为 JwtBlacklistProperties, 添加是否支持 jwt 黑名单的开关属性 enable.
24. 优化: 更新 generateClaimsSet() 逻辑, 由 CustomClaimSetService 的 claim 值覆盖 UmsGenerateClaimsSetServiceImpl.generateClaimsSet() 的 claim 的值.
25. 优化: jwt-example.



## 2.2.2
### Fixes and Improvements:
1. 修复: 补全 AuthToken 与 UserConnection 建表语句, 与刷新 access Token 定时任务处理逻辑相匹配.
2. 新增: ums-spring-boot-starter 模块替换 ums-core-spring-boot-starter 功能, ums-core-spring-boot-starter 模块只拥有用户码登录,手机登录,用户签到, jwt, mdc 功能.
3. 改进: 添加 ums.jwt.exposeRefreshTokenUri 属性, 是否曝露通过 refreshToken 获取 JWT uri 的属性开关.
4. 改进: 根据创建或解析 JWT 的不同场景生成 JwtDecoder.
5. 改进: 添加 jti 黑名单校验, 有触发开关.
6. 改进: 添加获取 JWT 存储 principal 的 claimName 的 getPrincipalClaimName() 接口, 优化一些接口的参数.
7. 优化: 刷新与重置 jwt 的逻辑等.
8. 日志: 优化返回前端的日志.
9. 优化: 重命名 UserInfoJsonVo 为 AuthTokenVo, 优化 AuthenticationSuccessHandler.onAuthenticationSuccess(..) 方法逻辑.
10. 示例: 优化 /me, 未登录时 NPE 问题.
11. 优化: 删除 RepositoryProperties 无用的配置属性.
12. 优化: 移动字段 auth2RedirectUrl 到 BaseAuthenticationSuccessHandler.



## 2.2.1
### Fixes and Improvements:
1. 重构: 从 ums-core 中分离出第三方授权登录功能作为单独的 ums-oauth 模块.
2. 重构: 从 ums-core 模块分离验证码功能作为单独的 ums-vc 模块.
3. 修复: 因未启用 JWT 功能而使 ClientAuthenticationSuccessHandler 发生 NPE 异常的 bug.
4. 改进: ums-commons 模块添加 JobHandler.java 定时任务接口, 让修改验证码图片定时任务和第三方登录刷新 accessToken 定时任务继承此接口.
5. 安全性: 在工具类/Context/Holder 的类上添加 final 字段并把无参构造方法设置为 private.
6. 示例: 添加详细的控制台日志, 以便查看 JWT 流程.
7. 示例: 修复因实现 UmsReAuthServiceImpl 一直返回 true 而导致的示例一直提示 JWT 失效的错误.
8. 日志: 添加统一的异常日志.
9. 日志: 规范面向用户的错误响应, 添加 MDC 链路追踪 ID, 方便定位问题.


## 2.2.0
### Fixes and Improvements:
1. 特性: 添加 Jwt 模块, JWT 创建(通过接口自定义 Claims, 通过配置设置算法等), 校验(通过接口自定义校验规则), 刷新(自动刷新, 直接拒绝, 通过 refreshToken 刷新), 刷新的 JWT
 使旧 JWT 失效引发的并发访问问题及黑名单.
2. 修复: defaultSuccessUrl 设置失效的 bug. 感谢: 帅子男一号.
3. 修复: 开启第三方授权登录时, 如果不配置某些属性会出现 NPE 问题.
4. 增强: mdc 模块在全局异常返回时添加 MDC 链路追踪 ID, 优化: 异常处理器, 返回 json 数据中添加日志链路追踪 ID.
5. 增强: 添加跨域属性(ums.client.cors.xxx)配置功能.
6. 重构: 从 core 模块 分离出 ums-mdc 模块, ums-rbac 模块, 为拆分微服务做准备.
7. 重构: 移动 MvcUtil 中 setRequestMappingUri(..) 与 registerController(..)方法到 ums-commons 模块的 reflectionUtil 中.
8. 重构: 移动 MvcUtil 中 registerDelegateApplicationListener(..) 方法到 ums-commons 模块的 AppContextUtil 中.
9. 重构: core 模块中 UmsUserDetailsService.java 与 UserDetailsRegisterService.java 到 ums-commons 模块.
10. 重构: 移动 AuthenticationUtil 中 isAjaxOrJson(..) 与 responseWithJson(..) 方法到 ums-commons 模块的 JsonUtil 中.
11. 重构: 为了重构 mdc 功能, 移动 MvcUtil 中 getUrlPathHelper() 方法到 ums-commons 模块的 UrlUtil 中.
12. 重构: 移动 TenantContextHolder 等引用到 ums-commons 模块中.
13. 重构: 移动 MvcUtil 中 setRequestMappingUri(..) 与 registerController(..)方法到 ums-commons 模块的 reflectionUtil 中.
14. 添加: rbac 模块 PermissionAdviceHandler.
15. 添加: Jwt 相关对象的 Jackson2 反序列化器, 优化已有的反序列化器.
16. 改进: 添加 MdcUtil.getMdcTraceId() 方法.
17. 改进: 授权异常被 ControllerAdvice 中的异常处理器拦截的问题, 添加 Controller 方法参数校验异常拦截.
18. 改进: JustAuthProperties 中的 scopes 属性格式(providerId:scope), 使其可以针对不同的第三方服务商进行不同的自定义 scope 配置.
19. 改进: 使自定义第三方授权登录可以自定义 providerId.
20. 改进: 添加 alwaysUseDefaultTargetUrl 所需配置.
21. 优化: 添加 ControllerAdvice 的 Order 注解, 并添加常量 Order 的常量, 方便调整 ControllerAdvice 的加载顺序.
22. 优化: 获取 ContextPath 的方式及重复添加 ContextPath 的问题.
23. 示例: 添加 JWT 示例.
24. 示例: 添加跨域功能配置.
25. 等其他改进与优化.



## 2.1.9
### Fixes and Improvements:
1. 特性: 增加多租户处理器接口 TenantContextHolder, 以及相应的配置文件与异常.
2. 增强: 在用户密码登录入口/手机登录入口/第三方登录入口添加提取多租户 ID 的逻辑.
3. 改进: Auth2LoginAuthenticationFilter 添加初始化 details 逻辑, 方便自定义初始化 details.
4. 修复: 当不支持登录路由时, loginUrl 被设置成 logoutUrl 的 bug.
5. 改进: 完善 RememberMe 配置, 使其支持自定义的 RememberMeServices.
6. 示例: 添加多租户示例


## 2.1.8
### Fixes and Improvements:
1. 改进: 增加两个自定义 OAuth2 Login 入口与相对应的属性(ums.oauth.customize 和 ums.oauth.gitlabPrivate). 三个相关的抽象类 AuthCustomizeRequest/AuthCustomizeSource/AuthGitlabPrivateSource.
2. 示例: 增加自定义 OAuth2 Login 示例.
3. 日志: 增加异常日志.

## 2.1.7
### Fixes and Improvements:
1. 修复: 定时任务失效的问题. 主要因为 MdcScheduledThreadPoolTaskExecutor 覆写了 newTaskFor(..) 方法导致, 删除不必要的覆写方法, 简化其他的实现逻辑, 在增加 MDC
 功能的情况下尽量不影响原有方法的实现逻辑, 但是注意: remove(Runnable) 方法在类内部调用有效, 通过实例调用此方法失效.
2. 增强: 基于 SLF4J MDC 机制实现日志链路追踪功能: 增加自定义追踪 ID 属性配置及相应的接口.
3. 改进: 第三方登录入口根据请求类型或接收的类型返回 Json 数据或网页.
4. 重构: 定时任务与相关配置.
5. 优化: 数据库 redis 缓存删除重复设置过期时间语句.
6. 改进: 添加 ums.client.topDomain 属性, 增加对登录成功处理器跳转 url 的校验是否为本应用的域名, 防止跳转到外链.
7. 依赖: 设置springBoot:2.3.4与springSession:2.3.1使其与spring-security5.4.1版本匹配.
8. 优化: 更改 ClientProperties 的个别属性 public 为 private.
9. 示例: 增加第三方登录页面 ajax 入口.


## 2.1.6
### Fixes and Improvements:
1. 添加: 可处理 Json 与 Ajax 的自定义 AccessDeniedHandler 处理器, 自定义403页面可通过属性 ums.client.accessDenyPage 设置.
2. 改进: 优化 UriAuthorizeService 接口, 优化 getUriAuthorityMapOfUser(authentication) 方法获取用户权限的逻辑, 为添加 JWT 功能做准备.
3. 优化: responseWithJson(..)方法的响应逻辑, 对 HttpServletRequest 是否需要返回 json 的判断逻辑.
4. 优化: 删除多余的错误页面属性或配置.
5. 优化: ResponseResult 增加 timestamp 字段.
6. 其他优化.

## 2.1.5
### Fixes and Improvements:
1. 优化: Auth2RequestHolder.getAuth2DefaultRequest(..) 与 Auth2RequestHolder.getProviderIdBySource(..) 方法.
2. 增加: swagger3 API 注释.

## 2.1.4
### Fixes and Improvements:
1. 改进: 通过适配器模式对 AuthDefaultRequest 子类进行适配取代对 AuthDefaultRequest 子类的逐个继承的方式. 因 CSDN 与 FEISHU 不支持第三方授权登录故删除此第三方的支持.
2. 优化: 日志重复记录异常调用链的问题.
3. 文档: 更新权限时序图.


## 2.1.3
### Fixes and Improvements:
1. 增强: 添加统一的异常处理, 此异常处理器可通过继承并注入 IOC 容器替换.
2. 改进: 改进短信验证码与手机登录逻辑, 以及相应的示例更改.
3. 改进: 验证码校验逻辑.
4. 优化: 验证码 redis 缓存参数 StringRedisTemplate 更改为更通用的 RedisConnectionFactory.
5. 示例: 优化滑块验证码显示方式, 更新签到配置文件错误
6. 文档: 更新权限控制时序图.

## 2.1.2
### Fixes and Improvements:
1. 修复: JsonRequestFilter 对 ContentType: application/x-www-form-urlencoded 的格式进行解析后使 request.getParameterMap() 返回值不完整, request
.getParameter(name) 逻辑错误, 有些情况不能正确返回值的 bug.
2. 修复: ajax 提交时未设置 ContentType 为 json 格式, 但提交的数据为 json格式的 bug
3. 修复: 因 AOP 切面的默认优先级与事务的 AOP 默认优先级相同, 在事务未提交就发布事件, 至所有角色更新权限资源不是更新后的权限资源 的bug; 修复后. 此切面生效前提, 事务的 Order 的值必须 大于 1
, 如果默认事务(优先级为 Integer.MAX_VALUE)不必关心这个值, 如果是自定义事务, 如果设置 Order 的值时必须 大于 1.
4. 改进: PermissionType 增加获取所有权限(permission)的集合.
5. 示例: 更新 permission-example.
6. 优化: 更新版本到 2.1.2, 优化内部模块依赖版本用版本表达式, 如: [2.1.0,), 方便维护.
7. 文档: 更新 HttpSecurity 的配置时序图 与 授权流程时序图.
8. 其他一下优化改进.

## 2.1.0
### Fixes and Improvements:
1. 修复: 注释 @ConditionalOnProperty 在配置文件未配置对应属性时失效的 bug; 例如: @ConditionalOnProperty(prefix = "ums.oauth", name = "enabled", havingValue = "true"), 当 ums.oauth.enabled 在配置文件未配置, 但在 Auth2Properties 中所需值默认为 true 时, 配置还是不生效.
   另外对 Auth2Properties 中的 ums.oauth.enabled 属性增加校验, 即用户必须在配置文件显示配置是否支持第三方授权登录.
2. 修复: 定时任务处理器实现定时任务接口的大 bug, 需要抽自己一嘴巴 :(
3. 改进: 考虑到很多应用都有自己的定时任务应用, 提取 Executor 配置放入 executor 包, 从定时任务 RefreshAccessTokenJob 中拆分出 RefreshAccessTokenJobHandler
, RefreshTokenJob 接口的实现已注入 IOC 容器, 方便自定义定时任务接口时调用.
4. 改进: 因 Auth2Properties 增加了 JSR-303 校验, 会对 Auth2Properties 进行多层代理, 所以改进 Auth2RequestHolder 的放射逻辑.
5. 优化: 第三方授权登录获取授权链接时, 如果请求的第三方不在应用支持第三方服务商范围内, 跳转授权失败处理器处理.
6. 优化: Auth2DefaultRequest 获取逻辑
7. 重命名: 重命名 RefreshValidateCodeJob 为 RefreshValidateCodeCacheJob, 见名之意.
8. 依赖: 更新 spring-security:5.4.1, spring-session:2.4.1, spring-boot:2.3.5

## 2.0.8
### Fixes and Improvements:
1. 修复: 某些环境启动时可能提示 Could not resolve placeholder 的 bug. 感谢:振兴(吉他手&键盘手&鼠标手)
2. 修复: 修复未配置滑块验证码时, 自动加载原图片与模板图片出错的问题. 感谢:振兴(吉他手&键盘手&鼠标手)
3. 修复: 示例 Error reading assemblies: Error locating assembly descriptor: assembly.xml 问题.
4. 优化: mdc includeUrls 与 excludeUrls 属性用 Set 集合接收.


## 2.0.7
### Fixes and Improvements:
1. 修复: session 失效后跳转的 url 如果带 queryString 信息时, 缺失 ? 的bug.
2. 修复: 不能对部分通过 Filter 实现的逻辑进行 MDC 日志链路追踪的 bug, 如: 第三方授权登录, 因为 interceptor 拦截在 Filter 之后.
3. 删除: 注解 UriAuthorize 相关功能, 故删除对应的 UriAuthorizeService 的 hasPermission(..) 接口.
4. 特性: 新增 多租户与SCOPE 的权限控制逻辑. 以及优化基于角色的权限控制逻辑.
5. 新增: RolePermissionsService 角色资源服务接口. 主要用于给角色添加权限的操作; 新增 RolePermissionsException
    与 对应的异常处理器, 以及错误响应码; 新增 RolePermissionsServiceAspect 角色权限服务接口切面: 主要功能是基于 角色/多租户/SCOPE 的资源权限更新时, 发布更新角色权限事件. 
6. 新增: UpdateCacheOfRolesResourcesService 权限的更新与缓存服务接口.
7. 新增: 新增 UpdateRolesResourcesType 枚举来区分 角色/多租户/SCOPE 的类型.
8. 改进: 添加 enableRestfulApi 与 restfulAccessExp 属性, 当 enableRestfulApi=false 或者有 @EnableGlobalMethodSecurity 注释时 accessExp
    权限表达式生效; 当 enableRestfulApi=true 时且没有 @EnableGlobalMethodSecurity 注释时 restfulAccessExp
     权限表达式生效; 去除 UriAuthorizeAutoConfigurerAware 的 ConditionalOnMissingBean 条件.
9. 改进: UriAuthorizeService 添加 多租户 与 SCOPE 的角色资源服务接口, 移除不必要的接口 getPermission 与 isUriContainsInUriSet
10. 优化: 提示用户必须实现 AbstractUriAuthorizeService 的方式: 日志提醒改成抛异常.
11. 优化: 从 SecurityAutoConfiguration 中分离出相关的权限配置, 创建单独的权限配置文件 PermissionAutoConfiguration, 移动权限配置类到 permission
 包. 优化: 重命名 PermissionSuffixType 的枚举类名称为 PermissionType, 修改枚举类型使其完全跟 HttpMethod 相同, 使 HttpMethod 与 权限直接一一对应, 以适应 restful 风格的 API, 增加权限描述字段, 方便前端展示.
12. 改进: 图片验证码生成算法, 改变字体为 DejaVu Sans Mono, 使其更容易识别; 优化图片验证码与滑块验证码缓存与定时任务, 使其只有在验证码生效的情况下会执行, 优化从构造方法中去除初始化缓存逻辑, 放入初始化方法.
13. 示例: 更新.


## 2.0.6
### Fixes and Improvements:
1. 特性: 添加定时刷新验证码图片缓存的定时任务功能与相应的属性配置.
2. 增强: 添加图片验证码缓存功能, 支持定时刷新缓存验证码. 在单机上测试: 获取实时验证码: 响应验证码的时间为 15-30 ms 获取缓存验证码: 响应验证码的时间为 1-4   ms.
3. 增强: 添加滑块验证码图片缓存功能, 支持定时刷新缓存验证码图片.
4. 改进: 添加 SLF4J MDC 日志链路追踪的开关属性
5. 改进: 改进滑块验证码生成算法, 可以根据使用者提供的源图片(支持多图)与模板图片(只需白色背景的图片, 支持多图) 生成滑块验证码..
6. 修复: 修复 enableRefreshTokenJob 属性不能控制是否开启定时刷新 accessToken 任务的 bug.
7. 修复: 当 imageCodeFactory 实现自定义逻辑后, 无法创建 ImageCodeGenerator 的 bug.
8. 修复: 不能通过实现 ImageCodeFactory 且注入 IOC 容器后不能替换 DefaultImageCodeFactory 的 bug.
9. 修复: 修复示例中 ValidateCodeCacheType 不能注入的问题.
10. 修复: 第三方授权登录时, 缓存到 redis 时, 设置 state 缓存时间时少个时间单位, 变成 offset错误的 bug. 感谢: 永生的灯塔水母.
11. 优化: permitAllUrls 存储到 servletContext 的格式, 由 Map 改为 Set, 方便调用.
12. 优化: 更改 定时任务的 Executor 属性名称, 使其能应用在其他定时任务上.
13. 优化: 优化 ip 的获取方式. 删除设置图片验证码宽与高的 request 参数属性. 更新与优化验证码示例等.

## 2.0.5
### Fixes and Improvements:
1. 修复: permitUrls 当不带方法后缀时不生效的 bug.
2. 特性: 添加基于 SLF4J MDC 机制的日志链路追踪功能.

## 2.0.4
### Fixes and Improvements:
1. 修复: 生成 userConnectionUpdateExecutor 时 maximumPoolSize 小于 corePoolSize 的 bug. 感谢: 永生的灯塔水母.
2. 修复: AuthStateRedisCache.java containsKey(key) 方法的 bug. 感谢: 永生的灯塔水母.
3. 修复: 成功处理器redirect方式多加ServletContextPath的问题.
4. 增强: 添加在不支持自动注册时, 创建临时用户 TemporaryUser 后跳转 signUpUrl, signUpUrl 可通过属性设置, 再次获取 TemporaryUser 
通过 SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 改进 signUpUrl 的处理方式: 
增加如果 signUpUrl == null 时不跳转, 直接由开发者在成功处理器上自己处理. 添加 signUpUrl 相关的注释,文档, 示例, 增加 signUp.html 提示页面.
5. 优化: 添加了一些 Authentication 与 UserDetails 子类的反序列化器, 以解决 redis 缓存不能反序列化此类型的问题, 添加反序列化配置(Auth2Jackson2Module);
具体配置 redis 反序列器的配置请看 RedisCacheAutoConfiguration.getJackson2JsonRedisSerializer() 方法.
6. 优化: UmsUserDetailsService.generateUsernames(AuthUser authUser) 接口默认实现方法, 便于开发者对与用户命名规则的自定义.
7. 优化: 更改接口 UmsUserDetailsService 的方法名称: existedByUserIds -> existedByUsernames. 更新方法说明. 感谢: 永生的灯塔水母.
8. 其他: 更新 JustAuth 到 1.15.8. 时序图, 更新 example 与 README.
9. 增强: 添加验证码 redis 缓存. issue: #I22JKH


## 2.0.3
### Fixes and Improvements:
1. 去除 JDK11 API, 使用JDK1.8 编译框架, 增强兼容性;
2. 去除 common-collections/commons-langs3依赖;
3. 去除 fastjson API依赖
4. core 模块 lombok 依赖 scope: compile 改为 provided
5. UmsUserDetailsService 接口方法 existedByUserId 改为 existedByUsernames;
6. 删除过时的 example(social);
7. 删除过时的 social 模块;
8. 优化图片验证码生成逻辑;
9. 优化 Auth2XxxRequest 获取逻辑;
10. Auth2RequestHolder 与 AuthStateRedisCache 解耦对 StringRedisTemplate 的引用;
11. 添加新版 OAuth2Login 时序图;
12. 添加账号异常及相应的异常处理器;
13. 改进分布式定时任务日志输出;
14. 提取 api 包中接口引用的 entity 到 api 模块, 提取签到接口到 api 包;
15. 更改 RefreshToken 任务切分逻辑, 更改总记录数为最大 TokenId 值, 避免出现 TokenId 大于总记录数的记录不能更新.
16. 修复 HttpSecurity ACCESSS 权限配置 bug.

## 2.0.2
### Fixes and Improvements:
1. 集成 JustAuth, 支持所有 JustAuth 支持的第三方授权登录，登录后自动注册或绑定。
2. 支持定时刷新 accessToken, 支持分布式定时任务。
3. 支持第三方授权登录的用户信息表与 token 信息表的缓存功能。
4. 支持第三方绑定与解绑及查询接口(top.dcenter.ums.security.core.api.oauth.repository.jdbc.UsersConnectionRepository).
5. 支持线程池配置
6. 添加抑制非法反射警告, 适用于jdk11, 通过属性可以开启与关闭
7. 添加 spring cache 对 @Cacheable 操作异常处理, 缓存穿透处理(TTL随机20%上下浮动), 缓存击穿处理(添加对null值的缓存, 新增与更新时更新null值)
8. 移除 fastJson, 改成 Jackson.
9. 优化返回客户端 Json 字符串时 ObjectMapper 的调用方式.
10. 修复 SpotBugs analysis 扫描出的噪点.
11. 其他优化与改进
12. java 文件添加 LICENSE 信息头.

## 1.2.0
### Fixes and Improvements:
1. 增加权限控制 hasPermission(..) 方法的表达式处理器; 默认启用 httpSecurity.authorizeRequests().anyRequest().access("hasPermission(request, authentication)");
2. 添加 ClientProperties.accessExp="hasPermission(request, authentication)" 配置, 等效于 httpSecurity.authorizeRequests().anyRequest().access("hasPermission(request, authentication)");
3. 更新 ClientProperties.permitUrls 的处理方式, 使其可以解析 uri (带 HttpMethod 后缀; 用:隔开, 例如:/user/**:POST); 兼容旧模式.
4. 删除 UriAuthorizeFilter , 删除权限的过滤器授权模式, 用 hasPermission(request, authentication) 表达式替换.
5. 更新 SecurityCoreAutoConfigurer 中 httpSecurity 的权限配置方式, 使其完全支持 restfulAPI 风格的权限验证模式.
6. 更新与权限相关的配置/注释/文档.
7. 修复滑块验证码验证失败后复用.
8. 更新成功登录跳转逻辑, session失效跳转逻辑.
9. 优化获取 redirectUrl 逻辑.
10. ResponseResult 去除有歧义的静态方法.
11. PasswordEncoder BCryptPasswordEncoder 更改为 DelegatingPasswordEncoder, 但默认加密方式还是 BCryptPasswordEncoder.
12. 重命名 AbstractUserDetailsService 为 UmsUserDetailsService,
重命名 AbstractSocialUserDetailsService 为 UmsSocialUserDetailsService,
并更改抽象类为接口.

## 1.1.5.1
### Fixes and Improvements:
1. 添加更新角色权限异步监听器.
2. 修复 permission-example SysResourcesJpaRepository.findByRoleIdAndUrl() sql语句错误; 优化事务.

## 1.1.5
### Fixes and Improvements:
1. 修复 hasRole(roleArray) 只判断 roleArray[0] 的 bug.
2. 在 HttpSecurityAware 添加 anyRequest().access(EXP) 的 EXP 类型的常量 ACCESS.

## 1.1.4
### Fixes and Improvements:
1. 修复 登录路由未开启的情况下: BaseAuthenticationSuccessHandler 不能自动注册到 IOC 的 bug.
2. 更改属性配置前缀 security -> ums.
3. 添加获取验证码 url 前缀自定义配置.

## 1.1.3-alpha
### Fixes and Improvements:
1. 修复 ClientAuthenticationFialureHandler NPE bug
2. 更新验证码算法， 移动 SliberCodeFactory 到 api 包
3. 更新 README.md, 添加 Readme.osc.md, 使其在gitee时显示 Readme.osc.md 内容，在 Github 时显示 README.md 内容

## 1.1.2-alpha
### Fixes and Improvements: 
1. 修复在响应方式为 redirect 时 session 失效跳转登录后无法跳转到原始 url, 使用登录路由功能时完美解决. 
2. 修复Failurehandler NPE bug. 
3. 根据 callbackUrl/loginRoutingUrl/invalidSessionUrl/sliderCheckUrl 设置的属性动态注入到对应 @RequestMapping 注解上的 value 中.
4. 替换 commons-lang 为 commons-lang3, 更新其他依赖版本. 
5. 添加滑块验证码. 
6. demo模块添加各个功能的 example 示例. 
7. 其他代码的改进.

## 1.1.0-Beta3
### Fixes and Improvements: 
1. 设置统一回调地址时, 添加ServletContextPath. 
2. 优化 session 失效跳转逻辑. 
3. 修复第三方登录自动注册功能失效 bug, 更改注册功能属性名称. 
4. 修复 NPE bug. 
5. img 标签提交俩次的问题. 
6. 修复不能按设置是否自动加载第三方登录配置的 bug. 
7. 优化签到服务. 
8. 优化 authoritySet 转 roleSet 时的不必要的逻辑. 
9. 添加各个功能的 example demo模块. 
10. 更新 README.md."

## 1.1.0-Beta2
### Fixes and Improvements: 
1. 修复手机登录 ajax 提交, 成功登录跳转链接不带 ServletContextPath 问题.
2. 更新 spring-boot/spring-cloud/spring-security 版本.
3. 回调地址路由加解密时字符串与 byte[] 转换添加 charset.
4. 修复认证失败, 获取请求体(当为空) NPE 的 bug.
5. 修复认证成功, 返回跳转 url 不带 ServletContextPath 的 bug.
6. 修复认证失败, 返回 status 错误问题.