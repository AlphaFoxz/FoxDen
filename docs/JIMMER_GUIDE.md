# Jimmer ORM 使用指南

> 本文档旨在帮助 AI 理解和使用 Jimmer ORM 框架
> 官方文档：https://babyfish-ct.github.io/jimmer-doc/zh

## 目录

### 基础知识
- [什么是 Jimmer](#什么是-jimmer)
- [Jimmer vs 传统 ORM](#jimmer-vs-传统-orm)
- [核心概念](#核心概念)
- [1. 实体（Entity）](#1-实体entity)
- [2. 三大核心功能](#2-三大核心功能)
- [3. Trait 设计模式](#3-trait-设计模式)

### 配置与定义
- [Spring Boot 集成配置](#spring-boot-集成配置)
- [核心原则：使用 Starter 自动配置](#核心原则使用-starter-自动配置)
- [实体定义](#实体定义)
- [常用注解](#常用注解)
- [关联行为（@OnDissociate）](#关联行为ondissociate)
- [Trait 复用（FoxDen 项目）](#trait-复用foxden-项目)

### 查询操作
- [查询数据](#查询数据)
- [1. 使用 SQL Client 查询](#1-使用-sql-client-查询)
- [2. 使用 Fetcher 控制查询形状](#2-使用-fetcher-控制查询形状)
- [创建 Fetcher 的正确方式](#创建-fetcher-的正确方式)
- [递归 Fetcher（树形结构）](#递归-fetcher树形结构)
- [3. 动态查询](#3-动态查询)
- [4. 分页查询](#4-分页查询)

### 保存操作
- [保存数据](#保存数据)
- [Jimmer Draft API](#jimmer-draft-api)
- [插入新对象](#插入新对象)
- [更新现有对象](#更新现有对象)
- [更新单个字段](#更新单个字段)
- [关联保存](#关联保存)
- [重要注意事项](#重要注意事项)

### 项目应用
- [FoxDen 项目中的 Jimmer 使用](#foxden-项目中的-jimmer-使用)
- [1. 项目特点](#1-项目特点)
- [2. 实体定义位置](#2-实体定义位置)
- [3. 常用扩展函数](#3-常用扩展函数)
- [4. 分页封装](#4-分页封装)
- [5. 数据权限](#5-数据权限)
- [6. 多租户](#6-多租户)

### 常见模式
- [模式 1：按条件查询列表](#模式-1按条件查询列表)
- [模式 2：检查唯一性](#模式-2检查唯一性)
- [模式 3：批量查询并转换](#模式-3批量查询并转换)
- [模式 4：树形结构查询](#模式-4树形结构查询)

### 注意事项
- [注意事项](#注意事项)
- [常见问题](#常见问题)

### 参考资料
- [参考资料](#参考资料)

---

## 什么是 Jimmer

Jimmer 是 JVM 中最先进的 ORM，同时面向 Java 和 Kotlin。它的核心特点：

1. **为任意形状的数据结构设计** - 不局限于简单实体对象，可直接操作复杂的嵌套数据结构
2. **解决 N+1 问题** - 使用批量加载策略，避免常见的 ORM 性能问题
3. **强类型 DSL** - 支持复杂的多表动态查询，智能优化 SQL
4. **DTO 语言** - 以极低成本自动生成 DTO，解决业务模型与数据模型不一致问题
5. **不可变对象** - 实体为不可变对象，结合 immer 算法实现高效的结构更新

### Jimmer vs 传统 ORM

| 特性 | JPA/Hibernate | MyBatis | Jimmer |
|------|--------------|---------|--------|
| 查询任意形状数据 | ❌ | ⚠️ 需手动映射 | ✅ 原生支持 |
| N+1 问题 | ❌ 常见 | ⚠️ 需优化 | ✅ 智能解决 |
| 动态查询 | ⚠️ Criteria API 复杂 | ⚠️ XML 动态 SQL | ✅ 强类型 DSL |
| DTO 生成 | ❌ 需手动或 MapStruct | ❌ 需手动 | ✅ 自动生成 |
| 类型安全 | ✅ | ❌ | ✅ |

---

## 核心概念

### 1. 实体（Entity）

Jimmer 实体**不是 POJO**，而是 **interface**，由 KSP（Kotlin）或 APT（Java）在编译时生成实现类。

**关键特性：**
- **动态性**：每个属性都可以缺失（值未知），与 null（值已知但为空）完全不同
- **不可变性**：所有对象不可变，保证无循环引用
- **类型安全**：完全的编译时类型检查

### 2. 三大核心功能

1. **Fetcher** - 查询任意形状的数据结构
2. **Save Command** - 保存任意形状的数据结构
3. **SQL DSL** - 强类型的动态 SQL 构建器

### 3. Trait 设计模式

Jimmer 使用 `@MappedSuperclass` trait 实现代码复用，避免重复定义公共字段。

---

## Spring Boot 集成配置

### 核心原则：使用 Starter 自动配置

**❌ 错误做法：手动创建 JSqlClient Bean**

```kotlin
// 不要这样做！
@Configuration
class JimmerJSqlClientConfig {
    @Bean
    fun KSqlClient(dataSource: DataSource): JSqlClient {
        return JSqlClient.newBuilder()
            .setConnectionManager(dataSource.connection)
            .build()
    }
}
```

**✅ 正确做法：让 Spring Boot Starter 自动配置**

```yaml
# application.yaml
jimmer:
  language: kotlin    # 关键配置！
  dialect: org.babyfish.jimmer.sql.dialect.PostgresDialect
  show-sql: true
  pretty-sql: true
```

```kotlin
// 直接注入使用
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

---

## 实体定义

### 基本 Entity 定义

```kotlin
import org.babyfish.jimmer.sql.*
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*

@Entity
@Table(name = "sys_user")
interface SysUser : CommDelFlag, CommId, CommInfo, CommTenant {
    val userName: String
    val nickName: String?
    val email: String?
    val phonenumber: String?
    val status: String?

    // 一对一/多对一关联
    @ManyToOne
    @JoinColumn(name = "dept_id")
    val dept: SysDept?

    // 多对多关联
    @ManyToMany
    @JoinTable(name = "sys_user_role")
    val roles: List<SysRole>
}
```

### 常用注解

| 注解 | 用途 | 示例 |
|------|------|------|
| `@Entity` | 标记实体接口 | `@Entity interface User` |
| `@Table` | 指定表名 | `@Table(name = "sys_user")` |
| `@Id` | 主键 | 在 `CommId` trait 中定义 |
| `@GeneratedValue` | 自增主键 | 在 `CommId` trait 中定义 |
| `@ManyToOne` | 多对一关联 | `@ManyToOne val dept: SysDept` |
| `@ManyToMany` | 多对多关联 | `@ManyToMany val roles: List<SysRole>` |
| `@JoinTable` | 关联表 | `@JoinTable(name = "user_role")` |
| `@OnDissociate` | 关联解除时的行为 | `@OnDissociate(DissociateAction.DELETE)` |
| `@LogicalDeleted` | 逻辑删除字段 | 在 `CommDelFlag` trait 中定义 |

### 关联行为（@OnDissociate）

```kotlin
// 删除关联时级联删除
@OnDissociate(DissociateAction.DELETE)
val password: String?

@OnDissociate(DissociateAction.DELETE)
@ManyToMany
@JoinTable(name = "sys_user_role")
val roles: List<SysRole>
```

### Trait 复用（FoxDen 项目）

```kotlin
// CommId - 主键
@MappedSuperclass
interface CommId {
    @Id
    @GeneratedValue
    val id: Long
}

// CommTenant - 租户
@MappedSuperclass
interface CommTenant {
    val tenantId: String
}

// CommInfo - 审计字段
@MappedSuperclass
interface CommInfo {
    val createDept: Long?
    val createBy: Long?
    val createTime: LocalDateTime?
    val updateBy: Long?
    val updateTime: LocalDateTime?
    val remark: String?
}

// CommDelFlag - 逻辑删除
@MappedSuperclass
interface CommDelFlag {
    @LogicalDeleted("true")
    val delFlag: Boolean
}
```

---

## 查询数据

### 1. 使用 SQL Client 查询

```kotlin
import org.babyfish.jimmer.sql.kt.ast.expression.eq

@Service
class UserService(
    private val sqlClient: KSqlClient
) {
    // 简单查询
    fun findById(id: Long): SysUser? {
        return sqlClient.findById(SysUser::class, id)
    }

    // 条件查询
    fun findByUsername(username: String): SysUser? {
        return sqlClient.createQuery(SysUser::class) {
            where(table.userName eq username)
            select(table)
        }.fetchOneOrNull()
    }

    // 列表查询
    fun findActiveUsers(): List<SysUser> {
        return sqlClient.createQuery(SysUser::class) {
            where(table.status eq "0")
            select(table)
        }.execute()
    }
}
```

### 2. 使用 Fetcher 控制查询形状

**概念**：Fetcher 类似 GraphQL，可以精确控制查询返回的数据结构，避免 N+1 问题。

#### 创建 Fetcher 的正确方式

**重要**：Jimmer 提供了 `newFetcher` 函数来创建 Fetcher！

```kotlin
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher

// ✅ 正确：使用 newFetcher 函数
val USER_FETCHER = newFetcher(SysUser::class).by {
    allScalarFields()
}

// ✅ 正确：带关联的 Fetcher
val USER_WITH_ROLE_FETCHER = newFetcher(SysUser::class).by {
    allScalarFields()
    roles {
        allScalarFields()
    }
}

// ✅ 正确：递归 Fetcher（树形结构）
val MENU_TREE_FETCHER = newFetcher(SysMenu::class).by {
    allScalarFields()
    children {
        allScalarFields()
        children {
            allScalarFields()
        }
    }
}

// ❌ 已弃用：使用生成的单例（不推荐）
val OLD_FETCHER = SysUserFetcher.$  // 仍可使用，但不推荐
```

#### 在查询中使用 Fetcher

```kotlin
// 方式 1：在 findById 中使用 Fetcher
fun getUserWithRoles(userId: Long): SysUser? {
    return sqlClient.findById(
        SysUser::class,
        userId,
        USER_WITH_ROLE_FETCHER
    )
}

// 方式 2：在 createQuery 中使用 table.fetch
fun getMenuTree(): List<SysMenu> {
    return sqlClient.createQuery(SysMenu::class) {
        where(table.parentId.isNull())
        orderBy(table.orderNum.asc())
        select(table.fetch(MENU_TREE_FETCHER))
    }.execute()
}
```

#### 递归 Fetcher（树形结构）

对于菜单树、部门树等递归结构，使用递归语法实现：

```kotlin
// 递归 Fetcher 定义 - 支持无限递归
val MENU_TREE_FETCHER = newFetcher(SysMenu::class).by {
    allScalarFields()
    children {
        allScalarFields()
        children {  // 递归加载所有子级
            allScalarFields()
        }
    }
}

fun getMenuTree(): List<SysMenu> {
    return sqlClient.createQuery(SysMenu::class) {
        where(table.parentId.isNull())
        orderBy(table.orderNum.asc())
        select(table.fetch(MENU_TREE_FETCHER))
    }.execute()
}
```

**关键点**：
- 递归深度受数据库性能限制，建议配合 `WHERE` 条件限制范围
- Jimmer 使用批量加载避免 N+1 问题

### 3. 动态查询

```kotlin
fun searchUsers(
    username: String? = null,
    status: String? = null,
    deptId: Long? = null
): List<SysUser> {
    return sqlClient.createQuery(SysUser::class) {
        // 动态条件
        username?.let { where(table.userName eq it) }
        status?.let { where(table.status eq it) }
        deptId?.let { where(table.dept().id eq it) }

        // 排序
        orderBy(table.createTime.desc())

        select(table)
    }.execute()
}
```

### 4. 分页查询

```kotlin
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery

fun selectPageUserList(bo: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
    val pager = sqlClient.createQuery(SysUser::class) {
        // 动态条件
        bo.userName?.takeIf { it.isNotBlank() }?.let {
            where(table.userName like "%${it}%")
        }
        bo.status?.takeIf { it.isNotBlank() }?.let {
            where(table.status eq it)
        }

        // 排序
        orderBy(table.createTime.desc())

        select(table)
    }.fetchPage(
        pageQuery.getPageNumOrDefault() - 1,  // Jimmer fetchPage expects 0-based index
        pageQuery.getPageSizeOrDefault()
    )

    return TableDataInfo.build(
        pager.rows.map { entityToVo(it) },
        pager.totalRowCount
    )
}
```

---

## 保存数据

### Jimmer Draft API

Jimmer 使用 Draft 模式进行数据修改。每个实体都会生成一个对应的 Draft 接口，包含可变的属性（`var`）。

#### 核心 API

```kotlin
// Draft 接口由 KSP 自动生成
// 位置：build/generated/ksp/main/kotlin/.../entity/SysUserDraft.kt

public interface SysUserDraft : SysUser, CommDelFlagDraft, CommIdDraft, CommInfoDraft, CommTenantDraft {
    override var userName: String
    override var nickName: String?
    override var email: String?
    // ... 其他可变属性

    public object `$` {
        // 创建新实体或修改现有实体
        public fun produce(
            base: SysUser? = null,
            block: SysUserDraft.() -> Unit
        ): SysUser
    }
}
```

### 插入新对象

```kotlin
override fun insertUser(user: SysUserBo): Int {
    val newUser = SysUserDraft.`$`.produce {
        userName = user.userName ?: throw ServiceException("用户名不能为空")
        nickName = user.nickName
        email = user.email
        status = user.status ?: SystemConstants.NORMAL
        delFlag = false
        createTime = LocalDateTime.now()
    }

    val result = sqlClient.save(newUser)
    return if (result.isModified) 1 else 0
}
```

### 更新现有对象

```kotlin
override fun updateUser(user: SysUserBo): Int {
    val userIdVal = user.userId ?: return 0
    val existing = sqlClient.findById(SysUser::class, userIdVal)
        ?: throw ServiceException("用户不存在")

    val updated = SysUserDraft.`$`.produce(existing) {
        user.deptId?.let { deptId = it }
        user.nickName?.let { nickName = it }
        user.email?.let { email = it }
        user.status?.let { status = it }
        updateTime = LocalDateTime.now()
    }

    val result = sqlClient.save(updated)
    return if (result.isModified) 1 else 0
}
```

### 更新单个字段

```kotlin
override fun updateUserStatus(userId: Long, status: String): Int {
    val existing = sqlClient.findById(SysUser::class, userId)
        ?: throw ServiceException("用户不存在")

    val updated = SysUserDraft.`$`.produce(existing) {
        this.status = status
    }

    val result = sqlClient.save(updated)
    return if (result.isModified) 1 else 0
}
```

### 关联保存

对于多对多关联，Jimmer 支持简单的对象引用方式：

```kotlin
// ❌ 错误方式：直接操作 Draft 的列表（不推荐）
override fun insertUserAuth(userId: Long, roleIds: Array<Long>) {
    val existing = sqlClient.findById(SysUser::class, userId)
        ?: throw ServiceException("用户不存在")

    val updated = SysUserDraft.`$`.produce(existing) {
        this.roles().clear()
        roleIds.forEach { roleId ->
            val roleEntity = sqlClient.findById(SysRole::class, roleId)
            if (roleEntity != null) {
                // ❌ 错误：这样无法正确保存
                this.roles().add(SysRoleDraft.`$`.produce(roleEntity) {})
            }
        }
    }
    sqlClient.save(updated)
}

// ✅ 正确方式：使用 save command 配置关联
@Transactional
override fun insertUserAuth(userId: Long, roleIds: Array<Long>) {
    val existing = sqlClient.findById(SysUser::class, userId)
        ?: throw ServiceException("用户不存在")

    // 1. 先清除旧关联
    sqlClient.createUpdate(SysUserRole::class) {
        where(table.userId eq userId)
        set(table.id).isNull()
    }.execute()

    // 2. 插入新关联
    roleIds.forEach { roleId ->
        sqlClient.insert(SysUserRoleDraft.`$`.produce {
            this.userId = userId
            this.roleId = roleId
        })
    }
}
```

**说明**：
- Jimmer 的关联保存更倾向于使用明确的数据库操作
- 复杂的关联保存建议使用 `@Transactional` 配合简单的 insert/update

### 重要注意事项

1. **Draft 对象只在 lambda 内部有效**：Draft 对象是临时的，只在 `produce` 方法的 lambda 内部可访问
2. **参数名冲突**：如果 lambda 参数名与实体属性名冲突，使用 `this` 显式引用
3. **返回值类型**：`produce` 方法返回的是不可变实体（`SysUser`），不是 Draft
4. **SaveResult**：`sqlClient.save()` 返回 `SimpleSaveResult<T>`，包含 `isModified` 属性

---

## FoxDen 项目中的 Jimmer 使用

### 1. 项目特点

- **无 Repository 接口**：直接使用 `KSqlClient` 或扩展函数
- **Trait 复用**：所有实体继承 `CommDelFlag`, `CommId`, `CommInfo`, `CommTenant`
- **扩展函数**：在 `ServiceExtensions.kt` 中定义常用的 CRUD 操作
- **BO/VO 模式**：使用 BO（业务对象）接收参数，VO（视图对象）返回数据

### 2. 实体定义位置

```
foxden-domain/foxden-domain-system/src/main/kotlin/.../entity/
├── SysUser.kt
├── SysRole.kt
├── SysMenu.kt
├── SysDept.kt
└── ...
```

### 3. 常用扩展函数

```kotlin
// 在 ServiceExtensions.kt 中定义
fun <E : Any> KSqlClient.findById(
    entityType: KClass<E>,
    id: Long
): E? = ...

fun <E : Any> KSqlClient.queryList(
    entityType: KClass<E>,
    where: (KMutableTableImplementor<E>) -> Unit
): List<E> = ...

fun <E : Any> KSqlClient.queryPage(
    entityType: KClass<E>,
    pageNum: Int,
    pageSize: Int,
    where: (KMutableTableImplementor<E>) -> Unit
): Page<E> = ...
```

### 4. 分页封装

```kotlin
// PageQuery.kt
class PageQuery {
    var pageNum: Int = 1
    var pageSize: Int = 10
}

// TableDataInfo.kt
class TableDataInfo<T> {
    var total: Long = 0
    var rows: List<T> = mutableListOf()
    var code: Int = 200
    var msg: String = "查询成功"
}
```

### 5. 数据权限

```kotlin
// 忽略数据权限
DataPermissionHelper.ignore {
    userService.updateByBo(userData)
}

// 使用 @DataPermission 注解自动过滤
@DataPermission(
    value = [
        DataColumn(key = ["deptName"], value = ["dept_id"], permission = "system:user:query")
    ]
)
fun selectUserList(bo: SysUserBo): List<SysUserVo> {
    // Jimmer 会自动注入数据权限过滤条件
}
```

### 6. 多租户

```kotlin
// 动态切换租户
TenantHelper.dynamic(tenantId) {
    val user = userService.selectUserByUserName(username)
    // 在此作用域内，所有查询都会过滤指定租户
}

// 获取当前租户
val tenantId = TenantHelper.getTenantId()
```

---

## 常见模式

### 模式 1：按条件查询列表

```kotlin
fun selectUserList(bo: SysUserBo): List<SysUserVo> {
    return sqlClient.createQuery(SysUser::class) {
        // 模糊匹配
        bo.userName?.takeIf { it.isNotBlank() }?.let {
            where(table.userName like "%${it}%")
        }
        // 精确匹配
        bo.status?.takeIf { it.isNotBlank() }?.let {
            where(table.status eq it)
        }

        orderBy(table.createTime.desc())
        select(table)
    }.execute()
}
```

### 模式 2：检查唯一性

```kotlin
fun checkUserNameUnique(user: SysUserBo): Boolean {
    val count = sqlClient.createQuery(SysUser::class) {
        where(table.userName eq user.userName)
        user.userId?.let { where(table.id ne it) }
        select(table.id.count())
    }.fetchOneOrNull() ?: 0L

    return count == 0L
}
```

### 模式 3：批量查询并转换

```kotlin
fun selectUserByIds(ids: List<Long>): List<SysUserVo> {
    return sqlClient.findByIds(SysUser::class, ids)
        .map { entity ->
            SysUserVo().apply {
                userId = entity.id
                userName = entity.userName
                nickName = entity.nickName
                // ... 其他字段
            }
        }
}
```

### 模式 4：树形结构查询

```kotlin
fun selectDeptTree(dept: SysDeptBo): List<com.github.alphafoxz.foxden.common.core.domain.Tree<Long>> {
    val depts = sqlClient.createQuery(SysDept::class) {
        where(table.delFlag eq "0")
        dept.deptId?.let { where(table.id eq it) }
        dept.deptName?.takeIf { it.isNotBlank() }?.let {
            where(table.deptName like "%${it}%")
        }
        orderBy(table.orderNum.asc())
        select(table)
    }.execute()

    return TreeBuildUtils.buildMultiRoot(
        depts,
        { it.deptId!! },
        { it.parentId ?: 0L },
        { node, treeNode ->
            treeNode.setId(node.deptId!!)
                .setParentId(node.parentId)
                .setName(node.deptName!!)
        }
    )
}
```

---

## 注意事项

### 1. 属性缺失 vs null

```kotlin
val user = sqlClient.findById(SysUser::class, 1L)

// ❌ 错误：访问缺失的属性会抛出异常
if (user.nickName == null) { ... }

// ✅ 正确：使用 `?.` 安全调用
user.nickName?.let {
    println("昵称: $it")
}
```

### 2. 不可变对象更新

```kotlin
// ❌ 错误：不能直接修改
user.userName = "new_name"  // 编译错误

// ✅ 正确：使用 Draft API
val updated = sqlClient.update(
    user,
    newInferred(SysUser::class).by {
        userName = "new_name"
    }
)
```

### 3. 多租户过滤

所有 `KSqlClient` 查询会自动添加租户过滤条件（`tenant_id = ?`）。

---

## 常见问题

### 1. ConnectionManagerDSL 错误

**错误信息**:
```
IllegalStateException: ConnectionManagerDSL has not been proceeded
```

**原因**: 手动创建了 KSqlClient Bean，但配置不正确

**解决方案**: 删除手动配置，确保 `jimmer.language: kotlin` 配置存在

### 2. KSP 生成的代码找不到

**原因**: 未配置 KSP 生成目录

**解决方案**: 在 `build.gradle.kts` 中添加：
```kotlin
kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}
```

### 3. Fetcher 定义错误

**问题**: Fetcher 未正确导入

**解决方案**: 确保 KSP 已运行
```bash
./gradlew :foxden-domain:foxden-domain-system:kspKotlin
```

---

## 参考资料

### 官方文档

- [Jimmer 官方文档](https://babyfish-ct.github.io/jimmer-doc/zh)
- [GitHub 仓库](https://github.com/babyfish-ct/jimmer)
- [示例项目](https://github.com/babyfish-ct/jimmer-examples)

### 核心文档链接

- [概述](https://babyfish-ct.github.io/jimmer-doc/zh/docs/overview/introduction/)
- [实体定义](https://babyfish-ct.github.io/jimmer-doc/zh/docs/mapping/base/index.html)
- [对象 Fetcher](https://babyfish-ct.github.io/jimmer-doc/zh/docs/query/object-fetcher/index.html)
- [保存命令](https://babyfish-ct.github.io/jimmer-doc/zh/docs/mutation/save-command/index.html)
- [Spring Boot 集成](https://babyfish-ct.github.io/jimmer-doc/zh/docs/spring/index.html)
