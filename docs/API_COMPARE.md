# FoxDen API 对比文档（新系统 vs 老系统）

> **对比说明**
> - 🟢 **新系统缺少**：老系统有，新系统没有的接口（需要补充）
> - 🔴 **新系统多余**：新系统有，老系统没有的接口（需要确认是否必需）
> - ⚪ **等价匹配**：两个系统都有且功能相同的接口
> - ✅ **确认框**：勾选 `[x]` 表示已确认接口等价

**生成时间**: 2025-02-15
**目的**: 第一阶段迁移，保证新系统和老系统的接口完全等价

---

## 统计概览

| 模块 | 老系统接口数 | 新系统接口数 | 等价接口 | 新系统缺少 | 新系统多余 |
|------|-------------|-------------|---------|-----------|-----------|
| AuthController | 7 | 4 | 4 | 3 | 0 |
| CacheController | 1 | 1 | 1 | 0 | 0 |
| CaptchaController | 3 | 1 | 1 | 2 | 0 |
| FlowCategoryController | 7 | 7 | 7 | 0 | 0 |
| FlowDefinitionController | 13 | 12 | 11 | 2 | 1 |
| FlowInstanceController | 14 | 8 | 7 | 7 | 0 |
| FlowSpelController | 5 | 5 | 5 | 0 | 0 |
| FlowTaskController | 16 | 18 | 15 | 6 | 7 |
| IndexController | 1 | 1 | 1 | 0 | 0 |
| SseController | 2 | 2 | 2 | 0 | 0 |
| SysClientController | 7 | 7 | 7 | 0 | 0 |
| SysConfigController | 9 | 8 | 7 | 1 | 0 |
| SysDeptController | 7 | 7 | 6 | 1 | 1 |
| SysDictDataController | 7 | 8 | 7 | 0 | 1 |
| SysDictTypeController | 8 | 8 | 8 | 0 | 0 |
| SysLogininforController | 5 | 5 | 5 | 0 | 0 |
| SysMenuController | 10 | 10 | 10 | 0 | 0 |
| SysNoticeController | 5 | 5 | 5 | 0 | 0 |
| SysOssConfigController | 6 | 6 | 6 | 0 | 0 |
| SysOssController | 5 | 5 | 5 | 0 | 0 |
| SysOperLogController | 4 | 5 | 4 | 0 | 1 |
| SysPostController | 8 | 8 | 8 | 0 | 0 |
| SysProfileController | 4 | 4 | 4 | 0 | 0 |
| SysRoleController | 14 | 14 | 14 | 0 | 0 |
| SysSocialController | 1 | 1 | 1 | 0 | 0 |
| SysTenantController | 11 | 11 | 11 | 0 | 0 |
| SysTenantPackageController | 8 | 8 | 8 | 0 | 0 |
| SysUserOnlineController | 5 | 2 | 2 | 3 | 0 |
| SysUserController | 16 | 16 | 16 | 0 | 0 |
| **总计** | **194** | **183** | **171** | **26** | **12** |

---

## 详细对比

### AuthController-认证模块

**基础路径**: `/auth`

- [ ] | /auth/login | POST | 用户登录 | ⚪ 等价
- [ ] | /auth/logout | POST | 退出登录 | ⚪ 等价
- [ ] | /auth/register | POST | 用户注册 | ⚪ 等价
- [ ] | /auth/tenant/list | GET | 获取租户列表 | ⚪ 等价
- [ ] | /auth/binding/{source} | GET | 获取社交登录跳转URL | 🟢 新系统缺少
- [ ] | /auth/social/callback | POST | 社交登录回调绑定 | 🟢 新系统缺少
- [ ] | /auth/unlock/{socialId} | DELETE | 取消社交授权 | 🟢 新系统缺少

**差异说明**: 新系统缺少3个社交登录相关接口，需要补充。

---

### CacheController-缓存监控

**基础路径**: `/monitor/cache`

- [ ] | /monitor/cache | GET | 获取缓存监控信息 | ⚪ 等价

**差异说明**: 无差异。

---

### CaptchaController-验证码服务

**基础路径**: `/auth/code`

- [ ] | /auth/code | GET | 生成验证码 | ⚪ 等价
- [ ] | /resource/sms/code | GET | 短信验证码 | 🟢 新系统缺少
- [ ] | /resource/email/code | GET | 邮箱验证码 | 🟢 新系统缺少

**差异说明**: 新系统缺少2个验证码接口（短信验证码和邮箱验证码）。

---

### FlowCategoryController-工作流-流程分类

**基础路径**: `/workflow/category`

