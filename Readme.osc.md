# UMS (user manage scaffolding) 用户管理脚手架: 

![JDK](https://img.shields.io/badge/JDK-1.8+-green.svg)
![Maven](https://img.shields.io/badge/Maven-3.6.3-green.svg)
![MySQL](https://img.shields.io/badge/MySQL-5.7.27-green.svg)
![Redis](https://img.shields.io/badge/Redis-5.0.3-green.svg)
![SpringBoot](https://img.shields.io/badge/SpringBoot-2.3.4-green.svg)
![SpringSecurity](https://img.shields.io/badge/SpringSecurity-5.4-green.svg)
![SpringSession](https://img.shields.io/badge/SpringSession-2.3.1-green.svg)
![license](https://img.shields.io/badge/license-MIT-yellow.svg)

用户管理脚手架集成：用户密码登录、手机登录、支持所有 JustAuth 支持的第三方授权登录、验证码、基于 RBAC 的 uri 访问权限控制功能、签到等功能。
通过配置文件与实现 用户服务, 短信发送服务, 获取角色权限服务 三个 API 接口就可以实现上述功能，实现快速开发，只需要专注于业务逻辑。

![ums-arch](doc/ums-arch.png)
------

## 一、`UMS 功能列表`：

  - 验证码（图片，短信, 滑块）校验功能。
  - 手机登录功能，登录后自动注册。
  - 支持所有 JustAuth 支持的第三方授权登录，登录后自动注册或绑定或创建临时用户([TemporaryUser](https://gitee.com/pcore/UMS/blob/master/src/main/java/top/dcenter/ums/security/core/oauth/userdetails/TemporaryUser.java))。
      - 支持定时刷新 accessToken, 支持分布式定时任务。
      - 支持第三方授权登录的用户信息表与 token 信息表的缓存功能。
      - 支持第三方绑定与解绑及查询接口(top.dcenter.ums.security.core.oauth.repository.UsersConnectionRepository).
  - 访问权限控制功能。
  - 简化 session、remember me、csrf 等配置。
  - 根据设置的响应方式（JSON 与 REDIRECT）返回 json 或 html 数据。
  - 签到功能。
  - 支持基于 SLF4J MDC 机制的日志链路追踪功能
  
### 模块功能 

  | **模块**   | **功能**                                                         |
  | ------ | ------------------------------------------------------------ |
  | [commons](https://gitee.com/pcore/UMS/tree/master/commons)   | 通用组件模块 |
  | [core](https://gitee.com/pcore/UMS/tree/master/core)   | 验证码/用户名密码登录/手机登录且自动注册/OAuth2 login by JustAuth/访问权限控制/签到/简化HttpSecurity(session、remember me、csrf 等)配置/session redis 缓存/可配置的响应方式(JSON 与 REDIRECT)返回 json 或 html 数据 |
  | [demo](https://gitee.com/pcore/UMS/tree/master/demo)   | basic-example/basic-detail-example/permission-example/quickStart/session-detail-example/validate-codi-example/justAuth-security-oauth2-example |
### demo 演示功能 
 
  | **demo**                   | **演示功能**                                                     |
  | ---------------------- | ------------------------------------------------------------ |
  | [basic-example](https://gitee.com/pcore/UMS/tree/master/demo/basic-example)         | core 模块基本功能: 最简单的配置                              |
  | [basic-detail-example](https://gitee.com/pcore/UMS/tree/master/demo/basic-detail-example)   | core 模块基本功能详细的配置: 含anonymous/session简单配置/rememberMe/csrf/登录路由/签到, 不包含session详细配置/验证码/手机登录/权限. |
  | [permission-example](https://gitee.com/pcore/UMS/tree/master/demo/permission-example)     | core 模块: 基于 RBAC 的权限功能设置                          |
  | [quickStart](https://gitee.com/pcore/UMS/tree/master/demo/quickStart)             | 快速开始示例                                                 |
  | [justAuth-security-oauth2-example](https://gitee.com/pcore/UMS/tree/master/demo/justAuth-security-oauth2-example)             | 第三方授权登录, MDC 日志链路追踪配置                                               |
  | [session-detail-example](https://gitee.com/pcore/UMS/tree/master/demo/session-detail-example) | core 模块: session 与 session 缓存详细配置                   |
  | [validate-code-example](https://gitee.com/pcore/UMS/tree/master/demo/validate-code-example)  | core 模块基本功能: 验证码(含自定义滑块验证码), 手机登录配置  |

### [Github 更新日志(UpdatedLog)](https://github.com/ZeroOrInfinity/UMS/blob/master/UpdatedLog.md) | [Gitee 更新日志](https://gitee.com/pcore/UMS/blob/master/UpdatedLog.md)

### [Gitee文档地址](https://gitee.com/pcore/UMS/wikis/pages) | [Github 文档地址](https://github.com/ZeroOrInfinity/UMS/wiki)

微信群：UMS 添加微信(z56133)备注(UMS) 
------
## 二、`maven`：

```xml
<dependency>
    <groupId>top.dcenter</groupId>
    <artifactId>ums-core-spring-boot-starter</artifactId>
    <version>latest</version>
</dependency>
```
------
## 三、`TODO List`:

- 1. 准备基于 spring-security5.4 添加 JWT, OAuth2 authenticate server
- 2. 添加多租户权限控制
------
## 四、`快速开始`：

- [Gitee 文档](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926257&doc_id=984605) | [Github 文档](https://github.com/ZeroOrInfinity/UMS/wiki/%E5%9B%9B%E3%80%81%E5%BF%AB%E9%80%9F%E5%BC%80%E5%A7%8B(Quick-Start))
- example: [quickStart](https://gitee.com/pcore/UMS/tree/master/demo/quickStart)
------
## 五、接口使用说明:

### 实现对应功能时需要实现的接口：   
 
1. 用户服务: `必须实现`
    - [UmsUserDetailsService](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/api/service/UmsUserDetailsService.java)    

2. 图片验证码: 如果不实现就会使用默认图片验证码, 实时产生验证码图片, 没有缓存功能
    - [ImageCodeFactory](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/api/validate/code/image/ImageCodeFactory.java)

3. 短信验证码: `默认空实现`
    - [SmsCodeSender](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/api/validate/code/sms/SmsCodeSender.java)

4. 滑块验证码: 如果不实现就会使用默认滑块验证码, 实时产生验证码图片, 没有缓存功能
    - [SimpleSliderCodeFactory](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/api/validate/code/slider/SliderCodeFactory.java) 

5. 自定义验证码:
    - [AbstractValidateCodeProcessor](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/api/validate/code/AbstractValidateCodeProcessor.java)
    - [ValidateCodeGenerator](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/api/validate/code/ValidateCodeGenerator.java)

6. 访问权限控制功能: 基于 RBAC 的访问权限控制, 增加了更加细粒度的权限控制, 支持 restfulApi; 如: 对菜单与按钮的权限控制
    - [AbstractUriAuthorizeService](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/api/permission/service/AbstractUriAuthorizeService.java):
        - `AbstractUriAuthorizeService` 类中的方法`getRolesAuthorities()`;
          `getRolesAuthorities()`返回值: Map<`role`, Map<`uri`, `UriResourcesDTO`>> 中`UriResourcesDTO`字段 `uri` 
          与 `permission` 必须有值. 
        - 默认实现了 `hasPermission(..)` 表达式, 实现 `AbstractUriAuthorizeService` 即生效,
          1. 默认启用 httpSecurity.authorizeRequests().anyRequest().access("hasPermission(request, authentication)"); 方式. 
          2. 如果开启注解方式( @UriAuthorize 或 @EnableGlobalMethodSecurity(prePostEnabled = true) ): 则通过注解 @PerAuthority
          ("hasPermission('/users/**', '/users/**:list')") 方式生效.

7. [Auth2StateCoder](https://gitee.com/pcore/UMS/blob/master/src/main/java/top/dcenter/ums/security/core/api/oauth/state/service/Auth2StateCoder.java): `用户需要时实现`, 对第三方授权登录流程中的 state 进行自定义编解码. 可以传递必要的信息, 
     如: 第三方登录成功的跳转地址等 注意此接口的两个方法必须同时实现对应的编解码逻辑, 实现此接口后注入 IOC 容器即可, 如有前端向后端获取 authorizeUrl
     时向后端传递额外参数 且用作注册时的信息, 需配合 UmsUserDetailsService.registerUser(AuthUser, String, String, String) 方法实现.

8. [Auth2UserService](https://gitee.com/pcore/UMS/blob/master/src/main/java/top/dcenter/ums/security/core/oauth/service/Auth2UserService.java): 获取第三方用户信息的接口, 一般**不需要用户实现**, 除非想自定义获取第三方用户信息的逻辑, 实现此接口注入 IOC 容器即可替代.

9. [UsersConnectionRepository](https://gitee.com/pcore/UMS/blob/master/src/main/java/top/dcenter/ums/security/core/oauth/repository/UsersConnectionRepository.java): 第三方授权登录的第三方用户信息增删改查, 绑定与解绑及查询是否绑定与解绑接口, 一般**不需要用户实现**. 
     除非想自定义获取第三方用户信息的逻辑, 实现此接口注入 IOC 容器即可替代.

10. [UsersConnectionTokenRepository](https://gitee.com/pcore/UMS/blob/master/src/main/java/top/dcenter/ums/security/core/oauth/repository/UsersConnectionTokenRepository.java): 第三方授权登录用户 accessToken 信息表增删改查接口, 一般**不需要用户实现**. 
      除非想自定义获取第三方用户信息的逻辑, 实现此接口注入 IOC 容器即可替代.

11. [ConnectionService](https://gitee.com/pcore/UMS/blob/master/src/main/java/top/dcenter/ums/security/core/oauth/signup/ConnectionService.java): 第三方授权登录用户的注册, 绑定, 更新第三方用户信息与 accessToken 信息的接口, 一般**不需要用户实现**.
      除非想自定义获取第三方用户信息的逻辑, 实现此接口注入 IOC 容器即可替代.

------
## 六、Configurations:

| **功能**                                                     | **模块**                                                 | **demo模块--简单配置**                                       | **demo模块--详细配置**                                       |
| ------------------------------------------------------------ | -------------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 1. [基本功能](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926340&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core)     | [basic-example](https://gitee.com/pcore/UMS/tree/master/demo/basic-example/src/main/resources/application.yml) |                                                              |
| 2. [登录路由功能](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926354&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [basic-detail-example](https://gitee.com/pcore/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 3. [session](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926355&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [session-detail-example](https://gitee.com/pcore/UMS/tree/master/demo/session-detail-example/src/main/resources/application.yml) |
| 4. [remember-me](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926356&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [basic-detail-example](https://gitee.com/pcore/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 5. [csrf](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926357&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [basic-detail-example](https://gitee.com/pcore/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 6. [anonymous](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926358&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [basic-detail-example](https://gitee.com/pcore/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 7. [验证码](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926360&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [validate-code-example](https://gitee.com/pcore/UMS/tree/master/demo/validate-code-example/src/main/resources/application.yml) |
| 8. [手机登录](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926419&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [basic-detail-example](https://gitee.com/pcore/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 9. [第三方登录](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926435&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core) |  | [basic-detail-example](https://gitee.com/pcore/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 10. [给第三方登录时用的数据库表 user_connection 与 auth_token 添加 redis cache](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2927093&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core) |                                                              | [basic-detail-example](https://gitee.com/pcore/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 11. [签到](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926437&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [basic-detail-example](https://gitee.com/pcore/UMS/tree/master/demo/basic-detail-example/src/main/resources/application.yml) |
| 12. [基于 RBAC 的访问权限控制功能](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926442&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [permission-example](https://gitee.com/pcore/UMS/tree/master/demo/permission-example/src/main/resources/application.yml) |
| 13. [线程池配置](https://gitee.com/pcore/UMS/wikis/pages?sort_id=3014547&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [justAuth-security-oauth2-example](https://gitee.com/pcore/UMS/tree/master/demo/justAuth-security-oauth2-example/src/main/resources/application.yml) |
| 14. [基于 SLF4J MDC 机制的日志链路追踪配置](https://gitee.com/pcore/UMS/wikis/pages?sort_id=3055053&doc_id=984605) | [core](https://gitee.com/pcore/UMS/tree/master/core)     |                                                              | [justAuth-security-oauth2-example](https://gitee.com/pcore/UMS/tree/master/demo/justAuth-security-oauth2-example/src/main/resources/application.yml) |


------

## 七、[`注意事项`](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926456&doc_id=984605):

### 1\. 基于 RBAC 的 uri 访问权限控制

- **更新角色权限时必须调用** [AbstractUriAuthorizeService](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/api/permission/service/AbstractUriAuthorizeService.java)`#updateRolesAuthorities()` 方法来**刷新权限**, 即可实时刷新角色权限.
    - **刷新权限**有两种方式：一种发布事件，另一种是直接调用服务；推荐用发布事件(异步执行)。
        1. 推荐用发布事件(异步执行): `applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true));`
        2. 直接调用服务: `abstractUriAuthorizeService.updateRolesAuthorities();`

### 2\. HttpSecurity 配置问题：UMS 中的 [HttpSecurityAware](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/api/config/HttpSecurityAware.java) 配置与应用中的 HttpSecurity 配置冲突问题：

1. 如果是新建应用添加 HttpSecurity 配置, 通过下面的接口即可:
    - [HttpSecurityAware](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/api/config/HttpSecurityAware.java)
2. 如果是已存在的应用：
    - 添加 HttpSecurity 配置, 通过下面的接口即可: `HttpSecurityAware`
    - 已有的 HttpSecurity 配置, 让原有的 HttpSecurity 配置实现此接口进行配置: `top.dcenter.security.core.api.config.HttpSecurityAware`

### 3\. 在 ServletContext 中存储的属性:

- 属性名称: SecurityConstants.SERVLET_CONTEXT_PERMIT_ALL_SET_KEY
- 属性值: Set<UriHttpMethodTuple>, 把权限类型为 PERMIT_ALL 的 Set 存储在 servletContext .

### 4\. servletContextPath 的值存储在 [MvcUtil](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/util/MvcUtil.java)`.servletContextPath` :

- 通过静态方法获取 `MvcUtil.getServletContextPath()`
- `MvcUtil.servletContextPath` 的值是通过: [SecurityAutoConfiguration](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/config/SecurityAutoConfiguration.java)`#afterPropertiesSet()` 接口注入

### 5\. 验证码优先级:

- 同一个 uri 由多种验证码同时配置, **优先级**如下: `SMS > CUSTOMIZE > SELECTION > TRACK > SLIDER > IMAGE`

### 6\. Jackson 序列化与反序列化

- 添加一些 Authentication 与 UserDetails 子类的反序列化器, 以解决 redis 缓存不能反序列化此类型的问题,
具体配置 redis 反序列器的配置请看 [RedisCacheAutoConfiguration.getJackson2JsonRedisSerializer()](https://gitee.com/pcore/UMS/blob/master/src/main/java/top/dcenter/ums/security/core/oauth/config/RedisCacheAutoConfiguration.java) 方法.

```java
// 示例
Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
ObjectMapper objectMapper = new ObjectMapper();
// Auth2Jackson2Module 为此项目实现的反序列化配置     
objectMapper.registerModules(new CoreJackson2Module(), new WebJackson2Module(), new Auth2Jackson2Module());
jackson2JsonRedisSerializer.setObjectMapper(om);
```
- 注意: [UmsUserDetailsService](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/api/service/UmsUserDetailsService.java)
的注册用户方法返回的 `UserDetails` 的默认实现 `User` 已实现反序列化器, 如果是开发者**自定义的子类**, **需开发者自己实现反序列化器**.

------
## 八、[属性配置列表](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926468&doc_id=984605)
| **属性配置列表**                                             |
| ------------------------------------------------------------ |
| [基本属性](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2927088&doc_id=984605) |
| [签到属性](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2927090&doc_id=984605) |
| [手机登录属性](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2927091&doc_id=984605) |
| [验证码属性](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2927092&doc_id=984605) |
| [第三方授权登录](https://gitee.com/pcore/UMS/wikis/pages?sort_id=3006562&doc_id=984605) |
| [线程池属性](https://gitee.com/pcore/UMS/wikis/pages?sort_id=3006566&doc_id=984605) |
| [基于 SLF4J MDC 机制的日志链路追踪属性](https://gitee.com/pcore/UMS/wikis/pages?sort_id=3054979&doc_id=984605) |
| [第三方授权登录用户信息数据 redis 缓存配置](https://gitee.com/pcore/UMS/wikis/pages?sort_id=3006567&doc_id=984605) |
| [第三方授权登录用户信息表 user_connection sql 配置](https://gitee.com/pcore/UMS/wikis/pages?sort_id=3006568&doc_id=984605) |

------

## 九、参与贡献

1. Fork 本项目
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
------
## 十、[流程图](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926470&doc_id=984605): 随着版本迭代会有出入

### 1. 滑块验证码(sliderValidateCode)

![sliderValidateCode](doc/sliderFlow.png)

------
## 十一、[时序图](https://gitee.com/pcore/UMS/wikis/pages?sort_id=2926484&doc_id=984605): 随着版本迭代会有出入

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
| [权限控制](doc/SequenceDiagram/uriAuthorize.png)             |
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
- 多线程使用问题: 父线程新建子线程之前调用 `MDC.getCopyOfContextMap()` 方法获取 `MDC context`, 子线程在执行操作前先调用 
`MDC.setContextMap(context)` 方法将父线程的 `MDC context` 设置到子线程中. ThreadPoolTaskExecutor 的配置请参考 [ScheduleAutoConfiguration](https://gitee.com/pcore/UMS/blob/master/core/src/main/java/top/dcenter/ums/security/core/oauth/config/ScheduleAutoConfiguration.java).
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