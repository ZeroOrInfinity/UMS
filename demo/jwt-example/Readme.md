# 使用 JWT 注意事项:

1\. 通过 ajax 认证与刷新 jwt 或 `ums.client.login-process-type` 设置 `LoginProcessType.JSON` 通过 form POST 提交认证与刷新 jwt, 
    这样不需要处理因跳转目标 `url` 时处理 `jwt` 与 `refreshToken`. 减少复杂度.

2\. 认证后默认 `JWT` 通过 `header` 返回, `Authorization: bearer xxx.xxx.xxx` . 可以通过 `ums.jwt.bearer` 下的属性配置更改请求头,
    也可以通过 `json` 返回.

3\. 认证后默认 `JWT refresh token` 通过 `header` 返回, `refresh_token: xxxx.xxxx.xxxx` . 可以通过 `ums.jwt.bearer` 下的属性配置更改请求头,
    也可以通过 `json` 返回.
      
4\. 认证成功后请求需要认证 `API` 接口, 默认要求 `JWT` 设置到 `header`, `Authorization: bearer xxx.xxx.xxx` . `可以通过 ums.jwt.bearer` 下
    的属性配置更改 `JWT` 传递方式, 如设置到 `request parameter` 中传递.
    
5\. 需要跨域时, 需配置 `ums.client.cors` 下属性, 注意设置 `JWT` 需要曝露的 `header` 头, 例如: `Access-Control-Expose-Headers: Authorization`.

 