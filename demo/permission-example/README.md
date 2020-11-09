# permission example
## 添加用户, 默认密码: admin
http://localhost:9090/demo/addUser/13322221111
## 添加权限
http://localhost:9090/demo/addPermissionData/ROLE_ADMIN?uri=/test/permission/*&restfulMethod=post
http://localhost:9090/demo/addPermissionData/ROLE_ADMIN?uri=/test/deny/*&restfulMethod=post
http://localhost:9090/demo/addPermissionData/ROLE_ADMIN?uri=/test/role/admin/*&restfulMethod=get
http://localhost:9090/demo/addPermissionData/ROLE_ADMIN?uri=/test/role/user/*&restfulMethod=get
http://localhost:9090/demo/addPermissionData/ROLE_ADMIN?uri=/test/auth/admin/*&restfulMethod=get
http://localhost:9090/demo/addPermissionData/ROLE_USER?uri=/test/permission/*&restfulMethod=post
http://localhost:9090/demo/addPermissionData/ROLE_USER?uri=/test/role/admin/*&restfulMethod=get
http://localhost:9090/demo/addPermissionData/ROLE_USER?uri=/test/role/user/*&restfulMethod=get
http://localhost:9090/demo/addPermissionData/ROLE_USER?uri=/test/restful/*&restfulMethod=get
http://localhost:9090/demo/addPermissionData/ROLE_USER?uri=/test/restful/*&restfulMethod=post
http://localhost:9090/demo/addPermissionData/ROLE_USER?uri=/test/restful/*&restfulMethod=put
http://localhost:9090/demo/addPermissionData/ROLE_USER?uri=/test/restful/*&restfulMethod=delete
## 删除权限
http://localhost:9090/demo/delPermissionData/ROLE_ADMIN?uri=/test/permission/*&restfulMethod=post
http://localhost:9090/demo/delPermissionData/ROLE_ADMIN?uri=/test/deny/*&restfulMethod=post
http://localhost:9090/demo/delPermissionData/ROLE_ADMIN?uri=/test/role/admin/*&restfulMethod=get
http://localhost:9090/demo/delPermissionData/ROLE_ADMIN?uri=/test/role/user/*&restfulMethod=get
http://localhost:9090/demo/delPermissionData/ROLE_ADMIN?uri=/test/auth/admin/*&restfulMethod=get
http://localhost:9090/demo/delPermissionData/ROLE_USER?uri=/test/permission/*&restfulMethod=post
http://localhost:9090/demo/delPermissionData/ROLE_USER?uri=/test/role/admin/*&restfulMethod=get
http://localhost:9090/demo/delPermissionData/ROLE_USER?uri=/test/role/user/*&restfulMethod=get
## 测试权限控制


### 测试 ClientProperties.accessExp="hasPermission(request, authentication)"
#### 取消 @EnableUriAuthorize 注释, 添加权限
http://localhost:9090/demo/addPermissionData/ROLE_USER?uri=/test/deny/*&restfulMethod=get
#### 访问 url
http://localhost:9090/demo/test/deny/1
http://localhost:9090/demo/test/pass/1