- [ ] | /workflow/category | POST | 新增流程分类 | ⚪ 等价
- [ ] | /workflow/category | PUT | 修改流程分类 | ⚪ 等价
- [ ] | /workflow/category/{categoryId} | DELETE | 删除流程分类 | ⚪ 等价
- [ ] | /workflow/category/{categoryId} | GET | 获取流程分类详细信息 | ⚪ 等价
- [ ] | /workflow/category/categoryTree | GET | 获取流程分类树列表 | ⚪ 等价
- [ ] | /workflow/category/export | POST | 导出流程分类列表 | ⚪ 等价
- [ ] | /workflow/category/list | GET | 查询流程分类列表 | ⚪ 等价

**差异说明**: 无差异。

---

### FlowDefinitionController-工作流-流程定义

**基础路径**: `/workflow/definition`

- [ ] | /workflow/definition | POST | 新增流程定义 | ⚪ 等价
- [ ] | /workflow/definition | PUT | 修改流程定义 | ⚪ 等价
- [ ] | /workflow/definition/{ids} | DELETE | 删除流程定义 | ⚪ 等价
- [ ] | /workflow/definition/{id} | GET | 获取流程定义详细信息 | ⚪ 等价
- [ ] | /workflow/active/{id} | PUT | 激活/挂起流程定义 | 🟢 新系统缺少
- [ ] | /workflow/copy/{id} | POST | 复制流程定义 | ⚪ 等价
- [ ] | /workflow/definition/copy/{id} | POST | 复制流程定义 | 🔴 新系统多余（路径不同）
- [ ] | /workflow/importDef | POST | 导入流程定义 | 🟢 新系统缺少
- [ ] | /workflow/list | GET | 查询流程定义分页列表 | ⚪ 等价
- [ ] | /workflow/publish/{id} | PUT | 发布流程定义 | ⚪ 等价
- [ ] | /workflow/unPublish/{id} | PUT | 取消发布流程定义 | ⚪ 等价
- [ ] | /workflow/unPublishList | GET | 查询未发布的流程定义分页列表 | ⚪ 等价
- [ ] | /workflow/xmlString/{id} | GET | 获取流程定义JSON字符串 | 🟢 新系统缺少
- [ ] | /workflow/exportDef/{id} | POST | 导出流程定义 | 🟢 新系统缺少
- [ ] | /workflow/definition/export/{id} | GET | 导出流程定义 | 🔴 新系统多余（路径和方式不同）

**差异说明**:
1. 新系统缺少 `/workflow/active/{id}`、`/workflow/importDef`、`/workflow/xmlString/{id}`、`/workflow/exportDef/{id}`
2. 新系统的 `copy` 和 `export` 接口路径与老系统不同

---

### FlowInstanceController-工作流-流程实例

**基础路径**: `/workflow/instance`

- [ ] | /workflow/instance/{instanceId} | GET | 获取流程实例详细信息 | ⚪ 等价
- [ ] | /workflow/instance/{businessId} | GET | 根据业务id查询流程实例详细信息 | 🟢 新系统缺少
- [ ] | /workflow/instance/active/{instanceId} | POST | 激活流程实例 | ⚪ 等价
- [ ] | /workflow/instance/active/{id} | PUT | 激活/挂起流程实例 | 🟢 新系统缺少
- [ ] | /workflow/instance/cancelProcessApply | POST | 取消流程申请 | ⚪ 等价
- [ ] | /workflow/instance/cancelProcessApply | PUT | 取消流程申请 | 🟢 新系统缺少（老系统是PUT）
- [ ] | /workflow/instance/deleteByBusinessIds | DELETE | 根据业务ID删除流程实例 | ⚪ 等价
- [ ] | /workflow/instance/deleteByBusinessIds/{businessIds} | DELETE | 根据业务ID删除流程实例 | 🟢 新系统缺少（老系统有路径参数）
- [ ] | /workflow/instance/deleteByInstanceIds | DELETE | 根据实例ID删除流程实例 | ⚪ 等价
- [ ] | /workflow/instance/deleteByInstanceIds/{instanceIds} | DELETE | 根据实例ID删除流程实例 | 🟢 新系统缺少（老系统有路径参数）
- [ ] | /workflow/instance/deleteHisByInstanceIds/{instanceIds} | DELETE | 按照实例id删除已完成的流程实例 | 🟢 新系统缺少
- [ ] | /workflow/instance/flowHisTaskList/{businessId} | GET | 获取流程图，流程记录 | 🟢 新系统缺少
- [ ] | /workflow/instance/instanceVariable/{instanceId} | GET | 获取流程变量 | 🟢 新系统缺少
- [ ] | /workflow/instance/invalid | POST | 作废流程 | 🟢 新系统缺少
- [ ] | /workflow/instance/pageByCurrent | GET | 获取当前登陆人发起的流程实例 | ⚪ 等价
- [ ] | /workflow/instance/pageByFinish | GET | 查询已结束的流程实例分页列表 | ⚪ 等价
- [ ] | /workflow/instance/pageByRunning | GET | 查询正在运行的流程实例分页列表 | ⚪ 等价
- [ ] | /workflow/instance/updateVariable | PUT | 修改流程变量 | 🟢 新系统缺少

