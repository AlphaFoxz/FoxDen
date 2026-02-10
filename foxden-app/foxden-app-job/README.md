# FoxDen 定时任务模块

本模块提供了基于 [SnailJob](https://github.com/aizuda/snail-job) 的分布式定时任务功能。

## 模块结构

- `foxden-common-job`: SnailJob 客户端自动配置
- `foxden-domain-job`: SnailJob 实体定义（Jimmer ORM）
- `foxden-app-job`: 任务执行器实现

## 配置

在 `application-dev.yaml` 中已添加以下配置：

```yaml
snail-job:
  enabled: true
  group: "foxden_group"
  token: "SJ_cKqBTPzCsWA3VyuCfFoccmuIEGXjr5KT"
  server:
    host: 127.0.0.1
    port: 17888
  namespace: dev
  port: 2${server.port}  # 212003
```

## 可用的任务执行器

### 业务任务

| 执行器名称 | 说明 |
|-----------|------|
| `alipayBillTask` | 支付宝账单任务（DAG工作流示例） |
| `wechatBillTask` | 微信账单任务（DAG工作流示例） |
| `summaryBillTask` | 汇总账单任务（DAG工作流示例） |

### 测试/演示任务

| 执行器名称 | 说明 | 任务类型 |
|-----------|------|----------|
| `demoJobTask` | 演示任务 | 基础任务 |
| `testJobExecutor` | 注解式任务示例 | 基础任务 |
| `testBroadcastJob` | 广播任务示例（随机数测试） | 广播任务 |
| `testClassJobExecutor` | 继承式任务示例 | 基础任务 |
| `testMapReduceAnnotation1` | MapReduce任务（分片+合并） | MapReduce |
| `testStaticShardingJob` | 静态分片任务（按ID范围处理） | 静态分片 |

## 创建新的定时任务

1. 在 `foxden-app-job/src/main/kotlin/com/github/alphafoxz/foxden/app/job/snailjob/` 下创建新的类

2. 使用 `@JobExecutor` 注解标注任务执行器：

```kotlin
@Component
@JobExecutor(name = "myCustomTask")
class MyCustomTask {

    fun jobExecute(jobArgs: JobArgs): ExecuteResult {
        // 任务逻辑
        SnailJobLog.REMOTE.info("执行自定义任务")

        // 返回执行结果
        return ExecuteResult.success("任务执行成功")
    }
}
```

3. 在 SnailJob 管理后台创建任务并关联到 `foxden_group` 组

## DAG 工作流示例

本项目包含一个完整的 DAG 工作流示例，展示如何通过上下文传递数据：

1. `alipayBillTask` - 生成支付宝账单数据并写入上下文
2. `wechatBillTask` - 生成微信账单数据并写入上下文
3. `summaryBillTask` - 从上下文读取两个账单数据并汇总

## 任务类型说明

### 基础任务
最简单的任务类型，直接实现业务逻辑：
- `demoJobTask`
- `testJobExecutor` (注解式)
- `testClassJobExecutor` (继承式)

### 广播任务
广播任务会同时在所有在线客户端执行：
- `testBroadcastJob` - 演示如何在多个客户端间协调

### MapReduce 任务
适用于大数据量处理，支持分片和结果合并：
- `testMapReduceAnnotation1` - 演示 Map 分片 + Reduce 合并
  - Map阶段：将 1-200 的数字分成每组 50 个
  - Reduce阶段：汇总所有分片的结果

### 静态分片任务
根据服务端配置的参数进行分片处理：
- `testStaticShardingJob` - 按ID范围分片处理
  - 任务参数示例：`1,1000` (处理 ID 1-1000)

## 注意事项

- SnailJob 服务端需要独立部署和运行
- 确保 PostgreSQL 数据库中的 SnailJob 表已创建
- 任务组 `foxden_group` 需要在 SnailJob 管理后台预先创建
- 任务执行器名称必须与 SnailJob 后台配置的执行器名称一致

## 参考文档

- [SnailJob 官方文档](https://snailjob.opensnail.com)
- [SnailJob GitHub](https://github.com/aizuda/snail-job)
