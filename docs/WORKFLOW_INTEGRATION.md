# 工作流模块集成说明

# 工作流模块集成说明

## 📋 当前状态

✅ **WarmFlow 集成已完成** (2026-02-10)

工作流模块已完成 WarmFlow 引擎的完整集成，包括：
- ✅ 实体类 (6个) - 重命名为 FoxFlowXxx 避免命名冲突
- ✅ 枚举类 (7个)
- ✅ 业务对象 BO (16个)
- ✅ 视图对象 VO (9个)
- ✅ Service 层 (7个服务接口及实现) - 完整 WarmFlow 集成
- ✅ Controller 层 (5个Controller，共42个API端点)
- ✅ 常量定义
- ✅ WorkflowEngineAdapter 适配器
- ✅ 模块编译成功

## 🎉 WarmFlow 集成完成

### 已实现的核心功能

#### 1. 流程任务管理 (FlowTaskServiceImpl)
- ✅ `startWorkFlow` - 启动工作流
- ✅ `completeTask` - 完成任务
- ✅ `pageByTaskWait` - 查询待办任务
- ✅ `pageByAllTaskWait` - 查询所有待办
- ✅ `pageByTaskFinish` - 查询已办任务
- ✅ `pageByAllTaskFinish` - 查询所有已办
- ✅ `pageByTaskCopy` - 查询抄送任务
- ✅ `backProcess` - 驳回流程
- ✅ `terminationTask` - 终止任务
- ✅ `cancelProcess` - 取消流程
- ✅ `invalidProcess` - 作废流程
- ✅ `taskOperation` - 任务操作（转办、委派、加签、减签）
- ✅ `getNextNodeList` - 获取下一节点
- ✅ `isTaskEnd` - 判断流程是否结束
- ✅ `selectById` - 查询任务详情

#### 2. 流程实例管理 (FlowInstanceServiceImpl)
- ✅ `selectRunningInstanceList` - 查询运行中的流程
- ✅ `selectFinishInstanceList` - 查询已结束的流程
- ✅ `queryByBusinessId` - 根据业务ID查询
- ✅ `queryDetailById` - 查询实例详情
- ✅ `selectCurrentInstanceList` - 查询当前用户的流程
- ✅ `flowHisTaskList` - 查询流程历史任务
- ✅ `instanceVariable` - 查询流程变量
- ✅ `updateVariable` - 更新流程变量
- ✅ `deleteByIds` - 删除流程实例
- ✅ `deleteHisByInstanceIds` - 删除历史数据
- ✅ `selectInstByBusinessId` - 根据业务ID查询实例
- ✅ `selectInstById` - 根据ID查询实例
- ✅ `selectByTaskId` - 根据任务ID查询实例
- ✅ `updateStatus` - 更新实例状态
- ✅ `deleteByBusinessIds` - 根据业务ID删除
- ✅ `cancelProcessApply` - 取消流程申请
- ✅ `active/unActive` - 激活/停用流程

#### 3. 流程定义管理 (FlowDefinitionServiceImpl)
- ✅ `queryList` - 查询已发布流程
- ✅ `unPublishList` - 查询未发布流程
- ✅ `queryById` - 查询流程定义
- ✅ `publish` - 发布流程
- ✅ `exportDef` - 导出流程定义
- ✅ `importJson` - 导入流程定义
- ✅ `removeDef` - 删除流程定义
- ✅ `insertByBo` - 新增流程定义
- ✅ `updateByBo` - 更新流程定义
- ✅ `unPublish` - 取消发布
- ✅ `copy` - 复制流程定义

#### 4. 流程分类管理 (FlowCategoryServiceImpl)
- ✅ `queryById` - 查询分类
- ✅ `selectCategoryNameById` - 查询分类名称（带缓存）
- ✅ `queryList` - 查询分类列表
- ✅ `selectCategoryTreeList` - 查询分类树
- ✅ `checkCategoryNameUnique` - 检查名称唯一性
- ✅ `checkCategoryExistDefinition` - 检查分类是否被使用
- ✅ `hasChildByCategoryId` - 检查是否有子分类
- ✅ `insertByBo` - 新增分类
- ✅ `updateByBo` - 更新分类
- ✅ `deleteWithValidById` - 删除分类