**差异说明**: 新系统缺少7个接口，主要是流程实例的详细查询、变量管理和历史数据删除功能。

---

### FlowSpelController-工作流-流程Spel表达式

**基础路径**: `/workflow/spel`

- [ ] | /workflow/spel | POST | 新增流程spel表达式定义 | ⚪ 等价
- [ ] | /workflow/spel | PUT | 修改流程spel表达式定义 | ⚪ 等价
- [ ] | /workflow/spel/{ids} | DELETE | 删除流程spel表达式定义 | ⚪ 等价
- [ ] | /workflow/spel/{id} | GET | 获取流程spel表达式定义详细信息 | ⚪ 等价
- [ ] | /workflow/spel/list | GET | 查询流程spel表达式定义列表 | ⚪ 等价

**差异说明**: 无差异。

---

### FlowTaskController-工作流-任务管理

**基础路径**: `/workflow/task`

- [ ] | /workflow/task/{taskId} | GET | 获取任务详细信息 | ⚪ 等价
- [ ] | /workflow/task/backProcess | POST | 驳回审批 | ⚪ 等价
- [ ] | /workflow/task/completeTask | POST | 办理任务 | ⚪ 等价
- [ ] | /workflow/task/currentTaskAllUser/{taskId} | GET | 获取当前任务的所有办理人 | 🟢 新系统缺少
- [ ] | /workflow/task/getBackTaskNode/{taskId}/{nowNodeCode} | GET | 获取可驳回的前置节点 | 🟢 新系统缺少
- [ ] | /workflow/task/getNextNodeList | POST | 获取下一节点信息 | ⚪ 等价
- [ ] | /workflow/task/getTask/{taskId} | GET | 根据taskId查询代表任务 | 🟢 新系统缺少
- [ ] | /workflow/task/pageByAllTaskFinish | GET | 查询已办任务 | ⚪ 等价
- [ ] | /workflow/task/pageByAllTaskWait | GET | 查询待办任务 | ⚪ 等价
- [ ] | /workflow/task/pageByTaskCopy | GET | 查询当前用户的抄送 | ⚪ 等价
- [ ] | /workflow/task/pageByTaskFinish | GET | 查询当前用户的已办任务 | ⚪ 等价
- [ ] | /workflow/task/pageByTaskWait | GET | 查询当前用户的待办任务 | ⚪ 等价
- [ ] | /workflow/task/startWorkFlow | POST | 启动任务 | ⚪ 等价
- [ ] | /workflow/task/taskOperation/{taskOperation} | POST | 任务操作（委派、转办、加签、减签） | 🟢 新系统缺少
- [ ] | /workflow/task/terminationTask | POST | 终止任务 | ⚪ 等价
- [ ] | /workflow/task/updateAssignee/{userId} | PUT | 修改任务办理人 | 🟢 新系统缺少
- [ ] | /workflow/task/urgeTask | POST | 催办任务 | 🟢 新系统缺少
- [ ] | /workflow/task/addSignature | POST | 加签 | 🔴 新系统多余（拆分出来的接口）
- [ ] | /workflow/task/cancelProcess | POST | 取消流程 | 🔴 新系统多余（拆分出来的接口）
- [ ] | /workflow/task/delegateTask | POST | 委派任务 | 🔴 新系统多余（拆分出来的接口）
- [ ] | /workflow/task/invalidProcess | POST | 作废流程 | 🔴 新系统多余（拆分出来的接口）
- [ ] | /workflow/task/isTaskEnd/{instanceId} | GET | 判断流程是否已结束 | 🔴 新系统多余
- [ ] | /workflow/task/reductionSignature | POST | 减签 | 🔴 新系统多余（拆分出来的接口）
- [ ] | /workflow/task/transferTask | POST | 转办任务 | 🔴 新系统多余（拆分出来的接口）

