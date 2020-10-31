## 2.0.4
### Fixes and Improvements:
1. 修复: 生成 userConnectionUpdateExecutor 时 maximumPoolSize 小于 corePoolSize 的 bug. 感谢: 永生的灯塔水母
2. 修复: AuthStateRedisCache.java containsKey(key) 方法的 bug. 感谢: 永生的灯塔水母
3. 修复: 成功处理器redirect方式多加ServletContextPath的问题.
4. 增强: 添加在不支持自动注册时, 创建临时用户 TemporaryUser 后跳转 signUpUrl, signUpUrl 可通过属性设置, 再次获取 TemporaryUser 
通过 SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 改进 signUpUrl 的处理方式: 
增加如果 signUpUrl == null 时不跳转, 直接由开发者在成功处理器上自己处理. 添加 signUpUrl 相关的注释,文档, 示例, 增加 signUp.html 提示页面.
5. 优化: 添加了一些 Authentication 与 UserDetails 子类的反序列化器, 以解决 redis 缓存不能反序列化此类型的问题, 添加反序列化配置(Auth2Jackson2Module);
具体配置 redis 反序列器的配置请看 RedisCacheAutoConfiguration.getJackson2JsonRedisSerializer() 方法.
6. 优化: UmsUserDetailsService.generateUsernames(AuthUser authUser) 接口默认实现方法, 便于开发者对与用户命名规则的自定义.
7. 优化: 更改接口 UmsUserDetailsService 的方法名称: existedByUserIds -> existedByUsernames. 更新方法说明. 感谢: 永生的灯塔水母
8. 其他: 更新 JustAuth 到 1.15.8. 时序图, 更新 example 与 README.
9. 增强: 添加验证码 redis 缓存

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
4. 支持第三方绑定与解绑及查询接口(top.dcenter.ums.security.core.oauth.repository.UsersConnectionRepository).
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