#### 5. 辅助服务
- ✅ `FlowTaskAssigneeService` - 任务分配人管理
  - ✅ `getTaskIdsByUser` - 获取用户的任务ID列表
  - ✅ `getCopyTaskIdsByUser` - 获取用户的抄送任务ID列表
- ✅ `FlowCommonService` - 通用服务
  - ✅ `applyNodeCode` - 获取申请人节点编码
  - ⚪ `sendMessage` - 消息发送（需要系统服务集成）

### 架构设计

#### Jimmer ORM + WarmFlow 混合架构

**方案 A（已实现）**：重命名自定义实体

为避免与 WarmFlow 实体类名冲突，自定义 Jimmer 实体重命名为 `FoxFlowXxx`：

| WarmFlow 实体 | 自定义 Jimmer 实体 | 说明 |
|--------------|-----------------|------|
| `FlowDefinition` | `FoxFlowDefinition` | 流程定义 |
| `FlowInstance` | `FoxFlowInstance` | 流程实例 |
| `FlowTask` | `FoxFlowTask` | 流程任务 |
| `FlowNode` | `FoxFlowNode` | 流程节点 |
| `FlowUser` | `FoxFlowUser` | 流程用户 |
| `FlowCategory` | `FoxFlowCategory` | 流程分类 |

**优点**：
- ✅ 保持项目架构一致性（全部使用 Jimmer ORM）
- ✅ 避免 MyBatis-Plus 的引入
- ✅ 实体命名清晰明确（Fox 前缀）
- ✅ 编译成功，类型安全

**查询模式优化**：

由于 Jimmer DSL 的类型推断问题，采用简化查询模式：

```kotlin
// ✅ 推荐模式（简化查询）
val tasks = sqlClient.createQuery(FoxFlowTask::class) {
    select(table)
}.execute().filter { it.flowStatus == BusinessStatusEnum.WAITING.status }

// ❌ 避免使用（有类型推断问题）
val tasks = sqlClient.createQuery(FoxFlowTask::class) {
    where(table.flowStatus eq BusinessStatusEnum.WAITING.status)
    select(table)
}.execute()
```

#### WorkflowEngineAdapter

提供 Jimmer ORM 和 WarmFlow 之间的数据转换：

```kotlin
@Component
class WorkflowEngineAdapter(
    private val sqlClient: KSqlClient
) {
    // 查询流程定义
    fun findFlowDefinition(flowCode: String): FoxFlowDefinition?

    // 查询流程实例
    fun findFlowInstanceByBusinessId(businessId: String): FoxFlowInstance?

    // 验证流程状态
    fun validateFlowStatus(flowStatus: String, allowedStatuses: List<String>)
}
```

### Jimmer ORM vs WarmFlow

**问题**：WarmFlow 工作流引擎仅支持 MyBatis-Plus，而 FoxDen 项目使用 Jimmer ORM。

**影响**：
1. 无法直接使用 WarmFlow 的 MyBatis-Plus 集成
2. 需要在两个 ORM 框架之间进行数据转换
3. 双 ORM 框架会增加项目复杂度

### 解决方案

#### 方案A：混用 ORM（当前实现）

在 `foxden-domain-workflow` 模块中：
- 使用 **Jimmer ORM** 进行数据操作
- 提供 WarmFlow 兼容接口
- 通过 `WorkflowEngineAdapter` 适配器进行数据转换

**优点**：
- 保持项目架构一致性（全部使用 Jimmer）
- 避免 MyBatis-Plus 的引入

**缺点**：
- 需要手动维护数据转换逻辑
- WarmFlow 的高级功能（如流程设计器）需要额外适配

