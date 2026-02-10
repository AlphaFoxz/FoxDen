# 工作流模块 (Workflow Module)

本模块提供工作流引擎相关功能，基于 Jimmer ORM 和 Kotlin 实现。

## 📦 模块结构

```
foxden-domain-workflow/
├── entity/           # Jimmer 实体定义
├── bo/               # 业务对象
├── vo/               # 视图对象
├── enums/            # 枚举类
├── service/          # 服务接口
│   └── impl/         # 服务实现
└── constant/         # 常量定义
```

## ✅ 已完成内容

### 1. 实体类 (10个)

| 实体 | 说明 | 表名 |
|------|------|------|
| FlowCategory | 流程分类 | flow_category |
| FlowDefinition | 流程定义 | flow_definition |
| FlowInstance | 流程实例 | flow_instance |
| FlowTask | 任务 | flow_task |
| FlowNode | 节点 | flow_node |
| FlowHisTask | 历史任务 | flow_his_task |
| FlowSkip | 跳转记录 | flow_skip |
| FlowCopy | 抄送 | flow_copy |
| FlowSpel | SpEL表达式 | flow_spel |
| FlowMonitor | 监听器 | flow_monitor |

### 2. 枚举类 (7个)

- **TaskStatusEnum** - 任务状态（撤销、通过、待审核、作废、退回等）
- **ButtonPermissionEnum** - 按钮权限（弹窗选人、委托、转办、抄送等）
- **CopySettingEnum** - 抄送设置（并行会签、串行会签、独占）
- **MessageTypeEnum** - 消息类型（站内信、邮箱、短信）
- **TaskAssigneeEnum** - 任务分配人类型（用户、角色、部门、岗位、SpEL）
- **TaskAssigneeType** - 人员权限类型（审批人、转办人、委托人、抄送人）
- **NodeExtEnum** - 节点扩展属性接口

### 3. 业务对象 (16个)

用于接收前端请求参数，包含完整的字段验证注解。

### 4. 视图对象 (9个)

用于返回数据给前端，包含完整的业务字段。

### 5. Service 层

#### FlowSpelService
- 流程 SpEL 表达式管理
- 支持增删改查、分页查询
- 支持根据 SpEL 查询备注信息

#### FlowCategoryService
- 流程分类管理
- 支持树形结构查询
- 支持唯一性校验、子节点检查等

#### FlowDefinitionService
- 流程定义管理（待WarmFlow集成）
- 支持流程发布、取消发布、导入导出

#### FlowInstanceService
- 流程实例管理（待WarmFlow集成）
- 支持查询运行中/已结束实例、删除实例

#### FlowTaskService
- 任务管理（待WarmFlow集成）
- 支持启动流程、完成任务、驳回、转办、委托等操作

#### FlowTaskAssigneeService
- 任务分配人解析服务
- 支持根据storageIds解析并查询用户列表
- 支持用户、角色、部门、岗位、SpEL表达式等多种分配类型

#### FlowCommonService
- 通用工作流服务
- 发送消息（站内信、邮件、短信）
- 获取申请人节点编码

### 6. Controller 层

#### FlowSpelController
- `GET /workflow/spel/list` - 查询列表
- `GET /workflow/spel/{id}` - 获取详情
- `POST /workflow/spel` - 新增
- `PUT /workflow/spel` - 修改
- `DELETE /workflow/spel/{ids}` - 删除

#### FlowCategoryController
- `GET /workflow/category/list` - 查询列表
- `GET /workflow/category/{categoryId}` - 获取详情
- `GET /workflow/category/categoryTree` - 获取树形结构
- `POST /workflow/category` - 新增
- `PUT /workflow/category` - 修改
- `DELETE /workflow/category/{categoryId}` - 删除

#### FlowDefinitionController
- `GET /workflow/definition/list` - 查询流程定义列表
- `GET /workflow/definition/unPublishList` - 查询未发布流程定义
- `GET /workflow/definition/{id}` - 获取流程定义详情
- `POST /workflow/definition` - 新增流程定义
- `PUT /workflow/definition` - 修改流程定义
- `PUT /workflow/definition/publish/{id}` - 发布流程定义
- `PUT /workflow/definition/unPublish/{id}` - 取消发布流程定义
- `DELETE /workflow/definition/{ids}` - 删除流程定义
- `GET /workflow/definition/export/{id}` - 导出流程定义
- `POST /workflow/definition/import` - 导入流程定义
- `POST /workflow/definition/copy/{id}` - 复制流程定义

#### FlowTaskController
- `POST /workflow/task/startWorkFlow` - 启动流程
- `POST /workflow/task/completeTask` - 办理任务
- `GET /workflow/task/pageByTaskWait` - 查询待办任务
- `GET /workflow/task/pageByAllTaskWait` - 查询所有待办任务
- `GET /workflow/task/pageByTaskFinish` - 查询已办任务
- `GET /workflow/task/pageByAllTaskFinish` - 查询所有已办任务
- `GET /workflow/task/pageByTaskCopy` - 查询抄送
- `GET /workflow/task/{taskId}` - 获取任务详情
- `POST /workflow/task/getNextNodeList` - 获取下一节点
- `POST /workflow/task/backProcess` - 驳回审批
- `POST /workflow/task/terminationTask` - 终止任务
- `POST /workflow/task/cancelProcess` - 取消流程
- `POST /workflow/task/invalidProcess` - 作废流程
- `POST /workflow/task/delegateTask` - 委派任务
- `POST /workflow/task/transferTask` - 转办任务
- `POST /workflow/task/addSignature` - 加签
- `POST /workflow/task/reductionSignature` - 减签
- `GET /workflow/task/isTaskEnd/{instanceId}` - 判断流程是否已结束

