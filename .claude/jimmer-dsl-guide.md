# Jimmer ORM 配置指南

## 概述

Jimmer 是一个面向 Java/Kotlin 的现代 ORM 框架，采用预编译技术，在编译时而非运行时进行大部分处理。

**官方文档**:
- 英文: https://babyfish-ct.github.io/jimmer-doc/
- 中文: https://babyfish-ct.github.io/jimmer-doc/zh/
- GitHub: https://github.com/babyfish-ct/jimmer
- 示例代码: https://github.com/babyfish-ct/jimmer-examples

## Spring Boot 集成

### 核心原则：使用 Starter 自动配置

**❌ 错误做法：手动创建 KSqlClient Bean**

```kotlin
// 不要这样做！
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

**✅ 正确做法：让 Spring Boot Starter 自动配置**

```yaml
# application.yaml
jimmer:
  language: kotlin    # 关键配置！指示创建 KSqlClient (Kotlin) 而非 JSqlClient (Java)
  dialect: org.babyfish.jimmer.sql.dialect.PostgresDialect
  show-sql: true
  pretty-sql: true
  executor-context-level: DISABLED
  client:
    path: /jimmer
```

```kotlin
// 直接注入使用，无需手动配置
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

### 配置说明

| 配置项 | 说明 | 可选值 |
|--------|------|--------|
| `jimmer.language` | **最重要**：指定语言类型 | `kotlin` / `java` |
| `jimmer.dialect` | 数据库方言 | `H2Dialect`, `PostgresDialect`, `MySQLDialect` 等 |
| `jimmer.show-sql` | 打印 SQL | `true` / `false` |
| `jimmer.pretty-sql` | 格式化 SQL | `true` / `false` |
| `jimmer.executor-context-level` | 执行器上下文级别 | `DISABLED`, `SESSION`, `STATEMENT` |

### 自动配置原理

`jimmer-spring-boot-starter` 根据 `jimmer.language` 配置自动：

1. **Kotlin 项目** (`language: kotlin`)
   - 创建 `KSqlClient` Bean
   - 启用 Kotlin DSL 支持
   - 支持协程

2. **Java 项目** (`language: java` 或未配置)
   - 创建 `JSqlClient` Bean
   - 使用 Java 风格 API

## 实体定义

### 基础实体

Jimmer 使用**接口**而非类定义实体：

```kotlin
import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

@Entity
@Table(name = "sys_user")
interface SysUser : CommDelFlag, CommId, CommInfo, CommTenant {
    val userName: String
    val nickName: String
    val email: String?
    val phonenumber: String?
    val sex: String?
    val avatar: String?
    val password: String?

    @ManyToMany
    @JoinTable(name = "sys_user_role",
        joinColumnName = "user_id",
        inverseJoinColumnName = "role_id"
    )
    val roles: List<SysRole>

    @ManyToOne
    @JoinColumn(name = "dept_id")
    val dept: SysDept?
}
```

### Traint 设计模式

Jimmer 使用可复用的 trait 接口组合实体：

```kotlin
// foxden-common-jimmer/src/main/.../entity/comm/CommId.kt
@MappedSuperclass
interface CommId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long
}

// foxden-common-jimmer/src/main/.../entity/comm/CommTenant.kt
@MappedSuperclass
interface CommTenant {
    val tenantId: String
}

// foxden-common-jimmer/src/main/.../entity/comm/CommInfo.kt
@MappedSuperclass
interface CommInfo {
    val createDept: Long?
    val createBy: Long?
    val createTime: LocalDateTime?
    val updateBy: Long?
    val updateTime: LocalDateTime?
    val remark: String?
}

// foxden-common-jimmer/src/main/.../entity/comm/CommDelFlag.kt
@MappedSuperclass
interface CommDelFlag {
    @LogicalDeleted("true")
    val delFlag: Boolean
}
```

**使用 trait 组合实体**:
```kotlin
@Entity
@Table(name = "sys_user")
interface SysUser : CommDelFlag, CommId, CommInfo, CommTenant {
    // 实体特定字段
}
```

## 查询操作

### 基础查询

```kotlin
// 查询所有用户
val users = sqlClient.createQuery(SysUser::class) {
    select(table)
}.execute()

// 条件查询
val users = sqlClient.createQuery(SysUser::class) {
    where(table.userName eq "admin")
    select(table)
}.execute()

// 多条件
val users = sqlClient.createQuery(SysUser::class) {
    where(
        table.userName.like("%admin%"),
        table.status.eq(true)
    )
    select(table)
}.execute()
```

### 使用 Fetcher 部分加载

Jimmer 的 Fetcher 机制避免 N+1 查询：

```kotlin
// 定义 Fetcher
val userFetcher = SysUserFetcher.$
    .userName()
    .nickName()
    .email()
    .dept {
        deptName()
    }
    .roles {
        roleName()
    }

// 使用 Fetcher 查询
val users = sqlClient.createQuery(SysUser::class) {
    select(table.fetch(userFetcher))
}.execute()

// 生成的 SQL（仅一次查询）：
// SELECT u.id, u.userName, u.nickName, u.email,
//        d.id, d.deptName,
//        r.id, r.roleName
// FROM sys_user u
// LEFT JOIN sys_dept d ON u.dept_id = d.id
// LEFT JOIN sys_user_role ur ON u.id = ur.user_id
// LEFT JOIN sys_role r ON ur.role_id = r.id
```

