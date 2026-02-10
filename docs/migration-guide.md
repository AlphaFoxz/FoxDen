# FoxDen Kotlin 迁移指南

## 项目概述

FoxDen 是一个多租户 SaaS 系统，从若依（RuoYi）框架迁移至 Kotlin + Jimmer ORM 架构。项目采用**等效重写**策略，使用 Kotlin 最佳实践和现代技术栈。

### 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Kotlin | 2.3.0 | 主要开发语言 |
| Gradle | Kotlin DSL | 构建工具 |
| Jimmer | 0.10.6 | Kotlin 优先的 ORM 框架 |
| Spring Boot | 3.5.10 | Web 框架 |
| Database | PostgreSQL | 主数据库 |
| Redis | Redisson 3.35.0 | 缓存和分布式锁 |
| Sa-Token | 1.44.0 | 认证授权框架 |

---

## 项目结构

```
foxden/
├── foxden-bom/                      # BOM 依赖管理
├── foxden-common/                   # 通用模块
│   ├── foxden-common-core/         # 核心工具、常量、异常
│   ├── foxden-common-jimmer/       # Jimmer ORM 公共工具和 Traits
│   ├── foxden-common-web/          # Web 工具（验证码、XSS、i18n）
│   ├── foxden-common-security/     # Sa-Token 集成
│   ├── foxden-common-redis/        # Redis 缓存（Redisson）
│   ├── foxden-common-log/          # 日志注解和事件
│   ├── foxden-common-mail/         # 邮件功能
│   ├── foxden-common-sms/          # 短信功能
│   ├── foxden-common-social/       # 社交登录（JustAuth）
│   ├── foxden-common-ratelimiter/  # 限流
│   ├── foxden-common-idempotent/   # 幂等性
│   ├── foxden-common-excel/        # Excel 导入导出
│   ├── foxden-common-oss/          # 对象存储
│   ├── foxden-common-doc/          # SpringDoc API文档
│   ├── foxden-common-json/         # JSON 配置
│   └── foxden-common-encrypt/      # API 加密/解密（RSA+AES）
├── foxden-domain/                   # 领域模块
│   ├── foxden-domain-system/       # 系统域（用户、角色、菜单等）
│   ├── foxden-domain-tenant/       # 租户域
│   ├── foxden-domain-workflow/     # 工作流域
│   ├── foxden-domain-gen/          # 代码生成域
│   ├── foxden-domain-test/         # 测试域
│   └── foxden-domain-infrastructure/ # 基础设施服务
└── foxden-app/                      # 应用模块
    ├── foxden-app-admin/           # 管理端应用（端口12003）
    ├── foxden-app-system/          # 系统管理控制器
    └── foxden-app-workflow/        # 工作流控制器
```

---

## 快速开始

### 构建项目

```bash
# 完整构建
./gradlew build

# 仅编译
./gradlew compileKotlin

# 清理构建
./gradlew clean

# 停止 Gradle 守护进程
./gradlew --stop
```

### 运行应用

```bash
# 启动管理端应用
./gradlew :foxden-app:foxden-app-admin:bootRun
```

### 应用访问

- **管理端**: http://localhost:12003
- **API文档**: http://localhost:12003/swagger-ui.html
- **健康检查**: http://localhost:12003/actuator/health

---

## 配置说明

### 环境配置

**Active Profile**: `dev` (默认)

**开发环境数据源**:
- 数据库: PostgreSQL `localhost:12001/postgres`
- Redis: `localhost:11002`
- 管理端端口: 12003

### 关键配置项

#### 1. Jimmer ORM 配置

**application.yaml** 和 **application-dev.yaml**:

```yaml
jimmer:
  dialect: org.babyfish.jimmer.sql.dialect.PostgresDialect
  language: kotlin              # 重要：指示创建 KSqlClient
  show-sql: true                # 开发环境打印SQL
  pretty-sql: true              # 美化SQL输出
  executor-context-cache-enabled: true
```

**使用方式** - 直接注入 `KSqlClient`:

```kotlin
@Service
class UserService(
    private val sqlClient: KSqlClient  // Spring Boot Starter 自动注入
) {
    fun findAll(): List<User> {
        return sqlClient.createQuery(User::class) {
            where(table.delFlag eq "0")
            select(table)
        }.execute()
    }
}
```