#### FlowInstanceController
- `GET /workflow/instance/selectRunningInstanceList` - 查询运行中流程实例
- `GET /workflow/instance/selectFinishInstanceList` - 查询已结束流程实例
- `GET /workflow/instance/selectCurrentInstanceList` - 查询当前用户发起的流程实例
- `GET /workflow/instance/{instanceId}` - 获取流程实例详情
- `DELETE /workflow/instance/deleteByBusinessIds` - 根据业务ID删除
- `DELETE /workflow/instance/deleteByInstanceIds` - 根据实例ID删除
- `POST /workflow/instance/cancelProcessApply` - 取消流程申请
- `POST /workflow/instance/active/{instanceId}` - 激活流程实例

## ⚠️ 待完成内容

### 1. WarmFlow 集成

需要添加 WarmFlow 依赖并实现核心业务逻辑：

```kotlin
// build.gradle.kts
implementation("io.github.minliuhua:warm-flow:${property("version.warmflow")}")
```

需要实现的核心功能：
- FlowDefinitionService - 流程定义发布、导入导出
- FlowInstanceService - 流程实例查询、删除、激活
- FlowTaskService - 任务启动、完成、驳回、转办、委托等操作

### 2. 复杂查询优化

部分 Service 方法使用了简化的 TODO 实现，需要完善：

- FlowSpelServiceImpl.queryPageList - 分页查询
- FlowSpelServiceImpl.selectSpelByTaskAssigneeList - 任务分配查询
- FlowCategoryServiceImpl 查询条件完善

### 3. 实现Controller中的TODO方法

以下Controller方法中有TODO标记，需要实现：
- FlowDefinitionController.add/edit/unPublish/copy
- FlowInstanceController.selectCurrentInstanceList/deleteByBusinessIds/cancelProcessApply/active
- FlowTaskController.getTask

## 🔧 依赖说明

本模块依赖以下组件：

- **Jimmer ORM** - Kotlin 优先的 ORM 框架
- **Spring Boot 3.5.10** - 应用框架
- **Sa-Token** - 权限认证
- **WarmFlow** (待集成) - 工作流引擎

## 📝 使用示例

### 查询流程分类

```kotlin
// GET /workflow/category/list
val bo = FlowCategoryBo(categoryName = "审批分类")
val result = flowCategoryController.list(bo)
```

### SpEL 表达式管理

```kotlin
// 新增 SpEL 表达式
val bo = FlowSpelBo(
    componentName = "userService",
    methodName = "getUserById",
    methodParams = "Long userId",
    viewSpel = "@userService.getUserById(#userId)",
    status = "0"
)
flowSpelService.insertByBo(bo)
```

### 查询流程定义

```kotlin
// GET /workflow/definition/list
val bo = FlowDefinitionBo(flowCode = "leave_approval")
val pageQuery = PageQuery()
val result = flowDefinitionController.list(bo, pageQuery)
```

## 🚀 下一步计划

1. **集成 WarmFlow 引擎**
   - 添加依赖配置
   - 配置流程引擎
   - 实现核心业务逻辑

2. **完善 Service 实现**
   - 实现复杂的查询条件
   - 优化性能
   - 添加缓存支持

3. **实现 Controller TODO 方法**
   - 完善FlowDefinitionController
   - 完善FlowInstanceController
   - 完善FlowTaskController

## 📖 参考资料

- [Jimmer 官方文档](https://babyfish-ct.github.io/jimmer/)
- [WarmFlow 官方文档](https://warmflow.minliuhua.com/)
- [项目迁移指南](/docs/migration-guide.md)
- [工作流集成指南](/docs/WORKFLOW_INTEGRATION.md) - 完整的WarmFlow集成说明

## ⚡ 快速开始

### 启用工作流模块

在 `application.yaml` 中配置：

```yaml
foxden:
  workflow:
    enabled: false  # 暂时禁用，等待完整集成
    database-type: postgresql
```

### 使用示例

#### 1. 查询流程分类树
```kotlin
val categories = flowCategoryController.categoryTree(FlowCategoryBo())
```

#### 2. 管理SpEL表达式
```kotlin
// 新增SpEL表达式
val spelBo = FlowSpelBo(
    componentName = "userService",
    methodName = "getUserById",
    viewSpel = "@userService.getUserById(#userId)"
)
flowSpelService.insertByBo(spelBo)
```

#### 3. 查询流程定义
```kotlin
val definitions = flowDefinitionService.queryList(
    flowCode = "leave_approval",
    flowName = null,
    pageQuery = PageQuery()
)
```

## 🔧 架构说明

### Jimmer ORM 与 WarmFlow 集成

本项目使用 Jimmer ORM，而 WarmFlow 仅支持 MyBatis-Plus。为解决此架构差异：

1. **适配器模式**: 使用 `WorkflowEngineAdapter` 进行数据转换
2. **接口隔离**: Service层定义与WarmFlow兼容的接口
3. **配置分离**: 通过 `WorkflowProperties` 控制模块启用状态

详细集成方案请参考 [WORKFLOW_INTEGRATION.md](/docs/WORKFLOW_INTEGRATION.md)