**差异说明**:
1. **新系统缺少 `taskOperation` 统一接口**（前端会调用失败，必须实现）
2. 新系统缺少 6 个接口：`currentTaskAllUser`, `getBackTaskNode`, `getTask`, `taskOperation`, `updateAssignee`, `urgeTask`
3. 新系统多余 7 个拆分接口（前端不需要，应删除或保留作为内部实现）

---

### IndexController-其他接口

**基础路径**: `/`

- [ ] | / | GET | 首页提示 | ⚪ 等价

**差异说明**: 无差异。

---

### SseController-SSE服务

**基础路径**: `${sse.path}` (默认: `/sse`)

- [ ] | ${sse.path} (默认: /sse) | GET | 建立SSE连接 | ⚪ 等价
- [ ] | ${sse.path}/close | GET | 关闭SSE连接 | ⚪ 等价

**差异说明**: 无差异。

---

### SysClientController-客户端管理

**基础路径**: `/system/client`

- [ ] | /system/client | POST | 新增客户端管理 | ⚪ 等价
- [ ] | /system/client | PUT | 修改客户端管理 | ⚪ 等价
- [ ] | /system/client/{ids} | DELETE | 删除客户端管理 | ⚪ 等价
- [ ] | /system/client/{id} | GET | 获取客户端管理详细信息 | ⚪ 等价
- [ ] | /system/client/changeStatus | PUT | 修改客户端状态 | ⚪ 等价
- [ ] | /system/client/export | POST | 导出客户端管理列表 | ⚪ 等价
- [ ] | /system/client/list | GET | 查询客户端管理列表 | ⚪ 等价
- [ ] | /system/client/clientId/{clientId} | GET | 根据客户端ID查询详情 | 🔴 新系统多余

**差异说明**: 新系统多了1个根据客户端ID查询详情的接口。

---

### SysConfigController-参数配置管理

**基础路径**: `/system/config`

- [ ] | /system/config | POST | 新增参数配置 | ⚪ 等价
- [ ] | /system/config | PUT | 修改参数配置 | ⚪ 等价
- [ ] | /system/config/{configIds} | DELETE | 删除参数配置 | ⚪ 等价
- [ ] | /system/config/{configId} | GET | 根据参数编号获取详细信息 | ⚪ 等价
- [ ] | /system/config/configKey/{configKey} | GET | 根据参数键名查询参数值 | ⚪ 等价
- [ ] | /system/config/export | POST | 导出参数配置列表 | 🟢 新系统缺少
- [ ] | /system/config/list | GET | 获取参数配置列表 | ⚪ 等价
- [ ] | /system/config/refreshCache | DELETE | 刷新参数缓存 | ⚪ 等价
- [ ] | /system/config/updateByKey | PUT | 根据参数键名修改参数配置 | ⚪ 等价

**差异说明**: 新系统缺少 `export` 接口。

---

### SysDeptController-部门管理

**基础路径**: `/system/dept`

- [ ] | /system/dept | POST | 新增部门 | ⚪ 等价
- [ ] | /system/dept | PUT | 修改部门 | ⚪ 等价
- [ ] | /system/dept/{deptId} | DELETE | 删除部门 | ⚪ 等价
- [ ] | /system/dept/{deptId} | GET | 根据部门编号获取详细信息 | ⚪ 等价
- [ ] | /system/dept/list | GET | 获取部门列表 | ⚪ 等价
- [ ] | /system/dept/list/exclude/{deptId} | GET | 查询部门列表（排除节点） | ⚪ 等价
- [ ] | /system/dept/optionselect | GET | 获取部门选择框列表 | 🟢 新系统缺少
- [ ] | /system/dept/treeselect | GET | 获取部门下拉树列表 | 🔴 新系统多余

**差异说明**:
1. 新系统缺少 `optionselect` 接口
2. 新系统多了 `treeselect` 接口

---

### SysDictDataController-字典数据管理

**基础路径**: `/system/dict/data`

- [ ] | /system/dict/data | POST | 新增字典数据 | ⚪ 等价
- [ ] | /system/dict/data | PUT | 修改字典数据 | ⚪ 等价
- [ ] | /system/dict/data/{dictCodes} | DELETE | 删除字典数据 | ⚪ 等价
- [ ] | /system/dict/data/{dictCode} | GET | 根据字典数据ID查询详细信息 | ⚪ 等价
- [ ] | /system/dict/data/export | POST | 导出字典数据列表 | ⚪ 等价
- [ ] | /system/dict/data/list | GET | 查询字典数据列表 | ⚪ 等价
- [ ] | /system/dict/data/type/{dictType} | GET | 根据字典类型查询字典数据信息 | ⚪ 等价
- [ ] | /system/dict/data/type/{dictType}/{dictValue} | GET | 根据字典类型和键值查询标签 | 🔴 新系统多余

