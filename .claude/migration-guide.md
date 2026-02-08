# FoxDen Kotlin 迁移指南

## 项目概述

FoxDen 是一个多租户 SaaS 系统，正从 Java 迁移至 Kotlin。项目采用**等效重写**策略，使用 Kotlin 最佳实践和现代技术栈。

### 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Kotlin | 2.3.0 | ✅ 已迁移 |
| Gradle | Kotlin DSL | ✅ 已迁移 |
| Jimmer | 0.10.6 | ORM框架 |
| Spring Boot | 3.4.1 | Web框架 |
| Database | PostgreSQL (生产) / H2 (开发) | 数据库 |
| Redis | Redisson 3.35.0 | 缓存 |
| Sa-Token | 1.44.0 | 安全框架 |

---

## 项目结构

```
foxden/
├── foxden-bom/                      # BOM 依赖管理
├── foxden-common/                   # 通用模块
│   ├── foxden-common-core/         # 核心工具、常量、异常
│   ├── foxden-common-jimmer/       # Jimmer ORM 公共工具
│   ├── foxden-common-web/          # Web 工具（验证码、XSS等）
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
│   └── foxden-common-encrypt/      # API 加密/解密 ✅ 新增
├── foxden-domain/                   # 领域模块
│   ├── foxden-domain-system/       # 系统域（用户、角色、菜单等）
│   └── foxden-domain-tenant/       # 租户域
└── foxden-app/                      # 应用模块
    ├── foxden-app-admin/           # 管理端应用（端口12003）
    └── foxden-app-system/          # 系统管理控制器
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
```

### 运行应用

```bash
# 启动管理端应用
./gradlew :foxden-app:foxden-app-admin:bootRun

# 停止应用
pkill -f "foxden-app-admin"

# 停止 Gradle 守护进程
./gradlew --stop
```

### 应用访问

- **管理端**: http://localhost:12003
- **H2控制台**: http://localhost:12003/h2-console
- **API文档**: http://localhost:12003/swagger-ui.html
- **健康检查**: http://localhost:12003/actuator/health

---

## 配置说明

### 环境配置

**Active Profile**: `dev` (默认)

**开发环境数据源**:
- 数据库: PostgreSQL `localhost:12001/postgres`
- Redis: `localhost:11002` (Docker容器名: kangbao-redis)
- 端口: 12003

### 关键配置修复

#### 1. Redisson配置

**问题**: `RedissonClient` bean 未初始化

**解决方案**: 在 `application.yaml` 中移除 `RedissonAutoConfigurationV2` 排除项

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
      # 移除了 org.redisson.spring.starter.RedissonAutoConfigurationV2
