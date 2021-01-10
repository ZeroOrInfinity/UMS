# UMS (user manage scaffolding) 用户管理脚手架: 

![UMS](https://img.shields.io/badge/UMS-2.2.10-green.svg)
![JDK](https://img.shields.io/badge/JDK-1.8+-green.svg)
![Maven](https://img.shields.io/badge/Maven-3.6.3-green.svg)
![MySQL](https://img.shields.io/badge/MySQL-5.7.27-green.svg)
![Redis](https://img.shields.io/badge/Redis-5.0.3-green.svg)
![SpringBoot](https://img.shields.io/badge/SpringBoot-2.3.4-green.svg)
![SpringSecurity](https://img.shields.io/badge/SpringSecurity-5.4.1-green.svg)
![SpringSession](https://img.shields.io/badge/SpringSession-2.3.1-green.svg)
![JustAuth](https://img.shields.io/badge/JustAuth-1.15.9-green.svg)
![license](https://img.shields.io/badge/license-MIT-yellow.svg)

用户管理脚手架集成：用户密码登录、手机登录、支持 JustAuth 支持的所有第三方授权登录、验证码、基于 RBAC 的访问权限控制功能、支持多租户、JWT、SLF4J-MDC、签到等功能.
通过配置文件与实现 **用户服务, 短信发送服务, 获取角色权限服务** 三个 API 接口就可以实现上述功能，实现快速开发，只需要专注于业务逻辑.

User management scaffolding, integration: User password login, mobile login, OAuth2 login(Based on JustAuth), 
Support multi-tenancy, jwt , validate code(image, sms, sliderCode), RBAC, SLF4J-MDC, sign etc...

![ums-arch](doc/ums-arch.png)
------
## 一、`UMS 功能列表(UMS feature list)`：

  - 验证码（图片，短信, 滑块）校验功能(validate code(image, SMS, slider) verification function).
  - 手机登录功能，登录后自动注册, 支持多租户(Mobile login function, automatic registration after login, Support multi-tenancy).
  - 支持所有 JustAuth 支持的第三方授权登录，登录后自动注册 或 绑定 或 创建临时用户([TemporaryUser](https://github.com/ZeroOrInfinity/UMS/blob/master/src/main/java/top/dcenter/ums/security/core/oauth/userdetails/TemporaryUser.java))(OAuth2 login and auto signUp and auto binding).
      - 支持定时刷新 accessToken, 支持分布式定时任务(Support timing refresh accessToken, support distributed timing tasks).
      - 支持第三方授权登录的用户信息表与 token 信息表的缓存功能(Support the caching function of user table and token table by OAuth2 login).
      - 支持第三方绑定(binding)与解绑(unbinding)及查询接口.
  - 访问权限控制功能, 支持多租户(Access control function, support multi tenancy).
  - 简化 session、remember me、csrf、cors等配置(Simplify session、remember me、csrf cors etc configuration).
  - 根据设置的响应方式（JSON 与 REDIRECT）返回 json 或 html 数据.
  - 签到功能(sign).
  - 支持基于 SLF4J MDC 机制的日志链路追踪功能.
  - JWT 创建(通过接口自定义 Claims, 通过配置设置算法等), 校验(通过接口自定义校验规则), 刷新(自动刷新, 直接拒绝, 通过 refreshToken 刷新), 刷新的 JWT 使旧 jwt
   失效引发的并发访问问题及黑名单.
  
### 模块功能 

  | **模块**   | **功能**                                                         |
  | ------ | ------------------------------------------------------------ |
  | [commons](https://github.com/ZeroOrInfinity/UMS/tree/master/commons)   | 通用组件模块 |
  | [ums](https://github.com/ZeroOrInfinity/UMS/tree/master/ums-spring-boot-starter)   | 集成 commons/core/vc/mdc/oauth/rbac/jwt 模块 |
  | [core](https://github.com/ZeroOrInfinity/UMS/tree/master/core)   | 用户名密码登录/手机登录且自动注册/签到/简化HttpSecurity(session、remember me、csrf、跨域等)配置/session redis 缓存/可配置的响应方式(JSON 与 REDIRECT)返回 json 或 html 数据, 集成 jwt/mdc 模块 |
  | [vc](https://github.com/ZeroOrInfinity/UMS/tree/master/vc)   | 验证码, 集成 mdc 模块 |
  | [mdc](https://github.com/ZeroOrInfinity/UMS/tree/master/mdc)   | 基于 SLF4J MDC 机制的日志链路追踪功能 |
  | [oauth](https://github.com/ZeroOrInfinity/UMS/tree/master/oauth)   | OAuth2 login by JustAuth, 集成 jwt/mdc 模块 |
  | [rbac](https://github.com/ZeroOrInfinity/UMS/tree/master/rbac)   | 基于角色的访问权限控制,支持多租户, 集成 mdc 模块 |
  | [jwt](https://github.com/ZeroOrInfinity/UMS/tree/master/jwt)   | JWT 功能, 集成 mdc 模块 |
  | [demo](https://github.com/ZeroOrInfinity/UMS/tree/master/demo)   | basic-example/basic-detail-example/permission-example/quickStart/session-detail-example/validate-codi-example/justAuth-security-oauth2-example/tenant-example/jwt-example |

### demo 演示功能  

  | **demo**                   | **演示功能**                                                     |
  | ---------------------- | ------------------------------------------------------------ |
  | [basic-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/basic-example)         | 基本功能: 最简单的配置                             |
  | [basic-detail-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/basic-detail-example)   | 基本功能详细的配置: 含anonymous/session简单配置/rememberMe/csrf/跨域/登录路由/签到, 不包含session详细配置/验证码/手机登录/权限. |
  | [permission-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/permission-example)     | 基于 RBAC 的权限功能设置                          |
  | [quickStart](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/quickStart)             | 快速开始示例                                                 |
  | [multi-tenancy-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/tenant-example)             | 多租户注册与登录示例                                                 |  
  | [justAuth-security-oauth2-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/justAuth-security-oauth2-example)             | 第三方授权登录详细示例, MDC 日志链路追踪配置                                                |
  | [session-detail-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/session-detail-example) | session 与 session 缓存详细配置                   |
  | [validate-code-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/validate-code-example)  | 基本功能: 验证码(含滑块验证码), 手机登录配置  |
  | [jwt-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/jwt-example)  | JWT 功能示例  |

### [Github 更新日志(UpdatedLog)](https://github.com/ZeroOrInfinity/UMS/blob/master/UpdatedLog.md) | [Gitee 更新日志](https://gitee.com/pcore/UMS/blob/master/UpdatedLog.md)

### [Github 文档地址](https://github.com/ZeroOrInfinity/UMS/wiki) | [Gitee 文档地址](https://gitee.com/pcore/UMS/wikis/pages)

### 微信群：UMS 添加微信(z56133)备注(UMS) 
![weixin](doc/weixin.png)
------
## 二、`maven`：

```xml
<dependency>
    <groupId>top.dcenter</groupId>
    <artifactId>ums-spring-boot-starter</artifactId>
    <version>[2.2.0,)</version>
</dependency>
```
------
## 三、`TODO List`:

- 1\. 拆分微服务, OAuth2 authenticate server

------
## 四、`快速开始(Quick Start)`：

- [Gitee 文档](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926257&doc_id=984605) | [Github 文档](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%9B%9B%E3%80%81%E5%BF%AB%E9%80%9F%E5%BC%80%E5%A7%8B(Quick-Start))
- example: [quickStart](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/quickStart)

------

## 五、接口使用说明(`Interface instructions`):

### 实现对应功能时需要实现的接口(The interface that needs to be implemented when the corresponding function is present)：  
  
1. 用户服务(user service): `必须实现(Must implemente)`
   - [UmsUserDetailsService](https://github.com/ZeroOrInfinity/UMS/blob/master/commons/src/main/java/top/dcenter/ums/security/core/api/service/UmsUserDetailsService.java)    

2. 访问权限控制功能(Access control function): 基于 RBAC 的访问权限控制: 支持多租户与SCOPE
    - [UriAuthorizeService](https://github.com/ZeroOrInfinity/UMS/blob/master/rbac/src/main/java/top/dcenter/ums/security/core/api/permission/service/UriAuthorizeService.java): `推荐通过实现 AbstractUriAuthorizeService 来实现此接口`
    - [AbstractUriAuthorizeService](https://github.com/ZeroOrInfinity/UMS/blob/master/rbac/src/main/java/top/dcenter/ums/security/core/api/permission/service/AbstractUriAuthorizeService.java): `必须实现(Must implemente)`
        
        - uri(资源) 访问权限控制服务接口抽象类, 定义了基于(角色/多租户/SCOPE)的访问权限控制逻辑. 实现 AbstractUriAuthorizeService 抽象类并注入 IOC
         容器即可替换 DefaultUriAuthorizeService.  
        - 注意: 
        
          1\. 推荐实现 AbstractUriAuthorizeService 同时实现 UpdateAndCacheRolesResourcesService 更新与缓存权限服务, 有助于提高授权服务性能. 
          
          2\. 对传入的 Authentication 的 authorities 硬性要求: 
          ```java
           // 此 authorities 可以包含:  [ROLE_A, ROLE_B, ROLE_xxx TENANT_110110, SCOPE_read, SCOPE_write, SCOPE_xxx]
           // authorities 要求:
           //    1. 角色数量    >= 1
           //    2. SCOPE 数量 >= 0
           //    3. 多租户数量  = 1 或 0
           Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
          ```          
          3\. 此框架默认实现 `hasPermission(Authentication, HttpServletRequest)` 
          方法访问权限控制, 通过 `UriAuthoritiesPermissionEvaluator` 实现, 使用此接口的前提条件是: 应用使用的是 restful 风格的 API; 
          如果不是 restful 风格的 API, 请使用 `hasPermission(Authentication, String, String)` 接口的访问权限控制, 
          此接口使用注解的方式 `@PerAuthorize("hasPermission('/users', 'list')")` 来实现, 使用注解需先开启 `@EnableGlobalMethodSecurity(prePostEnabled = true)` 注解.
          
    - [UpdateAndCacheRolesResourcesService](https://github.com/ZeroOrInfinity/UMS/blob/master/rbac/src/main/java/top/dcenter/ums/security/core/api/permission/service/UpdateAndCacheRolesResourcesService.java):    

        - 用于更新或缓存基于(角色/多租户/SCOPE)角色的权限的服务接口, 每次更新角色的 uri(资源)权限时,需要调用此接口, 推荐实现此 RolePermissionsService 接口, 会自动通过 AOP
         方式实现发布 UpdateRolesResourcesEvent 事件, 从而调用 UpdateAndCacheRolesResourcesService 对应的方法.
        - 建议:
         
            1\. 基于 角色 的权限控制: 实现所有角色 uri(资源) 的权限 Map(role, map(uri, Set(permission))) 的更新与缓存本机内存. 
            
            2\. 基于 SCOPE 的权限控制: 情况复杂一点, 但 SCOPE 类型比较少, 也还可以像 1 的方式实现缓存本机内存与更新. 
            
            3\. 基于 多租户 的权限控制: 情况比较复杂, 租户很少的情况下, 也还可以全部缓存在本机内存, 通常情况下全部缓存内存不现实, 只能借助于类似 redis 等的内存缓存.
         
    - [RolePermissionsService](https://github.com/ZeroOrInfinity/UMS/blob/master/rbac/src/main/java/top/dcenter/ums/security/core/api/permission/service/RolePermissionsService.java):    

        - 更新与查询基于(角色/多租户/SCOPE)的角色资源服务接口. 主要用于给角色添加权限的操作. 
        - 注意: 
        
            1\. 在添加资源时, 通过PermissionType.getPermission() 来规范的权限格式, 因为要支持 restful 风格的 Api, 
                在授权时需要对 HttpMethod 与对应的权限进行匹配判断 
            
            2\. 如果实现了 UpdateAndCacheRolesResourcesService 接口, 未实现 RolePermissionsService 接口, 
                修改或添加基于"角色/多租户/SCOPE "的资源权限时一定要调用 UpdateAndCacheRolesResourcesService 对应的方法, 有两种方式: 一种发布事件, 另一种是直接调用对应服务; 
            ```java
            // 1. 推荐用发布事件(异步执行)
            applicationContext.publishEvent(new UpdateRolesResourcesEvent(true, ResourcesType.ROLE));
            applicationContext.publishEvent(new UpdateRolesResourcesEvent(true, ResourcesType.TENANT));
            applicationContext.publishEvent(new UpdateRolesResourcesEvent(true, ResourcesType.SCOPE));
            // 2. 直接调用服务
            // 基于角色
            UpdateAndCacheRolesResourcesService.updateAuthoritiesOfAllRoles();
            // 基于多租户
            UpdateAndCacheRolesResourcesService.updateAuthoritiesOfAllTenant();
            // 基于 SCOPE
            UpdateAndCacheRolesResourcesService.updateAuthoritiesOfAllScopes();
            ```
             
            3\. 实现此 RolePermissionsService 接口, 不需要执行上两种方法的操作, 已通过 AOP 方式实现发布 UpdateRolesResourcesEvent 事件.
            
            4\. 注意: RolePermissionsServiceAspect 切面**生效前提**, 事务的 `Order` 的值**必须 大于 1**, 如果是默认事务(优先级为 Integer.MAX_VALUE
                )不必关心这个值, 如果是自定义事务, 且设置了 Order 的值, 那么值**必须 大于 1**.
                
    - 时序图
    
![时序图](doc/SequenceDiagram/permission.png)  


3. 短信验证码(SMS validate code): `默认空实现`
    - [SmsCodeSender](https://github.com/ZeroOrInfinity/UMS/blob/master/vc/src/main/java/top/dcenter/ums/security/core/api/validate/code/sms/SmsCodeSender.java)

4. 图片验证码(image validate code): 已实现缓存功能, 支持定时刷新缓存功能, 可以自定义缓存验证码图片的输出路径与缓存数量
    - [ImageCodeFactory](https://github.com/ZeroOrInfinity/UMS/blob/master/vc/src/main/java/top/dcenter/ums/security/core/api/validate/code/image/ImageCodeFactory.java)

5. 滑块验证码(Slider validate code): 已实现缓存功能, 支持定时刷新缓存功能, 可以自定义缓存验证码图片的输出路径与缓存数量, 支持自定义源图片路径与模板图片路径(源图片与模板图片参考[validate-code-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/validate-code-example))
    - [SimpleSliderCodeFactory](https://github.com/ZeroOrInfinity/UMS/blob/master/vc/src/main/java/top/dcenter/ums/security/core/api/validate/code/slider/SliderCodeFactory.java) 

6. 自定义验证码(customize validate code):
    - [AbstractValidateCodeProcessor](https://github.com/ZeroOrInfinity/UMS/blob/master/vc/src/main/java/top/dcenter/ums/security/core/api/validate/code/AbstractValidateCodeProcessor.java)
    - [ValidateCodeGenerator](https://github.com/ZeroOrInfinity/UMS/blob/master/vc/src/main/java/top/dcenter/ums/security/core/api/validate/code/ValidateCodeGenerator.java)

7. [Auth2StateCoder](https://github.com/ZeroOrInfinity/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/api/oauth/state/service/Auth2StateCoder.java): `用户需要时实现`, 对第三方授权登录流程中的 state 进行自定义编解码. 可以传递必要的信息, 
     如: 第三方登录成功的跳转地址等 注意此接口的两个方法必须同时实现对应的编解码逻辑, 实现此接口后注入 IOC 容器即可, 如有前端向后端获取 authorizeUrl
     时向后端传递额外参数 且用作注册时的信息, 需配合 UmsUserDetailsService.registerUser(AuthUser, String, String, String) 方法实现.

8. [Auth2UserService](https://github.com/ZeroOrInfinity/UMS/blob/master/oauth/src/main/java/top/dcenter/ums/security/core/api/oauth/service/Auth2UserService.java): 获取第三方用户信息的接口, 一般**不需要用户实现**, 除非想自定义获取第三方用户信息的逻辑, 实现此接口注入 IOC 容器即可替代.

9. [UsersConnectionRepository](https://github.com/ZeroOrInfinity/UMS/blob/master/oauth/src/main/java/top/dcenter/ums/security/core/api/oauth/repository/jdbc/UsersConnectionRepository.java): 第三方授权登录的第三方用户信息增删改查, 绑定与解绑及查询是否绑定与解绑接口, 一般**不需要用户实现**. 
     除非想自定义获取第三方用户信息的逻辑, 实现此接口注入 IOC 容器即可替代.

10. [UsersConnectionTokenRepository](https://github.com/ZeroOrInfinity/UMS/blob/master/oauth/src/main/java/top/dcenter/ums/security/core/api/oauth/repository/jdbc/UsersConnectionTokenRepository.java): 第三方授权登录用户 accessToken 信息表增删改查接口, 一般**不需要用户实现**. 
      除非想自定义获取第三方用户信息的逻辑, 实现此接口注入 IOC 容器即可替代.

11. [ConnectionService](https://github.com/ZeroOrInfinity/UMS/blob/master/oauth/src/main/java/top/dcenter/ums/security/core/api/oauth/signup/ConnectionService.java): 第三方授权登录用户的注册, 绑定, 更新第三方用户信息与 accessToken 信息的接口, 一般**不需要用户实现**.
      除非想自定义获取第三方用户信息的逻辑, 实现此接口注入 IOC 容器即可替代.

12. 自定义 OAuth2 Login 扩展接口: 内置两个自定义 providerId(ums.oauth.customize 与 ums.oauth.gitlabPrivate)
    
    - [AuthGitlabPrivateSource](https://github.com/ZeroOrInfinity/UMS/blob/master/oauth/src/main/java/top/dcenter/ums/security/core/api/oauth/justauth/customize/AuthGitlabPrivateSource.java): 抽象类, 实现此自定义的 AuthGitlabPrivateSource 且注入 ioc 容器的同时, 必须实现 AuthCustomizeRequest , 会自动集成进 OAuth2 Login 逻辑流程中, 只需要像 JustAuth 默认实现的第三方登录一样, 配置相应的属性(ums.oauth.gitlabPrivate.[clientId|clientSecret]等属性)即可.
    
    - [AuthCustomizeSource](https://github.com/ZeroOrInfinity/UMS/blob/master/oauth/src/main/java/top/dcenter/ums/security/core/api/oauth/justauth/customize/AuthCustomizeSource.java): 抽象类, 实现此自定义的 AuthCustomizeSource 且注入 ioc 容器的同时, 必须实现 AuthCustomizeRequest , 会自动集成进 OAuth2 Login 逻辑流程中, 只需要像 JustAuth 默认实现的第三方登录一样, 配置相应的属性(ums.oauth.customize.[clientId|clientSecret]等属性)即可.
    
    - [AuthCustomizeRequest](https://github.com/ZeroOrInfinity/UMS/blob/master/oauth/src/main/java/top/dcenter/ums/security/core/api/oauth/justauth/customize/AuthCustomizeRequest.java): 抽象类, 实现此自定义的 AuthCustomizeRequest 同时, 必须实现 AuthCustomizeSource 或 AuthGitlabPrivateSource 且注入 ioc 容器, 会自动集成进 OAuth2 Login 逻辑流程中, 只需要像 JustAuth 默认实现的第三方登录一样, 配置相应的属性(ums.oauth.customize.[clientId|clientSecret]等属性)即可.

13. 自定义前后端认证 token 以及通信方式

    - [BaseAuthenticationSuccessHandler](https://github.com/ZeroOrInfinity/UMS/blob/master/commons/src/main/java/top/dcenter/ums/security/core/api/authentication/handler/BaseAuthenticationSuccessHandler.java): 认证成功处理器
    
    - [BaseAuthenticationFailureHandler](https://github.com/ZeroOrInfinity/UMS/blob/master/commons/src/main/java/top/dcenter/ums/security/core/api/authentication/handler/BaseAuthenticationFailureHandler.java): 认证失败处理器

14. 如果应用为多租户系统, 且使用 RememberMe 功能: 默认的 RememberMeServices 不支持多租户, 则需要自定义实现 `org.springframework.security.web
.authentication.RememberMeServices` 接口, 使其支持多租户系统, 不过一般情况下多租户系统不使用 RememberMe 功能.

15. [TenantContextHolder](https://github.com/ZeroOrInfinity/UMS/blob/master/commons/src/main/java/top/dcenter/ums/security/core/api/tenant/handler/TenantContextHolder.java):
    - 多租户上下文存储器. 实现此接口并注入 IOC 容器后, 会自动注入 UMS 默认实现的注册/登录/授权组件, 要实现 ums 框架具有多租户功能, 必须实现此接口并注入 IOC 容器.
    
    - 功能: 
    
        1\. `tenantIdHandle(HttpServletRequest, String)`从注册用户入口或登录用户入口提取 `tenantId` 及进行必要的逻辑处理(如: `tenantId` 存入 `ThreadLocal`
        , 或存入 `session`, 或存入 `redis` 缓存等). 
        
        2\. `getTenantId()`方便后续用户注册、登录、授权的数据处理(如：`sql` 添加 `tenantId` 的条件，注册用户添加 `TENANT_tenantId` 权限，根据 `tenantId` 获取角色的权限数据).

        3\. `getTenantId(Authentication)` 默认实现方法, 用户已登录的情况下, 获取租户 ID, 直接从 `authority` 中解析获取.

    - 注意: 
        1\. 多租户系统中, 在未登录时需要用到 tenantId 的接口, 如: `UserCache.getUserFromCache(String)/UmsUserDetailsService` 等接口, 
        可通过 `getTenantId()` 来获取 `tenantId`. 登录用户可以通过 `Authentication` 来获取 `tenantId`.
        
        2\. UMS 默认的登录与注册逻辑中, 都内置了 `TenantContextHolder.tenantIdHandle(HttpServletRequest, String)` 逻辑, 
        用户在实现 `UserCache/UmsUserDetailsService` 等接口中需要 `tenantId` 时, 调用 `TenantContextHolder.getTenantId()` 方法即可.
        
        3\. 如果自定义的注册或登录逻辑, 需要自己先调用 `TenantContextHolder.tenantIdHandle(HttpServletRequest, String)` 逻辑, 再在
        实现 `UserCache/UmsUserDetailsService` 等接口中需要 `tenantId` 时, 调用 `TenantContextHolder.getTenantId()` 方法即可.
        
16. [JobHandler](https://github.com/ZeroOrInfinity/UMS/blob/master/commons/src/main/java/top/dcenter/ums/security/common/api/tasks/handler/JobHandler.java)
    - 任务处理器接口, 继承此接口并注入 IOC 容器, top.dcenter.ums.security.core.tasks.config.ScheduleAutoConfiguration 会自动注册到 ScheduledTaskRegistrar 中.
    
17. JWT 接口请看 [jwt-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/jwt-example).

------
## 六、Configurations:

| **功能(Features)**                                                     | **模块(model)**                                                 | **demo模块--简单配置(Simple Configuration)**                                       | **demo模块--详细配置(detail Configuration)**                                       |
| ------------------------------------------------------------ | -------------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 1. [基本功能](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-1%E3%80%81%E5%9F%BA%E6%9C%AC%E5%8A%9F%E8%83%BD%E9%85%8D%E7%BD%AE) | [core](https://github.com/ZeroOrInfinity/tree/master/core)     | [basic-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/basic-example/src/main/resources/application.yml) |                                                              |
| 2. [登录路由功能](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-2%E3%80%81%E7%99%BB%E5%BD%95%E8%B7%AF%E7%94%B1%E5%8A%9F%E8%83%BD%E9%85%8D%E7%BD%AE) | [core](https://github.com/ZeroOrInfinity/tree/master/core)     |                                                              | [basic-detail-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 3. [session](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-3%E3%80%81Session-%E9%85%8D%E7%BD%AE) | [core](https://github.com/ZeroOrInfinity/tree/master/core)     |                                                              | [session-detail-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/session-detail-example/src/main/resources/application.yml) |
| 4. [remember-me](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-4%E3%80%81Remember-me-%E9%85%8D%E7%BD%AE) | [core](https://github.com/ZeroOrInfinity/tree/master/core)     |                                                              | [basic-detail-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 5. [csrf](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-5%E3%80%81CSRF-%E9%85%8D%E7%BD%AE) | [core](https://github.com/ZeroOrInfinity/tree/master/core)     |                                                              | [basic-detail-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 6. [anonymous](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-6%E3%80%81Anonymous-%E9%85%8D%E7%BD%AE) | [core](https://github.com/ZeroOrInfinity/tree/master/core)     |                                                              | [basic-detail-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 7. [验证码](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-7%E3%80%81%E9%AA%8C%E8%AF%81%E7%A0%81%E5%8A%9F%E8%83%BD%E9%85%8D%E7%BD%AE) | [core](https://github.com/ZeroOrInfinity/tree/master/core)     |                                                              | [validate-code-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/validate-code-example/src/main/resources/application.yml) |
| 8. [手机登录](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-8%E3%80%81%E6%89%8B%E6%9C%BA%E7%99%BB%E5%BD%95%E5%8A%9F%E8%83%BD%E9%85%8D%E7%BD%AE) | [core](https://github.com/ZeroOrInfinity/tree/master/core)     |                                                              | [basic-detail-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 9. [第三方登录](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-9%E3%80%81%E7%AC%AC%E4%B8%89%E6%96%B9%E7%99%BB%E5%BD%95%E5%8A%9F%E8%83%BD%E9%85%8D%E7%BD%AE) | [core](https://github.com/ZeroOrInfinity/tree/master/core) |   | [basic-detail-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 10. [给第三方登录时用的数据库表 user_connection 与 auth_token 添加 redis cache](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-10.-%E7%BB%99%E7%AC%AC%E4%B8%89%E6%96%B9%E7%99%BB%E5%BD%95%E6%97%B6%E7%94%A8%E7%9A%84%E6%95%B0%E6%8D%AE%E5%BA%93%E8%A1%A8-user_connection-%E4%B8%8E-auth_token-%E6%B7%BB%E5%8A%A0-redis-cache) | [core](https://github.com/ZeroOrInfinity/tree/master/core) |                                                              | [basic-detail-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 11. [签到](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-11%E3%80%81%E7%AD%BE%E5%88%B0%E5%8A%9F%E8%83%BD%E9%85%8D%E7%BD%AE) | [core](https://github.com/ZeroOrInfinity/tree/master/core)     |                                                              | [basic-detail-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 12. [基于 RBAC 的访问权限控制功能](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-13%E3%80%81%E5%9F%BA%E4%BA%8E-RBAC-%E7%9A%84%E8%AE%BF%E9%97%AE%E6%9D%83%E9%99%90%E6%8E%A7%E5%88%B6%E5%8A%9F%E8%83%BD%E5%8A%9F%E8%83%BD%E9%85%8D%E7%BD%AE) | [core](https://github.com/ZeroOrInfinity/tree/master/core)     |                                                              | [permission-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/permission-example/src/main/resources/application.yml) |
| 13. [线程池配置](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-14%E3%80%81%E7%BA%BF%E7%A8%8B%E6%B1%A0%E9%85%8D%E7%BD%AE) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [justAuth-security-oauth2-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/justAuth-security-oauth2-example/src/main/resources/application.yml) |
| 14. [基于 SLF4J MDC 机制的日志链路追踪配置](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AD-15%E3%80%81%E5%9F%BA%E4%BA%8E-SLF4J-MDC-%E6%9C%BA%E5%88%B6%E7%9A%84%E6%97%A5%E5%BF%97%E9%93%BE%E8%B7%AF%E8%BF%BD%E8%B8%AA%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [justAuth-security-oauth2-example](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/justAuth-security-oauth2-example/src/main/resources/application.yml) |


------
## 七、[`注意事项(NOTE)`](https://github.com/ZeroOrInfinity/UMS/wiki/%E4%B8%83%E3%80%81%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9): 

### 1\. HttpSecurity 配置问题：UMS 中的 [HttpSecurityAware](https://github.com/ZeroOrInfinity/blob/master/commons/src/main/java/top/dcenter/ums/security/common/api/config/HttpSecurityAware.java) 配置与应用中的 HttpSecurity 配置冲突问题：

1. 如果是新建应用添加 HttpSecurity 配置, 通过下面的接口即可: 
    - [HttpSecurityAware](https://github.com/ZeroOrInfinity/UMS/blob/master/commons/src/main/java/top/dcenter/ums/security/common/api/config/HttpSecurityAware.java)
2. 如果是已存在的应用：
    - 添加 HttpSecurity 配置, 通过下面的接口即可: `HttpSecurityAware`
    - 已有的 HttpSecurity 配置, 让原有的 HttpSecurity 配置实现此接口进行配置: `top.dcenter.security.core.api.config.HttpSecurityAware`

![核心配置逻辑](doc/SequenceDiagram/securityConfigurer.png) 

### 2\. 在 ServletContext 中存储的属性: 

- 属性名称: SecurityConstants.SERVLET_CONTEXT_PERMIT_ALL_SET_KEY
- 属性值: Set<UriHttpMethodTuple>, 把权限类型为 PERMIT_ALL 的 Set 存储在 servletContext .
      
### 4\. 验证码优先级(Verification code Priority): 

- 同一个 uri 由多种验证码同时配置, **优先级**如下:
  `SMS > CUSTOMIZE > SELECTION > TRACK > SLIDER > IMAGE`
  
### 5\. Jackson 序列化与反序列化

- 添加一些 Authentication 与 UserDetails 子类的反序列化器, 以解决 redis 缓存不能反序列化此类型的问题,
具体配置 redis 反序列器的配置请看 [RedisCacheAutoConfiguration.getJackson2JsonRedisSerializer()](https://github.com/ZeroOrInfinity/UMS/blob/master/ums-spring-boot-starter/src/main/java/top/dcenter/ums/security/core/redis/config/RedisCacheAutoConfiguration.java) 方法.

```java
// 示例
Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
ObjectMapper objectMapper = new ObjectMapper();
// Auth2Jackson2Module 为此项目实现的反序列化配置     
objectMapper.registerModules(new CoreJackson2Module(), new WebJackson2Module(), new Auth2Jackson2Module());
jackson2JsonRedisSerializer.setObjectMapper(om);
```
- 注意: [UmsUserDetailsService](https://github.com/ZeroOrInfinity/UMS/blob/master/commons/src/main/java/top/dcenter/ums/security/core/api/service/UmsUserDetailsService.java)
的注册用户方法返回的 `UserDetails` 的默认实现 `User` 已实现反序列化器, 如果是开发者**自定义的子类**, **需开发者自己实现反序列化器**.

------
## 八、[Properties Configurations](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AB%E3%80%81%E5%B1%9E%E6%80%A7%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8)

| **属性配置列表(Properties Configurations)**                                             |
| ------------------------------------------------------------ |
| [基本属性(Basic Properties)](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AB%E3%80%81%E5%B1%9E%E6%80%A7%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8) |
| [签到属性(Sign Properties)](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AB%E3%80%81%E5%B1%9E%E6%80%A7%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8) |
| [手机登录属性(Mobile login Properties)](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AB%E3%80%81%E5%B1%9E%E6%80%A7%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8) |
| [验证码属性(Validate Code Properties)](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AB%E3%80%81%E5%B1%9E%E6%80%A7%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8) |
| [第三方授权登录(OAuth2 JustAuth)](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AB%E3%80%81%E5%B1%9E%E6%80%A7%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8) |
| [线程池属性(ThreadPool Properties)](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AB%E3%80%81%E5%B1%9E%E6%80%A7%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8) |
| [基于 SLF4J MDC 机制的日志链路追踪属性](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AB%E3%80%81%E5%B1%9E%E6%80%A7%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8) |
| [第三方授权登录用户信息数据 redis 缓存配置(UserConnection Redis cache Properties)](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AB%E3%80%81%E5%B1%9E%E6%80%A7%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8) |
| [第三方授权登录用户信息表 user_connection sql 配置(UserConnection sql Properties)](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%85%AB%E3%80%81%E5%B1%9E%E6%80%A7%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8) |
------
## 九、参与贡献(Participate in contribution)

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
------
## 十、[流程图(Flow chart)](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%8D%81%E3%80%81%E6%B5%81%E7%A8%8B%E5%9B%BE:-%E9%9A%8F%E7%9D%80%E7%89%88%E6%9C%AC%E8%BF%AD%E4%BB%A3%E4%BC%9A%E6%9C%89%E5%87%BA%E5%85%A5): 随着版本迭代会有出入

### 登录流程
![登录流程](doc/登录流程.png)

### JWT
![纯jwt流程图](doc/纯jwt流程图.png)
![jwt-session流程图](doc/jwt-session流程图.png)

### 滑块验证码(sliderValidateCode)

![sliderValidateCode](doc/sliderFlow.png)

------
## 十一、[时序图(Sequence Diagram)](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%8D%81%E4%B8%80%E3%80%81%E6%97%B6%E5%BA%8F%E5%9B%BE:-%E9%9A%8F%E7%9D%80%E7%89%88%E6%9C%AC%E8%BF%AD%E4%BB%A3%E4%BC%9A%E6%9C%89%E5%87%BA%E5%85%A5): 随着版本迭代会有出入

| **时序图**                                                   |
| ------------------------------------------------------------ |
| [csrf](doc/SequenceDiagram/crsf.png)                         |
| [获取验证码逻辑](doc/SequenceDiagram/getValidateCode.png)    |
| [图片验证码逻辑](doc/SequenceDiagram/ImageValidateCodeLogin.png) |
| [logout](doc/SequenceDiagram/logout.png)                     |
| [第三方授权登录](doc/SequenceDiagram/OAuth2Login-justAuth.png)        |
| [rememberMe](doc/SequenceDiagram/rememberMe.png)             |
| [核心配置逻辑](doc/SequenceDiagram/securityConfigurer.png)   |
| [登录路由](doc/SequenceDiagram/securityRouter.png)           |
| [session](doc/SequenceDiagram/session.png)                   |
| [手机登录](doc/SequenceDiagram/SmsCodeLogin.png)             |
| [授权逻辑时序图](doc/SequenceDiagram/permission.png)             |
| [过时:第三方绑定与解绑](doc/SequenceDiagram/OAuth2Banding.png)    |
| [过时:第三方授权登录](doc/SequenceDiagram/OAuth2Login.png)        |
| [过时:第三方授权登录注册](doc/SequenceDiagram/OAuth2SignUp.png)   |


## 十二、基于 SLF4J MDC 机制的日志链路追踪功能

- 使用此功能在日志配置文件中的 `pattern` 中添加 `%X{MDC_TRACE_ID}` 即可.
```xml
<!-- 控制台 -->
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <!-- 日志格式 -->
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level ${PID:- } --- [%thread] %X{MDC_TRACE_ID} %logger[%L] - %msg%n</pattern>
        <charset>utf-8</charset>
    </encoder>
    <!--此日志appender是为开发使用，只配置最底级别，控制台输出的日志级别是大于或等于此级别的日志信息-->
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <!-- 只有这个日志权限才能看，sql语句 -->
        <level>DEBUG</level>
    </filter>
</appender>
```
- 自定义 `SLF4J MDC` 机制实现日志链路追踪 id 的类型: 可通过属性 ums.mdc.type 定义:
```yaml
# 基于 SLF4J MDC 机制实现日志链路追踪 id 的类型, 默认为 uuid. 当需要自定义 id 时, type = MdcIdType.CUSTOMIZE_ID, 再实现 MdcIdGenerator.getMdcId() 方法, 注入 IOC 容器即可.
ums:
  mdc:
    type: UUID/THREAD_ID/SESSION_ID/CUSTOMIZE_ID
```
当 ums.mdc.type = CUSTOMIZE_ID 时 需要实现接口 MdcIdGenerator[MdcIdGenerator](https://github.com/ZeroOrInfinity/UMS/blob/master/mdc/src/main/java/top/dcenter/ums/security/core/api/mdc/MdcIdGenerator.java) 并注入 IOC 容器.


- 多线程使用问题: 父线程新建子线程之前调用 `MDC.getCopyOfContextMap()` 方法获取 `MDC context`, 子线程在执行操作前先调用 
`MDC.setContextMap(context)` 方法将父线程的 `MDC context` 设置到子线程中. ThreadPoolTaskExecutor 的配置请参考 [ScheduleAutoConfiguration](https://github.com/ZeroOrInfinity/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/tasks/config/ScheduleAutoConfiguration.java).
- 多线程传递 MDC context 简单示例:  
```java
final Logger log = LoggerFactory.getLogger(this.getClass());
// 获取父线程 MDC 中的内容
final Map<String, String> context = MDC.getCopyOfContextMap();
final Runnable r = () -> {
    log.info("testMDC");
    System.out.println("...");
};
new Thread(() -> {
    // 将父线程的 MDC context 设置到子线程中
    MDC.setContextMap(context);
    r.run();
}, "testMDC").start();
```

## 友情链接
[一个 Spring security 集成 JustAuth 实现第三方授权登录脚手架](https://github.com/justauth/justauth-spring-boot-security-starter)
[一个基于aws s3快速集成多storage client的启动器](https://github.com/opcooc/opcooc-storage-spring-boot-starter)
[justAuth](https://github.com/justauth)

## 鸣谢
##### 感谢 JetBrains 提供的免费开源 License：
![JetBrains](doc/jetBrains.png)