**差异说明**: 新系统多了1个根据字典类型和键值查询标签的接口。

---

### SysDictTypeController-字典类型管理

**基础路径**: `/system/dict/type`

- [ ] | /system/dict/type | POST | 新增字典类型 | ⚪ 等价
- [ ] | /system/dict/type | PUT | 修改字典类型 | ⚪ 等价
- [ ] | /system/dict/type/{dictIds} | DELETE | 删除字典类型 | ⚪ 等价
- [ ] | /system/dict/type/{dictId} | GET | 根据字典类型ID查询详细信息 | ⚪ 等价
- [ ] | /system/dict/type/export | POST | 导出字典类型列表 | ⚪ 等价
- [ ] | /system/dict/type/list | GET | 查询字典类型列表 | ⚪ 等价
- [ ] | /system/dict/type/optionselect | GET | 获取字典选择框列表 | ⚪ 等价
- [ ] | /system/dict/type/refreshCache | DELETE | 刷新字典缓存 | ⚪ 等价

**差异说明**: 无差异。

---

### SysLogininforController-登录日志

**基础路径**: `/monitor/logininfor`

- [ ] | /monitor/logininfor/{infoIds} | DELETE | 删除登录日志 | ⚪ 等价
- [ ] | /monitor/logininfor/clean | DELETE | 清空登录日志 | ⚪ 等价
- [ ] | /monitor/logininfor/export | POST | 导出登录日志 | ⚪ 等价
- [ ] | /monitor/logininfor/list | GET | 获取登录日志列表 | ⚪ 等价
- [ ] | /monitor/logininfor/unlock/{userName} | GET | 账户解锁 | ⚪ 等价

**差异说明**: 无差异。

---

### SysMenuController-菜单管理

**基础路径**: `/system/menu`

- [ ] | /system/menu | POST | 新增菜单 | ⚪ 等价
- [ ] | /system/menu | PUT | 修改菜单 | ⚪ 等价
- [ ] | /system/menu/{menuId} | DELETE | 删除菜单 | ⚪ 等价
- [ ] | /system/menu/{menuId} | GET | 根据菜单编号获取详细信息 | ⚪ 等价
- [ ] | /system/menu/cascade/{menuIds} | DELETE | 批量级联删除菜单 | ⚪ 等价
- [ ] | /system/menu/getRouters | GET | 获取路由信息 | ⚪ 等价
- [ ] | /system/menu/list | GET | 获取菜单列表 | ⚪ 等价
- [ ] | /system/menu/roleMenuTreeselect/{roleId} | GET | 加载角色菜单列表树 | ⚪ 等价
- [ ] | /system/menu/tenantPackageMenuTreeselect/{packageId} | GET | 获取租户套餐菜单树下拉列表 | ⚪ 等价
- [ ] | /system/menu/treeselect | GET | 获取菜单下拉树列表 | ⚪ 等价

**差异说明**: 无差异。

---

### SysNoticeController-通知公告管理

**基础路径**: `/system/notice`

- [ ] | /system/notice | POST | 新增通知公告 | ⚪ 等价
- [ ] | /system/notice | PUT | 修改通知公告 | ⚪ 等价
- [ ] | /system/notice/{noticeIds} | DELETE | 删除通知公告 | ⚪ 等价
- [ ] | /system/notice/{noticeId} | GET | 根据通知公告编号获取详细信息 | ⚪ 等价
- [ ] | /system/notice/list | GET | 获取通知公告列表 | ⚪ 等价

**差异说明**: 无差异。

---

### SysOssConfigController-OSS配置管理

**基础路径**: `/resource/oss/config`

- [ ] | /resource/oss/config | POST | 新增对象存储配置 | ⚪ 等价
- [ ] | /resource/oss/config | PUT | 修改对象存储配置 | ⚪ 等价
- [ ] | /resource/oss/config/{ossConfigIds} | DELETE | 删除对象存储配置 | ⚪ 等价
- [ ] | /resource/oss/config/{ossConfigId} | GET | 获取对象存储配置详细信息 | ⚪ 等价
- [ ] | /resource/oss/config/changeStatus | PUT | 修改对象存储配置状态 | ⚪ 等价
- [ ] | /resource/oss/config/list | GET | 查询对象存储配置列表 | ⚪ 等价

**差异说明**: 无差异。

---

### SysOssController-对象存储管理

**基础路径**: `/resource/oss`