```

#### 2. 组件扫描配置

**问题**: `foxden-app-system` 的 Controller 未被扫描

**解决方案**: 在 `FoxdenAdminApplication.kt` 中添加 system 包扫描

```kotlin
@ComponentScan(basePackages = [
    "com.github.alphafoxz.foxden.app.admin",
    "com.github.alphafoxz.foxden.app.system",  // 添加此行
    "com.github.alphafoxz.foxden.domain",
    "com.github.alphafoxz.foxden.common"
])
```

#### 3. 模块依赖配置

**问题**: `foxden-app-system` 未包含在项目中

**解决方案**:
1. 在 `settings.gradle.kts` 中声明模块:
```kotlin
include("foxden-app:foxden-app-system")
```

2. 在 `foxden-app-admin/build.gradle.kts` 中添加依赖:
```kotlin
implementation(project(":foxden-app:foxden-app-system"))
```

---

## API 测试记录

### 正常工作的 API

| 端点 | 方法 | 状态 | 说明 |
|------|------|------|------|
| `/actuator/health` | GET | ✅ | 健康检查 |
| `/auth/code` | GET | ✅ | 获取验证码 |
| `/auth/tenant/list` | GET | ✅ | 租户列表（已禁用限流） |
| `/system/user/list` | GET | ✅ | 用户列表 |
| `/system/role/list` | GET | ✅ | 角色列表 |
| `/system/tenant/list` | GET | ✅ | 租户管理 |
| `/monitor/cache` | GET | ✅ | 缓存监控 |
| `/v3/api-docs` | GET | ✅ | OpenAPI 文档 |
| `/swagger-ui.html` | GET | ✅ | Swagger UI |

### 已知问题

#### 登录 API - 加密请求配置

**端点**: `POST /auth/login`

**状态**: ✅ 加密模块已迁移，需要配置 RSA 密钥

**使用方式**:
1. 配置 `api-decrypt.publicKey` 和 `api-decrypt.privateKey`
2. 前端使用 RSA 公钥加密 AES 密钥
3. 前端使用 AES 密钥加密请求体
4. 将加密后的 AES 密钥放在 `Encrypt-Flag` 请求头中
5. 将加密后的请求体放在请求 body 中

**示例密钥生成**:
```kotlin
val keyPair = EncryptUtils.generateRsaKey()
val publicKey = keyPair["publicKey"]   // 配置到 api-decrypt.publicKey
val privateKey = keyPair["privateKey"] // 配置到 api-decrypt.privateKey
```

---

## 最近修复的问题

### 1. 限流异常修复 (2025-02-09)

**问题**: 频繁触发限流，导致API无法访问

**解决方案**:
- 禁用 `AuthController.tenantList()` 的 `@RateLimiter` 注解（开发环境）
- 清除Redis中的限流记录

**修改文件**: `foxden-app-admin/src/main/kotlin/.../controller/AuthController.kt:99`

```kotlin
// 开发环境禁用限流
// @RateLimiter(time = 60, count = 100, limitType = LimitType.IP)
@GetMapping("/tenant/list")
fun tenantList(request: HttpServletRequest): R<LoginTenantVo>
```

### 2. 登录接口JSON反序列化修复 (2025-02-09)

**问题**:
```
Cannot construct instance of LoginBody: no String-argument constructor
```

**解决方案**: 修改登录接口参数类型

**修改前**:
```kotlin
@PostMapping("/login")
fun login(@RequestBody body: String): R<LoginVo> {
    val loginBody = JsonUtils.parseObject(body, LoginBody::class.java)
    // ...
}
```

**修改后** (`AuthController.kt:47-70`):
```kotlin
@PostMapping("/login")
fun login(@RequestBody loginBody: LoginBody): R<LoginVo> {
    ValidatorUtils.validate(loginBody)

    val clientId = loginBody.clientId!!
    val grantType = loginBody.grantType!!

    // ... 业务逻辑 ...

    // 登录 - 将对象序列化回JSON传给策略
    val bodyJson = JsonUtils.toJsonString(loginBody)!!
    val loginVo = AuthStrategy.login(bodyJson, client, grantType)

    return R.ok(loginVo)
}
```

**新增导入**:
```kotlin
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
```

### 3. 模块集成完成 (2025-02-09)

**完成的配置**:
- ✅ `foxden-app-system` 添加到 `settings.gradle.kts`
- ✅ `foxden-app-system` 添加到 `foxden-app-admin` 依赖
- ✅ `foxden-app-system` 添加到 `@ComponentScan`
- ✅ System 模块 Controller 正常加载

### 4. Redisson 初始化修复 (2025-02-09)

**问题**: `RedissonClient` bean 未创建

**解决方案**: 移除 `application.yaml` 中的 `RedissonAutoConfigurationV2` 排除项

**结果**:
- ✅ Redis 连接成功（8个连接初始化）
- ✅ 限流功能可用
- ✅ 缓存功能可用

### 5. 加密模块迁移完成 (2025-02-09)

**背景**: 前端发送的登录请求使用 RSA+AES 混合加密，需要迁移 `ruoyi-common-encrypt` 模块

**迁移内容**:
- ✅ 注解：`@ApiEncrypt`、`@EncryptField`
- ✅ 配置：`ApiDecryptProperties`、`EncryptAutoConfiguration`
- ✅ 工具：`EncryptUtils`（支持 AES、RSA、SM2、SM3、SM4）
- ✅ 过滤器：`CryptoFilter`、`DecryptRequestBodyWrapper`、`EncryptResponseBodyWrapper`

**加密流程**:
```
前端 → [AES加密请求体] → [RSA加密AES密钥] → 后端
     ↓
[CryptoFilter拦截]
     ↓
[RSA解密AES密钥] → [AES解密请求体] → [Controller处理]
```

**配置示例** (`application.yaml`):
```yaml
api-decrypt:
  enabled: true
  headerFlag: encrypt-key  # 请求头标识（与前端一致）
  publicKey: <RSA公钥>      # 响应加密公钥
  privateKey: <RSA私钥>     # 请求解密私钥