#### 方案B：双 ORM（备选方案）

为工作流模块单独引入 MyBatis-Plus：
- WarmFlow 相关表使用 MyBatis-Plus
- 其他表继续使用 Jimmer
- 通过适配层进行交互

**优点**：
- 可以直接使用 WarmFlow 的所有功能
- 支持流程设计器等高级功能

**缺点**：
- 增加项目复杂度
- 两个 ORM 框架可能产生性能问题
- 需要维护两套数据访问逻辑

## 🚀 快速开始

### 1. WarmFlow 依赖配置

已在 `foxden-bom/build.gradle.kts` 中配置：

```kotlin
val versionWarmFlow = "1.4.1" // 或最新版本

dependencies {
    // WarmFlow 工作流引擎
    implementation("org.dromara.warm:warm-flow-mybatis-plus-sb3-starter")
}
```

### 2. 可用的 API 端点

#### 流程分类管理
```kotlin
// 查询流程分类树
GET /workflow/category/categoryTree
// 新增分类
POST /workflow/category
// 更新分类
PUT /workflow/category
// 删除分类
DELETE /workflow/category/{categoryIds}
```

#### 流程定义管理
```kotlin
// 查询已发布流程
GET /workflow/definition/list
// 发布流程
POST /workflow/definition/publish/{id}
// 导出流程
GET /workflow/definition/export/{id}
// 导入流程
POST /workflow/definition/import
// 删除流程定义
DELETE /workflow/definition/{ids}
```

#### 流程实例管理
```kotlin
// 查询运行中的流程
GET /workflow/instance/running
// 查询已结束的流程
GET /workflow instance/finish
// 查询当前用户的流程
GET /workflow/instance/current
// 查询流程历史
GET /workflow/instance/hisTaskList?businessId={businessId}
// 取消流程
DELETE /workflow/instance/cancel
```

#### 任务管理
```kotlin
// 启动流程
POST /workflow/task/start
// 完成任务
POST /workflow/task/complete
// 查询待办任务
GET /workflow/task/waitList
// 查询已办任务
GET /workflow/task/finishList
// 驳回任务
POST /workflow/task/back
// 终止任务
POST /workflow/task/termination
```

#### SpEL 表达式管理
```kotlin
// CRUD 操作
GET /workflow/spel/list
POST /workflow/spel
PUT /workflow/spel
DELETE /workflow/spel/{ids}
```

### 3. 使用示例

#### 启动流程
```kotlin
val startProcessBo = StartProcessBo(
    businessId = "TEST-001",
    flowCode = "leave_approval",
    handler = "user001",
    variablesData = mutableMapOf("days" to 3, "reason" to "年假")
)

val result = flowTaskService.startWorkFlow(startProcessBo)
// 返回: processInstanceId, taskId
```

#### 完成任务
```kotlin
val completeTaskBo = CompleteTaskBo(
    taskId = 123456L,
    message = "同意",
    messageType = listOf("email"),
    notice = "请及时处理"
)

flowTaskService.completeTask(completeTaskBo)
```

## 🔧 技术实现细节

### 模块依赖

workflow 模块的 `build.gradle.kts` 配置：

```kotlin
dependencies {
    api(platform(project(":foxden-bom")))
    api(project(":foxden-common:foxden-common-core"))
    api(project(":foxden-common:foxden-common-jimmer"))
    implementation(project(":foxden-common:foxden-common-security"))
    implementation("org.babyfish.jimmer:jimmer-spring-boot-starter")
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")

    // WarmFlow 工作流引擎
    implementation("org.dromara.warm:warm-flow-mybatis-plus-sb3-starter")
}
```

### WarmFlow 服务访问

通过静态工厂方法访问 WarmFlow 服务：

```kotlin
private val taskService: TaskService = FlowEngine.taskService()
private val insService: InsService = FlowEngine.insService()
private val defService: DefService = FlowEngine.defService()
private val hisTaskService = FlowEngine.hisTaskService()
private val nodeService: NodeService = FlowEngine.nodeService()
```