- [ ] | /resource/oss/{ossIds} | DELETE | 删除OSS对象存储 | ⚪ 等价
- [ ] | /resource/oss/download/{ossId} | GET | 下载OSS对象 | ⚪ 等价
- [ ] | /resource/oss/list | GET | 查询OSS对象存储列表 | ⚪ 等价
- [ ] | /resource/oss/listByIds/{ossIds} | GET | 查询OSS对象基于id串 | ⚪ 等价
- [ ] | /resource/oss/upload | POST | 上传OSS对象存储 | ⚪ 等价

**差异说明**: 无差异。

---

### SysOperLogController-操作日志

**基础路径**: `/monitor/operlog`

- [ ] | /monitor/operlog/{operIds} | DELETE | 删除操作日志 | ⚪ 等价
- [ ] | /monitor/operlog/{operId} | GET | 根据操作编号获取详细信息 | 🔴 新系统多余
- [ ] | /monitor/operlog/clean | DELETE | 清空操作日志 | ⚪ 等价
- [ ] | /monitor/operlog/export | POST | 导出操作日志 | ⚪ 等价
- [ ] | /monitor/operlog/list | GET | 获取操作日志列表 | ⚪ 等价

**差异说明**: 老系统没有根据操作编号获取详细信息的接口，新系统多了1个。

---

### SysPostController-岗位管理

**基础路径**: `/system/post`

- [ ] | /system/post | POST | 新增岗位 | ⚪ 等价
- [ ] | /system/post | PUT | 修改岗位 | ⚪ 等价
- [ ] | /system/post/{postIds} | DELETE | 删除岗位 | ⚪ 等价
- [ ] | /system/post/{postId} | GET | 根据岗位编号获取详细信息 | ⚪ 等价
- [ ] | /system/post/deptTree | GET | 获取部门树列表 | ⚪ 等价
- [ ] | /system/post/export | POST | 导出岗位列表 | ⚪ 等价
- [ ] | /system/post/list | GET | 获取岗位列表 | ⚪ 等价
- [ ] | /system/post/optionselect | GET | 获取岗位选择框列表 | ⚪ 等价

**差异说明**: 无差异。

---

### SysProfileController-个人信息

**基础路径**: `/system/user/profile`

- [ ] | /system/user/profile | GET | 获取个人信息 | ⚪ 等价
- [ ] | /system/user/profile | PUT | 修改用户信息 | ⚪ 等价
- [ ] | /system/user/profile/avatar | POST | 上传头像 | ⚪ 等价
- [ ] | /system/user/profile/updatePwd | PUT | 重置密码 | ⚪ 等价

**差异说明**: 无差异。

---

### SysRoleController-角色管理

**基础路径**: `/system/role`

- [ ] | /system/role | POST | 新增角色 | ⚪ 等价
- [ ] | /system/role | PUT | 修改角色 | ⚪ 等价
- [ ] | /system/role/{roleIds} | DELETE | 删除角色 | ⚪ 等价
- [ ] | /system/role/{roleId} | GET | 根据角色编号获取详细信息 | ⚪ 等价
- [ ] | /system/role/authUser/allocatedList | GET | 查询已分配用户角色列表 | ⚪ 等价
- [ ] | /system/role/authUser/unallocatedList | GET | 查询未分配用户角色列表 | ⚪ 等价
- [ ] | /system/role/authUser/cancel | PUT | 取消授权用户 | ⚪ 等价
- [ ] | /system/role/authUser/cancelAll | PUT | 批量取消授权用户 | ⚪ 等价
- [ ] | /system/role/authUser/selectAll | PUT | 批量选择用户授权 | ⚪ 等价
- [ ] | /system/role/changeStatus | PUT | 修改角色状态 | ⚪ 等价
- [ ] | /system/role/dataScope | PUT | 修改数据权限 | ⚪ 等价
- [ ] | /system/role/deptTree/{roleId} | GET | 获取角色部门树列表 | ⚪ 等价
- [ ] | /system/role/export | POST | 导出角色列表 | ⚪ 等价
- [ ] | /system/role/list | GET | 获取角色列表 | ⚪ 等价
- [ ] | /system/role/optionselect | GET | 获取角色选择框列表 | ⚪ 等价

**差异说明**: 无差异。

---

### SysSocialController-社交登录

**基础路径**: `/system/social`

- [ ] | /system/social/list | GET | 查询社交关系列表 | ⚪ 等价

**差异说明**: 无差异。

---

### SysTenantController-租户管理

**基础路径**: `/system/tenant`