```

**使用示例** (`AuthController.kt`):
```kotlin
@PostMapping("/login")
fun login(@RequestBody body: String): R<LoginVo> {
    // body 已被 CryptoFilter 自动解密（如果是加密请求）
    val loginBody = JsonUtils.parseObject(body, LoginBody::class.java)
    // ...
}
```

**修改文件**:
- `foxden-common/foxden-common-encrypt/` (新建模块)
- `foxden-app-admin/src/main/resources/application.yaml` (添加配置)
- `foxden-app-admin/.../AuthController.kt` (支持混合加密/明文请求)

### 6. Jimmer KSqlClient 配置修复 (2025-02-09) ✅

**问题**: 启动时报错 `ConnectionManagerDsl has not be proceeded`

**根本原因**: 手动创建了 KSqlClient Bean，与 Spring Boot Starter 自动配置冲突

**错误配置**:
```kotlin
// ❌ JimmerKSqlClientConfig.kt (已删除)
@Configuration
class JimmerKSqlClientConfig {
    @Bean
    fun kSqlClient(dataSource: DataSource): KSqlClient {
        return newKSqlClient {
            setConnectionManager {
                dataSource.connection  // ❌ ConnectionManagerDsl 错误
            }
        }
    }
}
```

**解决方案**:

1. **删除手动配置文件**
   ```bash
   rm foxden-app-admin/src/main/.../config/JimmerKSqlClientConfig.kt
   ```

2. **添加 Jimmer 语言配置**
   ```yaml
   # application.yaml 和 application-dev.yaml
   jimmer:
     language: kotlin  # 关键配置！指示创建 KSqlClient
   ```

3. **直接注入使用**
   ```kotlin
   @RestController
   class UserController(
       private val sqlClient: KSqlClient  // ✅ Spring Boot Starter 自动注入
   ) {
       @GetMapping("/users")
       fun getUsers(): List<User> {
           return sqlClient.createQuery(User::class) {
               select(table)
           }.execute()
       }
   }
   ```

**技术原理**:

`jimmer-spring-boot-starter` 根据 `jimmer.language` 自动配置：

| 配置值 | 创建的 Bean | 支持的语言特性 |
|--------|-------------|----------------|
| `kotlin` | `KSqlClient` | Kotlin DSL、协程 |
| `java` 或未配置 | `JSqlClient` | Java API |

**参考文档**:
- 官方文档: https://babyfish-ct.github.io/jimmer-doc/
- Spring Boot 集成: https://www.cnblogs.com/poifa/p/16667568.html
- 详细指南: `.claude/jimmer-dsl-guide.md`

**修改文件**:
- ✅ 删除 `JimmerKSqlClientConfig.kt`
- ✅ 更新 `application.yaml` (添加 `jimmer.language: kotlin`)
- ✅ 更新 `application-dev.yaml` (添加 `jimmer.language: kotlin`)

---

## 开发注意事项

### WSL2 文件系统 I/O 限制

在 WSL2 的 `/mnt/f/` 路径上运行 Gradle 可能出现 I/O 错误。

**解决方案**:
```bash
# 停止所有 Gradle 进程
pkill -9 -f gradle
./gradlew --stop

# 清理并重建
./gradlew clean build
```

### 依赖版本管理

所有依赖版本在 `foxden-bom/build.gradle.kts` 中统一管理，业务模块不应硬编码版本号。

### Kotlin 代码规范

- ✅ 使用 `data class`
- ✅ 明确可空性标注（`String?`）
- ✅ 默认使用 `val`，必要时使用 `var`
- ✅ 使用扩展函数增强可读性
- ✅ 使用属性赋值而非 setter 方法

---

## 待办事项

### 高优先级

#### 1. Jimmer KSqlClient 配置完善 ✅ 已解决 (2025-02-09)

**问题**: `ConnectionManagerDsl has not be proceeded`

**根本原因**: 手动创建了 KSqlClient Bean，但 `jimmer-spring-boot-starter` 已自动配置

**解决方案**: 删除手动配置，使用 Spring Boot Starter 自动配置

**关键发现** (参考 https://www.cnblogs.com/poifa/p/16667568.html):

```yaml
# application.yaml
jimmer:
  language: kotlin    # 关键配置：指示starter创建KSqlClient而非JSqlClient
  show-sql: true
```

```kotlin
// 直接注入使用，无需手动创建Bean
class UserController(
    private val sqlClient: KSqlClient  // Spring Boot Starter自动创建
) { }

// ❌ 错误：手动创建Bean导致冲突
// @Bean
// fun kSqlClient(dataSource: DataSource): KSqlClient { ... }
```

**修改文件**:
- 删除 `JimmerKSqlClientConfig.kt` (已废弃)
- 确保配置文件中设置 `jimmer.language: kotlin`

#### 2. 登录功能完整实现

**依赖**: Jimmer 配置修复后

**包含**:
- 用户名密码登录
- 验证码验证
- Token 生成

### 中优先级

#### 3. 数据库初始化

**H2 开发数据库**:
- 表结构初始化脚本
- 初始数据导入

**PostgreSQL 生产数据库**:
- 数据库迁移脚本
- 数据备份恢复

#### 4. 完善业务逻辑

**Service 层实现**:
- 从 Java 迁移完整业务逻辑
- 实现 Jimmer DSL 查询
- 数据权限、缓存等功能

---

## 参考资源

- [Kotlin 官方文档](https://kotlinlang.org/docs/)
- [Jimmer 官方文档](https://babyfish-ct.github.io/jimmer-doc/)
- [Spring Boot 3.x 文档](https://docs.spring.io/spring-boot/)
- [Gradle Kotlin DSL 指南](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
- [Sa-Token 文档](https://sa-token.cc/)
- [Redisson 文档](https://github.com/redisson/redisson/wiki)