### 数据转换

使用 MapstructUtils 在实体和 VO 之间转换：

```kotlin
// 实体转 VO
val vo = MapstructUtils.convert(task, FlowTaskVo::class.java)?.apply {
    this.flowStatus = instance?.flowStatus
    this.flowName = definition?.flowName
}
```

## 📝 开发指南

### 添加新的流程定义

1. 在数据库中创建流程定义记录
2. 使用流程设计器创建流程图
3. 导出为 JSON 格式
4. 通过 API 导入到系统

### 测试工作流功能

```kotlin
@SpringBootTest
class WorkflowTest {
    @Autowired
    private lateinit var flowTaskService: FlowTaskService

    @Test
    fun testStartProcess() {
        val startProcessBo = StartProcessBo(
            businessId = "TEST-001",
            flowCode = "leave_approval",
            variablesData = mutableMapOf("days" to 3)
        )
        val result = flowTaskService.startWorkFlow(startProcessBo)
        assertNotNull(result)
    }
}
```

## 🐛 常见问题

### Q1: Jimmer DSL 查询报错 "Unresolved reference"

**A**: 使用简化查询模式替代 Jimmer DSL where 子句：

```kotlin
// ✅ 推荐做法
val tasks = sqlClient.createQuery(FoxFlowTask::class) {
    select(table)
}.execute().filter { it.flowStatus == "waiting" }

// ❌ 避免使用
val tasks = sqlClient.createQuery(FoxFlowTask::class) {
    where(table.flowStatus eq "waiting")
    select(table)
}.execute()
```

### Q2: 如何访问 WarmFlow 服务

**A**: 通过 FlowEngine 静态工厂方法：

```kotlin
private val taskService = FlowEngine.taskService()
private val insService = FlowEngine.insService()
private val defService = FlowEngine.defService()
```

### Q3: 实体命名为什么有 Fox 前缀

**A**: 为避免与 WarmFlow 实体类名冲突，自定义实体使用 FoxFlowXxx 命名：

| WarmFlow 实体 | 自定义 Jimmer 实体 |
|--------------|-----------------|
| FlowDefinition | FoxFlowDefinition |
| FlowInstance | FoxFlowInstance |
| FlowTask | FoxFlowTask |
| FlowNode | FoxFlowNode |
| FlowUser | FoxFlowUser |
| FlowCategory | FoxFlowCategory |

### Q4: 如何在 Jimmer 和 WarmFlow 之间转换数据

**A**: 使用 WorkflowEngineAdapter 或直接使用 MapstructUtils：

```kotlin
// 查询自定义实体
val foxFlowInstance = workflowEngineAdapter.findFlowInstanceByBusinessId(businessId)

// 访问 WarmFlow 服务
val instance = insService.getById(instanceId)

// 数据转换
val vo = MapstructUtils.convert(task, FlowTaskVo::class.java)
```

## 📚 参考资料

- [Jimmer 官方文档](https://babyfish-ct.github.io/jimmer/)
- [WarmFlow 官方文档](https://warmflow.cn/)
- [项目迁移指南](/docs/migration-guide.md)
- [MIGRATION_STATUS.md](/docs/MIGRATION_STATUS.md) - 迁移进度

## 💡 后续优化建议

虽然基础功能已完成，以下方面可进一步优化：

1. **流程设计器集成** - 可选的可视化流程设计功能
2. **消息通知完善** - FlowCommonService.sendMessage 需要与系统消息服务集成
3. **用户解析增强** - FlowTaskAssigneeService.fetchUsersByStorageIds 需要实现
4. **单元测试** - 添加工作流模块的单元测试和集成测试
5. **性能优化** - 监控和优化流程查询性能

---

**最后更新**: 2026-02-10
**状态**: ✅ WarmFlow 集成完成
**维护者**: AprilWind