- [ ] | /system/tenant | POST | 新增租户 | ⚪ 等价
- [ ] | /system/tenant | PUT | 修改租户 | ⚪ 等价
- [ ] | /system/tenant/{ids} | DELETE | 删除租户 | ⚪ 等价
- [ ] | /system/tenant/{id} | GET | 获取租户详细信息 | ⚪ 等价
- [ ] | /system/tenant/changeStatus | PUT | 修改租户状态 | ⚪ 等价
- [ ] | /system/tenant/dynamic/{tenantId} | GET | 动态切换租户 | ⚪ 等价
- [ ] | /system/tenant/dynamic/clear | GET | 清除动态租户 | ⚪ 等价
- [ ] | /system/tenant/export | POST | 导出租户列表 | ⚪ 等价
- [ ] | /system/tenant/list | GET | 查询租户列表 | ⚪ 等价
- [ ] | /system/tenant/syncTenantConfig | GET | 同步租户参数配置 | ⚪ 等价
- [ ] | /system/tenant/syncTenantDict | GET | 同步租户字典 | ⚪ 等价
- [ ] | /system/tenant/syncTenantPackage | GET | 同步租户套餐 | ⚪ 等价

**差异说明**: 无差异。

---

### SysTenantPackageController-租户套餐管理

**基础路径**: `/system/tenant/package`

- [ ] | /system/tenant/package | POST | 新增租户套餐 | ⚪ 等价
- [ ] | /system/tenant/package | PUT | 修改租户套餐 | ⚪ 等价
- [ ] | /system/tenant/package/{packageIds} | DELETE | 删除租户套餐 | ⚪ 等价
- [ ] | /system/tenant/package/{packageId} | GET | 获取租户套餐详细信息 | ⚪ 等价
- [ ] | /system/tenant/package/changeStatus | PUT | 修改租户套餐状态 | ⚪ 等价
- [ ] | /system/tenant/package/export | POST | 导出租户套餐列表 | ⚪ 等价
- [ ] | /system/tenant/package/list | GET | 查询租户套餐列表 | ⚪ 等价
- [ ] | /system/tenant/package/selectList | GET | 查询租户套餐下拉选列表 | ⚪ 等价

**差异说明**: 无差异。

---

### SysUserOnlineController-在线用户

**基础路径**: `/monitor/online`

- [ ] | /monitor/online | GET | 获取当前用户登录在线设备 | 🟢 新系统缺少
- [ ] | /monitor/online/{tokenId} | DELETE | 强退用户 | ⚪ 等价
- [ ] | /monitor/online/list | GET | 获取在线用户列表 | ⚪ 等价
- [ ] | /monitor/online/myself/{tokenId} | DELETE | 强退当前在线设备 | 🟢 新系统缺少

**差异说明**: 新系统缺少2个接口：获取当前用户登录在线设备、强退当前在线设备。

---

### SysUserController-用户管理

**基础路径**: `/system/user`

- [ ] | /system/user | POST | 新增用户 | ⚪ 等价
- [ ] | /system/user | GET | 获取用户信息表单（无ID时返回空表单）或根据用户编号获取详细信息 | ⚪ 等价
- [ ] | /system/user | PUT | 修改用户 | ⚪ 等价
- [ ] | /system/user/{userIds} | DELETE | 删除用户 | ⚪ 等价
- [ ] | /system/user/{userId} | GET | 根据用户编号获取详细信息 | ⚪ 等价
- [ ] | /system/user/authRole | PUT | 用户授权角色 | ⚪ 等价
- [ ] | /system/user/authRole/{userId} | GET | 获取用户授权角色 | ⚪ 等价
- [ ] | /system/user/changeStatus | PUT | 修改用户状态 | ⚪ 等价
- [ ] | /system/user/deptTree | GET | 获取部门树列表 | ⚪ 等价
- [ ] | /system/user/export | POST | 导出用户列表 | ⚪ 等价
- [ ] | /system/user/getInfo | GET | 获取当前用户信息 | ⚪ 等价
- [ ] | /system/user/importData | POST | 导入用户数据 | ⚪ 等价
- [ ] | /system/user/importTemplate | POST | 下载用户导入模板 | ⚪ 等价
- [ ] | /system/user/list | GET | 获取用户列表 | ⚪ 等价
- [ ] | /system/user/list/dept/{deptId} | GET | 根据部门ID查询用户列表 | ⚪ 等价
- [ ] | /system/user/optionselect | GET | 批量获取用户基础信息 | ⚪ 等价
- [ ] | /system/user/resetPwd | PUT | 重置密码 | ⚪ 等价

**差异说明**: 两个系统接口完全等价，无差异。

---

## 待办事项清单

### 🟢 必须补充的接口（新系统缺少）