#### 2. 数据库连接池配置

```yaml
spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:12001/postgres
    username: postgres
    password: postgres
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      auto-commit: false
      idle-timeout: 600000
      max-lifetime: 1800000
```

#### 3. Redisson 配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 11002
      database: 0
      timeout: 10s
      lettuce:
        pool:
          min-idle: 0
          max-idle: 8
          max-active: 8
```

#### 4. API 加密配置（可选）

```yaml
api-decrypt:
  enabled: true
  headerFlag: encrypt-key    # 请求头标识
  publicKey: <RSA公钥>      # 响应加密公钥
  privateKey: <RSA私钥>     # 请求解密私钥
```

---

## Jimmer 实体定义规范

### Trait 组合模式

所有实体都由可复用的 Trait 组合而成：

| Trait | 提供字段 | 说明 |
|-------|---------|------|
| `CommId` | `id: Long` | 主键（自增） |
| `CommTenant` | `tenantId: String` | 多租户支持 |
| `CommInfo` | `createDept`, `createBy`, `createTime`, `updateBy`, `updateTime` | 审计字段 |
| `CommDelFlag` | `delFlag: String` | 逻辑删除（"0"存在/"1"删除） |

### 实体定义示例

```kotlin
@Entity
@Table(name = "sys_user")
interface SysUser : CommDelFlag, CommId, CommInfo, CommTenant {
    val userName: String
    val nickName: String
    val email: String?

    @OnDissociate(DissociateAction.DELETE)
    val password: String?

    @ManyToMany
    @JoinTable(name = "sys_user_role")
    val roles: List<SysRole>
}
```

### 主键列名映射

对于非标准主键列名，使用 `@Column` 注解：

```kotlin
@Entity
@Table(name = "flow_category")
interface FlowCategory : CommDelFlag, CommInfo, CommTenant {
    @Column(name = "category_id")
    @Id
    @GeneratedValue
    val id: Long

    val categoryName: String
}
```

---

## 开发注意事项

### WSL2 文件系统 I/O 限制

在 WSL2 的 `/mnt/f/` 路径上运行 Gradle 可能出现 I/O 错误。

**解决方案**:
```bash
# 停止所有 Gradle 进程
./gradlew --stop

# 清理并重建
./gradlew clean build
```

### 依赖版本管理

所有依赖版本在 `foxden-bom/build.gradle.kts` 中统一管理，业务模块不应硬编码版本号。

### Kotlin 代码规范

- ✅ 使用 `data class` for DTO/VO
- ✅ 明确可空性标注（`String?`）
- ✅ 默认使用 `val`，必要时使用 `var`
- ✅ 使用扩展函数增强可读性
- ✅ 使用属性赋值而非 setter 方法
- ✅ 避免使用 `!!` 非空断言

### Jimmer DSL 类型推断

部分 Jimmer DSL 查询可能存在类型推断问题，解决方案：

```kotlin
// ✅ 明确指定类型
val users: List<User> = sqlClient.createQuery(User::class) {
    select(table)
}.execute()

// ✅ 或者使用 fetchOneOrNull
val user = sqlClient.findById(User::class, id)
```

---

## 迁移文档

- **[Jimmer 使用指南](./JIMMER_GUIDE.md)** - Jimmer ORM 详细使用说明
- **[迁移状态](./MIGRATION_STATUS.md)** - 当前迁移进度
- **[旧版本指南](./OLD_VERSION_GUIDE.md)** - 旧项目结构说明
- **[工作流迁移](./WORKFLOW_MIGRATION.md)** - 工作流模块迁移文档

---

## 参考资源

### 官方文档
- [Kotlin 官方文档](https://kotlinlang.org/docs/)
- [Jimmer 官方文档](https://babyfish-ct.github.io/jimmer-doc/)
- [Spring Boot 3.x 文档](https://docs.spring.io/spring-boot/)
- [Gradle Kotlin DSL 指南](https://docs.gradle.org/current/userguide/kotlin_dsl.html)

### 框架文档
- [Sa-Token 文档](https://sa-token.cc/)
- [Redisson 文档](https://github.com/redisson/redisson/wiki)
- [WarmFlow 文档](https://warm-flow.github.io/warm-flow-docs/)

---

## 许可证

Copyright © 2025 FoxDen Team