### 分页查询

```kotlin
val users = sqlClient.createQuery(SysUser::class) {
    where(table.userName.like("%admin%"))
    select(table)
}.fetchPage(pageNum - 1, pageSize)
```

### 排序

```kotlin
val users = sqlClient.createQuery(SysUser::class) {
    orderBy(table.createTime.desc())
    select(table)
}.execute()
```

## 保存操作

### 插入

```kotlin
val newUser = ImmutableSysUser.newBuilder {
    userName = "newuser"
    nickName = "New User"
    email = "newuser@example.com"
    // ... 其他字段
}.build()

sqlClient.insert(newUser)
```

### 更新

```kotlin
val updatedUser = sqlClient.findById(SysUser::class, userId)?.let {
    ImmutableSysUser.$ {
        it.userName // 保留原值
        nickName = "Updated Nickname"
        email = "updated@example.com"
    }
}

sqlClient.update(updatedUser)
```

### 保存命令（Save Command）

```kotlin
// 简单保存
val saved = sqlClient.entities.save(newUser)

// 深度保存（包含关联）
val saved = sqlClient.entities.save(
    ImmutableSysUser.newBuilder {
        userName = "newuser"
        dept = ImmutableSysDept.newBuilder {
            deptId = 1L
        }
    }.build(),
    AutoDepthOfSave.ROOT_ONLY  // 保存策略
)
```

## 扩展函数

项目中定义的扩展函数简化常见操作：

```kotlin
// foxden-domain-system/src/main/.../service/extensions/ServiceExtensions.kt

// 根据 ID 查询
fun <E : Any> KSqlClient findById(
    entityType: KClass<E>,
    id: Long
): E? = ...

// 查询列表
fun <E : Any> KSqlClient queryList(
    entityType: KClass<E>,
    where: (KMutableTableImplementor<E>) -> Unit
): List<E> = ...

// 分页查询
fun <E : Any> KSqlClient queryPage(
    entityType: KClass<E>,
    pageNum: Int,
    pageSize: Int,
    where: (KMutableTableImplementor<E>) -> Unit
): Page<E> = ...
```

**使用示例**:
```kotlin
// 使用扩展函数
val user = sqlClient.findById(SysUser::class, userId)

val users = sqlClient.queryList(SysUser::class) {
    where(table.status.eq(true))
}

val page = sqlClient.queryPage(SysUser::class, 1, 10) {
    where(table.userName.like("%admin%"))
}
```

## 常见问题

### 1. ConnectionManagerDsl 错误

**错误信息**:
```
IllegalStateException: ConnectionManagerDsl has not be proceeded
```

**原因**: 手动创建了 KSqlClient Bean，但配置不正确

**解决方案**: 删除手动配置，使用 Spring Boot Starter 自动配置

```kotlin
// ❌ 删除此文件
// JimmerKSqlClientConfig.kt

// ✅ 确保配置正确
// application.yaml
jimmer:
  language: kotlin  # 必须设置！
```

### 2. 类型推断错误

**问题**: Jimmer DSL 中的类型推断有时需要显式指定

**解决方案**:
```kotlin
// 明确指定类型
where(table.userName eq "admin")  // 而非 eq("admin")
```

### 3. KSP 生成的代码找不到

**原因**: 未配置 KSP 生成目录

**解决方案**: 在 `build.gradle.kts` 中添加：

```kotlin
kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}
```

### 4. Fetcher 定义错误

**问题**: Fetcher 未正确导入或生成

**解决方案**:
```kotlin
// 确保 KSP 已运行
./gradlew :foxden-domain:foxden-domain-system:kspKotlin

// 正确导入
import com.github.alphafoxz.foxden.domain.system.entity.SysUserFetcher
```

## 最佳实践

### 1. 始终使用 Fetcher

```kotlin
// ✅ 推荐：使用 Fetcher
val user = sqlClient.findById(SysUser::class, id, SysUserFetcher.$.allScalar())

// ❌ 避免：不使用 Fetcher（可能触发 N+1 查询）
val user = sqlClient.findById(SysUser::class, id)
```

### 2. 利用扩展函数

```kotlin
// ✅ 使用扩展函数
sqlClient.findById(SysUser::class, id)

// ❌ 避免重复代码
sqlClient.createQuery(SysUser::class) {
    where(table.id eq id)
    select(table)
}.execute().firstOrNull()
```

### 3. 使用类型安全的 DSL

```kotlin
// ✅ 类型安全
where(table.userName eq "admin")

// ❌ 避免字符串
where("user_name = 'admin'")
```

### 4. 合理使用事务

```kotlin
@Service
class UserService {
    @Transactional
    fun createUserWithRoles(user: SysUser, roleIds: List<Long>) {
        // 事务内的多个操作
        val savedUser = sqlClient.insert(user)
        // ... 分配角色
    }
}
```

## 参考资料

- **官方文档**: https://babyfish-ct.github.io/jimmer-doc/zh/
- **Spring Boot 集成示例**: https://www.cnblogs.com/poifa/p/16667568.html
- **GitHub 仓库**: https://github.com/babyfish-ct/jimmer
- **示例代码**: https://github.com/babyfish-ct/jimmer-examples
