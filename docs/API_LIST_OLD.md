# 老系统（RuoYi）接口列表

> 本文档列出了老系统（RuoYi Java/MyBatis）的所有REST API接口
> 所有接口以controller文件作为分类依据，并且分类按照controller名称升序排列
> 所有列表项按Controller路径的字母升序排列
> 忽略测试接口

## 目录

- [AuthController-认证模块](#authcontroller-认证模块)
- [CacheController-缓存监控](#cachecontroller-缓存监控)
- [CaptchaController-验证码服务](#captchacontroller-验证码服务)
- [FlwCategoryController-工作流-流程分类](#flwcategorycontroller-工作流-流程分类)
- [FlwDefinitionController-工作流-流程定义](#flwdefinitioncontroller-工作流-流程定义)
- [FlwInstanceController-工作流-流程实例](#flwinstancecontroller-工作流-流程实例)
- [FlwSpelController-工作流-流程Spel表达式](#flwspelcontroller-工作流-流程spel表达式)
- [FlwTaskController-工作流-任务管理](#flwtaskcontroller-工作流-任务管理)
- [IndexController-其他接口](#indexcontroller-其他接口)
- [SseController-SSE服务](#ssecontroller-sse服务)
- [SysClientController-客户端管理](#sysclientcontroller-客户端管理)
- [SysConfigController-参数配置管理](#sysconfigcontroller-参数配置管理)
- [SysDeptController-部门管理](#sysdeptcontroller-部门管理)
- [SysDictDataController-字典数据管理](#sysdictdatacontroller-字典数据管理)
- [SysDictTypeController-字典类型管理](#sysdicttypecontroller-字典类型管理)
- [SysLogininforController-登录日志](#syslogininforcontroller-登录日志)
- [SysMenuController-菜单管理](#sysmenucontroller-菜单管理)
- [SysNoticeController-通知公告管理](#sysnoticecontroller-通知公告管理)
- [SysOssConfigController-OSS配置管理](#sysossconfigcontroller-oss配置管理)
- [SysOssController-对象存储管理](#sysosscontroller-对象存储管理)
- [SysOperlogController-操作日志](#sysoperlogcontroller-操作日志)
- [SysPostController-岗位管理](#syspostcontroller-岗位管理)
- [SysProfileController-个人信息](#sysprofilecontroller-个人信息)
- [SysRoleController-角色管理](#sysrolecontroller-角色管理)
- [SysSocialController-社交登录](#syssocialcontroller-社交登录)
- [SysTenantController-租户管理](#systenantcontroller-租户管理)
- [SysTenantPackageController-租户套餐管理](#systenantpackagecontroller-租户套餐管理)
- [SysUserController-用户管理](#sysusercontroller-用户管理)
- [SysUserOnlineController-在线用户](#sysuseronlinecontroller-在线用户)

---

## AuthController-认证模块

**Controller**: `AuthController`

### 基础路径：`/auth`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /auth/login | POST | 用户登录 |
| /auth/logout | POST | 退出登录 |
| /auth/register | POST | 用户注册 |
| /auth/tenant/list | GET | 获取租户列表 |
| /auth/binding/{source} | GET | 获取社交登录跳转URL |
| /auth/social/callback | POST | 社交登录回调绑定 |
| /auth/unlock/{socialId} | DELETE | 取消社交授权 |

---

## CacheController-缓存监控

**Controller**: `CacheController`

### 基础路径：`/monitor/cache`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /monitor/cache | GET | 获取缓存监控信息 |

---

## CaptchaController-验证码服务

**Controller**: `CaptchaController`

### 基础路径：无固定基础路径

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /auth/code | GET | 生成验证码 |
| /resource/sms/code | GET | 短信验证码 |
| /resource/email/code | GET | 邮箱验证码 |

---

## FlwCategoryController-工作流-流程分类

**Controller**: `FlwCategoryController`

### 基础路径：`/workflow/category`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /workflow/category | POST | 新增流程分类 |
| /workflow/category | PUT | 修改流程分类 |
| /workflow/category/{categoryId} | DELETE | 删除流程分类 |
| /workflow/category/{categoryId} | GET | 获取流程分类详细信息 |
| /workflow/category/categoryTree | GET | 获取流程分类树列表 |
| /workflow/category/export | POST | 导出流程分类列表 |
| /workflow/category/list | GET | 查询流程分类列表 |

---

## FlwDefinitionController-工作流-流程定义

**Controller**: `FlwDefinitionController`

### 基础路径：`/workflow/definition`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /workflow/definition | POST | 新增流程定义 |
| /workflow/definition | PUT | 修改流程定义 |
| /workflow/definition/{ids} | DELETE | 删除流程定义 |
| /workflow/definition/{id} | GET | 获取流程定义详细信息 |
| /workflow/definition/active/{id} | PUT | 激活/挂起流程定义 |
| /workflow/definition/copy/{id} | POST | 复制流程定义 |
| /workflow/definition/importDef | POST | 导入流程定义 |
| /workflow/definition/list | GET | 查询流程定义分页列表 |
| /workflow/definition/publish/{id} | PUT | 发布流程定义 |
| /workflow/definition/unPublish/{id} | PUT | 取消发布流程定义 |
| /workflow/definition/unPublishList | GET | 查询未发布的流程定义分页列表 |
| /workflow/definition/xmlString/{id} | GET | 获取流程定义JSON字符串 |
| /workflow/definition/exportDef/{id} | POST | 导出流程定义 |

---

## FlwInstanceController-工作流-流程实例

**Controller**: `FlwInstanceController`

### 基础路径：`/workflow/instance`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /workflow/instance/active/{id} | PUT | 激活/挂起流程实例 |
| /workflow/instance/cancelProcessApply | PUT | 取消流程申请 |
| /workflow/instance/deleteByBusinessIds/{businessIds} | DELETE | 根据业务ID删除流程实例 |
| /workflow/instance/deleteByInstanceIds/{instanceIds} | DELETE | 根据实例ID删除流程实例 |
| /workflow/instance/deleteHisByInstanceIds/{instanceIds} | DELETE | 按照实例id删除已完成的流程实例 |
| /workflow/instance/flowHisTaskList/{businessId} | GET | 获取流程图，流程记录 |
| /workflow/instance/getInfo/{businessId} | GET | 根据业务id查询流程实例详细信息 |
| /workflow/instance/instanceVariable/{instanceId} | GET | 获取流程变量 |
| /workflow/instance/invalid | POST | 作废流程 |
| /workflow/instance/pageByCurrent | GET | 获取当前登陆人发起的流程实例 |
| /workflow/instance/pageByFinish | GET | 查询已结束的流程实例分页列表 |
| /workflow/instance/pageByRunning | GET | 查询正在运行的流程实例分页列表 |
| /workflow/instance/updateVariable | PUT | 修改流程变量 |

---

## FlwSpelController-工作流-流程Spel表达式

**Controller**: `FlwSpelController`

### 基础路径：`/workflow/spel`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /workflow/spel | POST | 新增流程spel表达式定义 |
| /workflow/spel | PUT | 修改流程spel表达式定义 |
| /workflow/spel/{ids} | DELETE | 删除流程spel表达式定义 |
| /workflow/spel/{id} | GET | 获取流程spel表达式定义详细信息 |
| /workflow/spel/list | GET | 查询流程spel表达式定义列表 |

---

## FlwTaskController-工作流-任务管理

**Controller**: `FlwTaskController`

### 基础路径：`/workflow/task`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /workflow/task/backProcess | POST | 驳回审批 |
| /workflow/task/completeTask | POST | 办理任务 |
| /workflow/task/currentTaskAllUser/{taskId} | GET | 获取当前任务的所有办理人 |
| /workflow/task/getBackTaskNode/{taskId}/{nowNodeCode} | GET | 获取可驳回的前置节点 |
| /workflow/task/getNextNodeList | POST | 获取下一节点信息 |
| /workflow/task/getTask/{taskId} | GET | 根据taskId查询代表任务 |
| /workflow/task/pageByAllTaskFinish | GET | 查询已办任务 |
| /workflow/task/pageByAllTaskWait | GET | 查询待办任务 |
| /workflow/task/pageByTaskCopy | GET | 查询当前用户的抄送 |
| /workflow/task/pageByTaskFinish | GET | 查询当前用户的已办任务 |
| /workflow/task/pageByTaskWait | GET | 查询当前用户的待办任务 |
| /workflow/task/startWorkFlow | POST | 启动任务 |
| /workflow/task/taskOperation/{taskOperation} | POST | 任务操作（委派、转办、加签、减签） |
| /workflow/task/terminationTask | POST | 终止任务 |
| /workflow/task/updateAssignee/{userId} | PUT | 修改任务办理人 |
| /workflow/task/urgeTask | POST | 催办任务 |

---

## IndexController-其他接口

**Controller**: `IndexController`

### 基础路径：`/`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| / | GET | 首页提示 |

---

## SseController-SSE服务

**Controller**: `SseController`

### 基础路径：`${sse.path}` (默认: `/sse`)

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| ${sse.path} (默认: /sse) | GET | 建立SSE连接 |
| ${sse.path}/close | GET | 关闭SSE连接 |

---

## SysClientController-客户端管理

**Controller**: `SysClientController`

### 基础路径：`/system/client`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/client | POST | 新增客户端管理 |
| /system/client | PUT | 修改客户端管理 |
| /system/client/{ids} | DELETE | 删除客户端管理 |
| /system/client/{id} | GET | 获取客户端管理详细信息 |
| /system/client/changeStatus | PUT | 修改客户端状态 |
| /system/client/export | POST | 导出客户端管理列表 |
| /system/client/list | GET | 查询客户端管理列表 |

---

## SysConfigController-参数配置管理

**Controller**: `SysConfigController`

### 基础路径：`/system/config`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/config | POST | 新增参数配置 |
| /system/config | PUT | 修改参数配置 |
| /system/config/{configIds} | DELETE | 删除参数配置 |
| /system/config/{configId} | GET | 根据参数编号获取详细信息 |
| /system/config/configKey/{configKey} | GET | 根据参数键名查询参数值 |
| /system/config/export | POST | 导出参数配置列表 |
| /system/config/list | GET | 获取参数配置列表 |
| /system/config/refreshCache | DELETE | 刷新参数缓存 |
| /system/config/updateByKey | PUT | 根据参数键名修改参数配置 |

---

## SysDeptController-部门管理

**Controller**: `SysDeptController`

### 基础路径：`/system/dept`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/dept | POST | 新增部门 |
| /system/dept | PUT | 修改部门 |
| /system/dept/{deptId} | DELETE | 删除部门 |
| /system/dept/{deptId} | GET | 根据部门编号获取详细信息 |
| /system/dept/list | GET | 获取部门列表 |
| /system/dept/list/exclude/{deptId} | GET | 查询部门列表（排除节点） |
| /system/dept/optionselect | GET | 获取部门选择框列表 |

---

## SysDictDataController-字典数据管理

**Controller**: `SysDictDataController`

### 基础路径：`/system/dict/data`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/dict/data | POST | 新增字典数据 |
| /system/dict/data | PUT | 修改字典数据 |
| /system/dict/data/{dictCodes} | DELETE | 删除字典数据 |
| /system/dict/data/{dictCode} | GET | 根据字典数据ID查询详细信息 |
| /system/dict/data/export | POST | 导出字典数据列表 |
| /system/dict/data/list | GET | 查询字典数据列表 |
| /system/dict/data/type/{dictType} | GET | 根据字典类型查询字典数据信息 |

---

## SysDictTypeController-字典类型管理

**Controller**: `SysDictTypeController`

### 基础路径：`/system/dict/type`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/dict/type | POST | 新增字典类型 |
| /system/dict/type | PUT | 修改字典类型 |
| /system/dict/type/{dictIds} | DELETE | 删除字典类型 |
| /system/dict/type/{dictId} | GET | 根据字典类型ID查询详细信息 |
| /system/dict/type/export | POST | 导出字典类型列表 |
| /system/dict/type/list | GET | 查询字典类型列表 |
| /system/dict/type/optionselect | GET | 获取字典选择框列表 |
| /system/dict/type/refreshCache | DELETE | 刷新字典缓存 |

---

## SysLogininforController-登录日志

**Controller**: `SysLogininforController`

### 基础路径：`/monitor/logininfor`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /monitor/logininfor/{infoIds} | DELETE | 删除登录日志 |
| /monitor/logininfor/clean | DELETE | 清空登录日志 |
| /monitor/logininfor/export | POST | 导出登录日志 |
| /monitor/logininfor/list | GET | 获取登录日志列表 |
| /monitor/logininfor/unlock/{userName} | GET | 账户解锁 |

---

## SysMenuController-菜单管理

**Controller**: `SysMenuController`

### 基础路径：`/system/menu`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/menu | POST | 新增菜单 |
| /system/menu | PUT | 修改菜单 |
| /system/menu/{menuId} | DELETE | 删除菜单 |
| /system/menu/{menuId} | GET | 根据菜单编号获取详细信息 |
| /system/menu/cascade/{menuIds} | DELETE | 批量级联删除菜单 |
| /system/menu/getRouters | GET | 获取路由信息 |
| /system/menu/list | GET | 获取菜单列表 |
| /system/menu/roleMenuTreeselect/{roleId} | GET | 加载角色菜单列表树 |
| /system/menu/tenantPackageMenuTreeselect/{packageId} | GET | 获取租户套餐菜单树下拉列表 |
| /system/menu/treeselect | GET | 获取菜单下拉树列表 |

---

## SysNoticeController-通知公告管理

**Controller**: `SysNoticeController`

### 基础路径：`/system/notice`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/notice | POST | 新增通知公告 |
| /system/notice | PUT | 修改通知公告 |
| /system/notice/{noticeIds} | DELETE | 删除通知公告 |
| /system/notice/{noticeId} | GET | 根据通知公告编号获取详细信息 |
| /system/notice/list | GET | 获取通知公告列表 |

---

## SysOssConfigController-OSS配置管理

**Controller**: `SysOssConfigController`

### 基础路径：`/resource/oss/config`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /resource/oss/config | POST | 新增对象存储配置 |
| /resource/oss/config | PUT | 修改对象存储配置 |
| /resource/oss/config/{ossConfigIds} | DELETE | 删除对象存储配置 |
| /resource/oss/config/{ossConfigId} | GET | 获取对象存储配置详细信息 |
| /resource/oss/config/changeStatus | PUT | 修改对象存储配置状态 |
| /resource/oss/config/list | GET | 查询对象存储配置列表 |

---

## SysOssController-对象存储管理

**Controller**: `SysOssController`

### 基础路径：`/resource/oss`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /resource/oss/{ossIds} | DELETE | 删除OSS对象存储 |
| /resource/oss/download/{ossId} | GET | 下载OSS对象 |
| /resource/oss/list | GET | 查询OSS对象存储列表 |
| /resource/oss/listByIds/{ossIds} | GET | 查询OSS对象基于id串 |
| /resource/oss/upload | POST | 上传OSS对象存储 |

---

## SysOperlogController-操作日志

**Controller**: `SysOperlogController`

### 基础路径：`/monitor/operlog`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /monitor/operlog/{operIds} | DELETE | 删除操作日志 |
| /monitor/operlog/clean | DELETE | 清空操作日志 |
| /monitor/operlog/export | POST | 导出操作日志 |
| /monitor/operlog/list | GET | 获取操作日志列表 |

---

## SysPostController-岗位管理

**Controller**: `SysPostController`

### 基础路径：`/system/post`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/post | POST | 新增岗位 |
| /system/post | PUT | 修改岗位 |
| /system/post/{postIds} | DELETE | 删除岗位 |
| /system/post/{postId} | GET | 根据岗位编号获取详细信息 |
| /system/post/deptTree | GET | 获取部门树列表 |
| /system/post/export | POST | 导出岗位列表 |
| /system/post/list | GET | 获取岗位列表 |
| /system/post/optionselect | GET | 获取岗位选择框列表 |

---

## SysProfileController-个人信息

**Controller**: `SysProfileController`

### 基础路径：`/system/user/profile`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/user/profile | GET | 获取个人信息 |
| /system/user/profile | PUT | 修改用户信息 |
| /system/user/profile/avatar | POST | 上传头像 |
| /system/user/profile/updatePwd | PUT | 重置密码 |

---

## SysRoleController-角色管理

**Controller**: `SysRoleController`

### 基础路径：`/system/role`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/role | POST | 新增角色 |
| /system/role | PUT | 修改角色 |
| /system/role/{roleIds} | DELETE | 删除角色 |
| /system/role/{roleId} | GET | 根据角色编号获取详细信息 |
| /system/role/authUser/allocatedList | GET | 查询已分配用户角色列表 |
| /system/role/authUser/unallocatedList | GET | 查询未分配用户角色列表 |
| /system/role/authUser/cancel | PUT | 取消授权用户 |
| /system/role/authUser/cancelAll | PUT | 批量取消授权用户 |
| /system/role/authUser/selectAll | PUT | 批量选择用户授权 |
| /system/role/changeStatus | PUT | 修改角色状态 |
| /system/role/dataScope | PUT | 修改数据权限 |
| /system/role/deptTree/{roleId} | GET | 获取角色部门树列表 |
| /system/role/export | POST | 导出角色列表 |
| /system/role/list | GET | 获取角色列表 |
| /system/role/optionselect | GET | 获取角色选择框列表 |

---

## SysSocialController-社交登录

**Controller**: `SysSocialController`

### 基础路径：`/system/social`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/social/list | GET | 查询社交关系列表 |

---

## SysTenantController-租户管理

**Controller**: `SysTenantController`

### 基础路径：`/system/tenant`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/tenant | POST | 新增租户 |
| /system/tenant | PUT | 修改租户 |
| /system/tenant/{ids} | DELETE | 删除租户 |
| /system/tenant/{id} | GET | 获取租户详细信息 |
| /system/tenant/changeStatus | PUT | 修改租户状态 |
| /system/tenant/dynamic/{tenantId} | GET | 动态切换租户 |
| /system/tenant/dynamic/clear | GET | 清除动态租户 |
| /system/tenant/export | POST | 导出租户列表 |
| /system/tenant/list | GET | 查询租户列表 |
| /system/tenant/syncTenantConfig | GET | 同步租户参数配置 |
| /system/tenant/syncTenantDict | GET | 同步租户字典 |
| /system/tenant/syncTenantPackage | GET | 同步租户套餐 |

---

## SysTenantPackageController-租户套餐管理

**Controller**: `SysTenantPackageController`

### 基础路径：`/system/tenant/package`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/tenant/package | POST | 新增租户套餐 |
| /system/tenant/package | PUT | 修改租户套餐 |
| /system/tenant/package/{packageIds} | DELETE | 删除租户套餐 |
| /system/tenant/package/{packageId} | GET | 获取租户套餐详细信息 |
| /system/tenant/package/changeStatus | PUT | 修改租户套餐状态 |
| /system/tenant/package/export | POST | 导出租户套餐列表 |
| /system/tenant/package/list | GET | 查询租户套餐列表 |
| /system/tenant/package/selectList | GET | 查询租户套餐下拉选列表 |

---

## SysUserController-用户管理

**Controller**: `SysUserController`

### 基础路径：`/system/user`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /system/user | POST | 新增用户 |
| /system/user | GET | 获取用户信息表单（无ID时返回空表单）或根据用户编号获取详细信息 |
| /system/user | PUT | 修改用户 |
| /system/user/{userIds} | DELETE | 删除用户 |
| /system/user/{userId} | GET | 根据用户编号获取详细信息 |
| /system/user/authRole | PUT | 用户授权角色 |
| /system/user/authRole/{userId} | GET | 获取用户授权角色 |
| /system/user/changeStatus | PUT | 修改用户状态 |
| /system/user/deptTree | GET | 获取部门树列表 |
| /system/user/export | POST | 导出用户列表 |
| /system/user/getInfo | GET | 获取当前用户信息 |
| /system/user/importData | POST | 导入用户数据 |
| /system/user/importTemplate | POST | 下载用户导入模板 |
| /system/user/list | GET | 获取用户列表 |
| /system/user/list/dept/{deptId} | GET | 根据部门ID查询用户列表 |
| /system/user/optionselect | GET | 批量获取用户基础信息 |
| /system/user/resetPwd | PUT | 重置密码 |

---

## SysUserOnlineController-在线用户

**Controller**: `SysUserOnlineController`

### 基础路径：`/monitor/online`

| 请求路径 | 请求方式 | 功能 |
|---------|---------|-----|
| /monitor/online | GET | 获取当前用户登录在线设备 |
| /monitor/online/{tokenId} | DELETE | 强退用户 |
| /monitor/online/list | GET | 获取在线用户列表 |
| /monitor/online/myself/{tokenId} | DELETE | 强退当前在线设备 |

---

> 说明：本文档列出了老系统（RuoYi Java/MyBatis）中所有可用的REST API接口，按Controller文件名的字母顺序排列。所有接口均返回标准的JSON格式响应。
> 详细权限要求请参考具体业务逻辑实现。