## 必须实现的接口:

1. 用户服务:
    - 有 social 模块时: top.dcenter.security.social.api.service.AbstractSocialUserDetailsService
    - 无 social 模块时: top.dcenter.security.core.api.service.AbstractUserDetailsService    
2. 发送短信验证码:
    - top.dcenter.security.core.api.validate.code.SmsCodeSender
3. ...

## 根据业务选择实现的接口或类:

1. 添加 HttpSecurity 配置, 通过下面的接口即可:
    - top.dcenter.security.core.api.config.HttpSecurityAware