#### 高优先级（核心功能）
- [ ] **AuthController**: 3个社交登录接口
  - [ ] `GET /auth/binding/{source}` - 获取社交登录跳转URL
  - [ ] `POST /auth/social/callback` - 社交登录回调绑定
  - [ ] `DELETE /auth/unlock/{socialId}` - 取消社交授权

- [ ] **FlowDefinitionController**: 4个接口
  - [ ] `PUT /workflow/active/{id}` - 激活/挂起流程定义
  - [ ] `POST /workflow/importDef` - 导入流程定义
  - [ ] `GET /workflow/xmlString/{id}` - 获取流程定义JSON字符串
  - [ ] `POST /workflow/exportDef/{id}` - 导出流程定义

- [ ] **FlowInstanceController**: 9个接口
  - [ ] `GET /workflow/instance/{businessId}` - 根据业务id查询流程实例详细信息
  - [ ] `PUT /workflow/instance/active/{id}` - 激活/挂起流程实例
  - [ ] `DELETE /workflow/instance/deleteByInstanceIds/{instanceIds}` - 根据实例ID删除流程实例（路径参数）
  - [ ] `DELETE /workflow/instance/deleteByBusinessIds/{businessIds}` - 根据业务ID删除流程实例（路径参数）
  - [ ] `DELETE /workflow/instance/deleteHisByInstanceIds/{instanceIds}` - 删除已完成流程实例
  - [ ] `GET /workflow/instance/flowHisTaskList/{businessId}` - 获取流程图，流程记录
  - [ ] `GET /workflow/instance/instanceVariable/{instanceId}` - 获取流程变量
  - [ ] `POST /workflow/instance/invalid` - 作废流程
  - [ ] `PUT /workflow/instance/updateVariable` - 修改流程变量

#### 中优先级（工作流增强）
- [ ] **SysConfigController**: 1个接口
  - [ ] `POST /system/config/export` - 导出参数配置列表

- [ ] **FlowTaskController**: 6个接口
  - [ ] `GET /workflow/task/currentTaskAllUser/{taskId}` - 获取当前任务的所有办理人
  - [ ] `GET /workflow/task/getBackTaskNode/{taskId}/{nowNodeCode}` - 获取可驳回的前置节点
  - [ ] `GET /workflow/task/getTask/{taskId}` - 根据taskId查询代表任务
  - [ ] `POST /workflow/task/taskOperation/{taskOperation}` - 任务操作（委派、转办、加签、减签）
  - [ ] `PUT /workflow/task/updateAssignee/{userId}` - 修改任务办理人
  - [ ] `POST /workflow/task/urgeTask` - 催办任务

#### 低优先级（辅助功能）
- [ ] **CaptchaController**: 2个验证码接口
  - [ ] `GET /resource/sms/code` - 短信验证码
  - [ ] `GET /resource/email/code` - 邮箱验证码

- [ ] **SysDeptController**: 1个接口
  - [ ] `GET /system/dept/optionselect` - 获取部门选择框列表

- [ ] **SysUserOnlineController**: 2个接口
  - [ ] `GET /monitor/online` - 获取当前用户登录在线设备
  - [ ] `DELETE /monitor/online/myself/{tokenId}` - 强退当前在线设备

### 🔴 需要确认的接口（新系统多余）

- [ ] **FlowTaskController**: 7个拆分出来的独立接口 - 前端不调用，建议删除或保留作为内部实现
  - `addSignature` - 加签
  - `cancelProcess` - 取消流程
  - `delegateTask` - 委派任务
  - `invalidProcess` - 作废流程
  - `isTaskEnd/{instanceId}` - 判断流程是否已结束
  - `reductionSignature` - 减签
  - `transferTask` - 转办任务
- [ ] **SysClientController**: 1个根据客户端ID查询详情接口
- [ ] **SysDeptController**: 1个获取部门下拉树列表接口（与 optionselect 的关系）
- [ ] **SysDictDataController**: 1个根据字典类型和键值查询标签接口
- [ ] **SysOperLogController**: 1个根据操作编号获取详细信息接口

---

## 使用说明

1. **确认接口等价性**: 对每个标记为 ⚪ 等价的接口，勾选确认框 `[ ]` 为 `[x]`
2. **补充缺失接口**: 按照"待办事项清单"优先级补充新系统缺少的接口
3. **处理多余接口**: 确认新系统多余接口是否需要保留或删除
4. **更新文档**: 完成后更新本文档的状态，标记已完成的接口

---

> **最后更新**: 2025-02-15
> **责任人**: 待分配
> **完成目标**: 第一阶段迁移完成前，保证新系统和老系统的接口完全等价