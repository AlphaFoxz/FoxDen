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
  - [配置说明](#配置说明)
  - [自动配置原理](#自动配置原理)
  - [扩展函数](#扩展函数)
- [实体定义](#实体定义)
  - [基本 Entity 定义](#基本-entity-定义)
  - [常用注解](#常用注解)
  - [关联行为（@OnDissociate）](#关联行为ondissociate)
  - [Trait 复用（FoxDen 项目）](#trait-复用foxden-项目)

### 查询操作
- [查询数据](#查询数据)
  - [1. 使用 SQL Client 查询](#1-使用-sql-client-查询)
  - [2. 使用 Fetcher 控制查询形状](#2-使用-fetcher-控制查询形状)
    - [创建 Fetcher 的正确方式](#创建-fetcher-的正确方式)
    - [在查询中使用 Fetcher](#在查询中使用-fetcher)
    - [递归 Fetcher（树形结构）](#递归-fetcher树形结构)
  - [3. 动态查询](#3-动态查询)
  - [4. 分页查询](#4-分页查询)

### 进阶专题
- [处理关联属性的懒加载问题](#处理关联属性的懒加载问题)
  - [问题现象](#问题现象)
  - [根本原因](#根本原因)
  - [解决方案对比](#解决方案对比)
    - [方案 1: 使用 Fetcher API（高级用法）](#方案-1-使用-fetcher-api高级用法)
    - [方案 2: 手动查询（简单用法 - 推荐）](#方案-2-手动查询简单用法---推荐)
  - [与 ruoyi/MyBatis 的对比](#与-ruoyimybatis-的对比)
  - [最佳实践建议](#最佳实践建议)
- [保存数据](#保存数据)
  - [Jimmer Draft API](#jimmer-draft-api)
  - [插入新对象](#插入新对象)
  - [更新现有对象](#更新现有对象)
  - [更新单个字段](#更新单个字段)
  - [重要注意事项](#重要注意事项)
  - [多对多关联（待研究）](#多对多关联待研究)

### 项目应用
- [FoxDen 项目中的 Jimmer 使用](#foxden-项目中的-jimmer-使用)
  - [1. 项目特点](#1-项目特点)
  - [2. 实体定义位置](#2-实体定义位置)
  - [3. 常用扩展函数](#3-常用扩展函数)
  - [4. 分页封装](#4-分页封装)
  - [5. 数据权限](#5-数据权限)
  - [6. 多租户](#6-多租户)
- [常见模式](#常见模式)
  - [模式 1：按条件查询列表](#模式-1按条件查询列表)
  - [模式 2：检查唯一性](#模式-2检查唯一性)
  - [模式 3：批量查询并转换](#模式-3批量查询并转换)
  - [模式 4：树形结构查询](#模式-4树形结构查询)
  - [模式 5：逻辑删除](#模式-5逻辑删除)

### 实践指南
- [注意事项](#注意事项)
  - [1. 属性缺失 vs null](#1-属性缺失-vs-null)
  - [2. 不可变对象更新](#2-不可变对象更新)
  - [3. 多租户过滤](#3-多租户过滤)
- [常见问题](#常见问题)
  - [1. ConnectionManagerDsl 错误](#1-connectionmanagerdsl-错误)
  - [2. 类型推断错误](#2-类型推断错误)
  - [3. KSP 生成的代码找不到](#3-ksp-生成的代码找不到)
  - [4. Fetcher 定义错误](#4-fetcher-定义错误)
  - [5. 多对多关联保存](#5-多对多关联保存)
- [最佳实践](#最佳实践)

### 参考资料
- [参考资料](#参考资料)
- [附录：Kotlin DSL 快速参考](#附录kotlin-dsl-快速参考)
  - [✅ 已验证的正确模式](#-已验证的正确模式)
  - [⚠️ 常见错误](#-常见错误)
  - [🔧 KSP 配置要求](#-ksp-配置要求)
  - [🎯 学习建议](#-学习建议)

---

## 什么是 Jimmer

Jimmer 是 JVM 中最先进的 ORM，同时面向 Java 和 Kotlin。它的核心特点：

1. **为任意形状的数据结构设计** - 不局限于简单实体对象，可直接操作复杂的嵌套数据结构
2. **解决 N+1 问题** - 即使实体具有复杂计算属性，查询仍无 N+1 问题
3. **强类型 DSL** - 支持复杂的多表动态查询，智能优化 SQL
4. **DTO 语言** - 以极低成本自动生成 DTO，解决业务模型与数据模型不一致问题
5. **不可变对象** - 实体为不可变对象，结合 immer 算法实现高效的结构更新

### Jimmer vs 传统 ORM

| 特性 | JPA/Hibernate | MyBatis | Jimmer |
|------|--------------|---------|--------|
| 查询任意形状数据 | ❌ | ⚠️ 需手动映射 | ✅ 原生支持 |
| N+1 问题 | ❌ 常见 | ✅ 无此问题 | ✅ 智能解决 |
| 动态查询 | ⚠️ Criteria API 复杂 | ⚠️ XML 动态 SQL | ✅ 强类型 DSL |
| DTO 生成 | ❌ 需手动或 MapStruct | ❌ 需手动 | ✅ 自动生成 |
| 类型安全 | ✅ | ❌ | ✅ |
| 批量操作 | ⚠️ 需优化 | ✅ 手动控制 | ✅ 自动优化 |

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

### 扩展函数

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

### 关联映射限制（重要）

**Jimmer 不支持通过非主键列进行关联**

与 JPA/Hibernate 不同，Jimmer 的 `@ManyToOne` 和 `@OneToMany` 关联**必须**通过外键ID引用目标实体的主键。

#### ❌ 不支持的模式

```kotlin
// 错误：Jimmer 不支持这种关联方式
@Entity
interface SysDictData {
    val dictType: String  // 字符串字段，存储字典类型代码

    // ❌ 错误：试图通过 dictType 字段关联 SysDictType
    // Jimmer 会尝试查询 dict_type_obj_id 列（不存在）
    @ManyToOne
    @JoinColumn(name = "dict_type")  // ❌ 不会像 JPA 那样引用 dictType 列
    val dictTypeObj: SysDictType?
}

@Entity
interface SysDictType {
    @Id
    val id: Long
    val dictType: String  // 字符串字段

    // ❌ 错误：反向引用也会失败
    @OneToMany(mappedBy = "dictTypeObj")
    val dictData: List<SysDictData>
}
```

#### ✅ 正确的做法

**方案 1：删除关联，使用字符串字段（推荐）**

```kotlin
@Entity
interface SysDictData {
    val dictType: String  // 保留字符串字段
    // 删除 dictTypeObj 关联
}

@Entity
interface SysDictType {
    @Id
    val id: Long
    val dictType: String
    // 删除 dictData 关联
}
```

在需要关联查询时，手动通过 `dictType` 字段查询：

```kotlin
@Service
class SysDictDataServiceImpl(
    private val sqlClient: KSqlClient
) {
    fun selectDictDataWithType(dictType: String): List<SysDictData> {
        return sqlClient.createQuery(SysDictData::class) {
            where(table.dictType eq dictType)
            select(table)
        }.execute()
    }

    // 如果需要同时查询字典类型信息
    fun selectDictDataWithTypeInfo(dictType: String): Pair<List<SysDictData>, SysDictType?> {
        val dataList = sqlClient.createQuery(SysDictData::class) {
            where(table.dictType eq dictType)
            select(table)
        }.execute()

        val typeInfo = sqlClient.createQuery(SysDictType::class) {
            where(table.dictType eq dictType)
            select(table)
        }.fetchOneOrNull()

        return Pair(dataList, typeInfo)
    }
}
```

**方案 2：修改数据库表结构，添加外键（仅当必要时）**

如果确实需要 ORM 关联，可以修改数据库表结构，添加外键列：

```sql
-- 修改 sys_dict_data 表，添加外键列
ALTER TABLE sys_dict_data
ADD COLUMN dict_type_id BIGINT REFERENCES sys_dict_type(dict_id);
```

```kotlin
@Entity
interface SysDictData {
    val dictType: String      // 保留原有字段
    val dictTypeId: Long?     // 新增外键列

    @ManyToOne
    @JoinColumn(name = "dict_type_id")
    val dictTypeObj: SysDictType?  // ✅ 现在可以正确关联
}
```

#### 与 JPA/Hibernate 的对比

| 特性 | JPA/Hibernate | Jimmer |
|------|--------------|--------|
| `@ManyToOne` 引用非主键列 | ✅ 支持 `referencedColumnName` | ❌ 不支持 |
| `@OneToMany(mappedBy = "非ID字段")` | ✅ 支持 | ❌ 不支持 |
| 强制外键ID关联 | ❌ 不强制 | ✅ 强制 |

#### 迁移建议

从 MyBatis/JPA 迁移到 Jimmer 时：

1. **审查所有关联定义**：确保所有 `@ManyToOne` 关联都有对应的外键列
2. **删除无效关联**：如果数据库表没有外键列，删除实体中的关联定义
3. **使用手动关联查询**：通过 Service 层代码手动组合关联数据
4. **参考老项目实现**：保持与老项目（如 ruoyi）的数据结构一致

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
import org.babyfish.jimmer.sql.kt.ast.expression.like

// 注入 SqlClient
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

**重要**：Jimmer 的 Fetcher 通过 `newFetcher` 函数创建，而非 `SysMenuFetcher.$` 单例对象。

```kotlin
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher

// ✅ 正确方式 1：使用 newFetcher 函数
val MENU_TREE_FETCHER = newFetcher(SysMenu::class).by {
    allScalarFields()
    children {
        allScalarFields()
        children {
            allScalarFields()
            // 支持递归定义
        }
    }
}

// ✅ 正确方式 2：简化语法（单级关联）
val USER_WITH_ROLE_FETCHER = newFetcher(SysUser::class).by {
    allScalarFields()
    roles {
        allScalarFields()
    }
}

// ❌ 错误方式：使用 SysMenuFetcher.$（不推荐）
val WRONG_FETCHER = SysMenuFetcher.$  // 不推荐，用于内部实现
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

// 方式 3：直接在查询中定义 Fetcher
fun getMenus(): List<SysMenu> {
    return sqlClient.createQuery(SysMenu::class) {
        select(table.fetch {
            allScalarFields()
            children {
                allScalarFields()
            }
        })
    }.execute()
}
```

**Fetcher 优势：**
- ✅ 避免 N+1 问题（自动 JOIN 或批量查询）
- ✅ 按需加载字段
- ✅ 类型安全
- ✅ 支持递归查询（树形结构）

#### 递归 Fetcher（树形结构）

对于菜单树、部门树等递归结构，使用 Jimmer 的 `*` 语法实现无限递归：

```kotlin
// 递归 Fetcher 定义 - 使用 children*() 实现无限递归
val MENU_TREE_FETCHER = newFetcher(SysMenu::class).by {
    allScalarFields()
    // 使用 parent*() 递归加载所有父级（向上递归）
    `parent*`()
    // 使用 children*() 递归加载所有子级（向下递归）
    `children*`()
}

// 使用递归 Fetcher 查询菜单树
fun getMenuTree(): List<SysMenu> {
    return sqlClient.createQuery(SysMenu::class) {
        where(table.parentId.isNull())
        orderBy(table.orderNum.asc())
        select(table.fetch(MENU_TREE_FETCHER))
    }.execute()
}
```

**关键点**：
- `children*()` - 星号表示无限递归，自动加载所有层级的子菜单
- `parent*()` - 递归加载所有层级的父菜单
- 手动嵌套 `children { children { ... } }` 只能加载固定层级，不推荐

**注意事项**：
- 递归深度受数据库性能限制，建议配合 `WHERE` 条件限制范围
- 对于超深树形结构，考虑使用分页或限制深度

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
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo

fun selectPageUserList(bo: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
    val pager = sqlClient.createQuery(SysUser::class) {
        // 动态条件
        bo.userName?.let { where(table.userName like "%$it%") }
        bo.status?.let { where(table.status eq it) }
        bo.phonenumber?.let { where(table.phonenumber eq it) }

        // 排序
        orderBy(table.createTime.desc())

        select(table)
    }.fetchPage(
        pageQuery.pageNum,
        pageQuery.pageSize
    )

    return TableDataInfo(
        pager.rows,
        pager.totalRowCount
    )
}
```

---

## 处理关联属性的懒加载问题

### 问题现象

在使用 Jimmer ORM 时，直接访问实体的关联属性（如 `@ManyToMany`, `@ManyToOne`）会抛出 `UnloadedException`：

```kotlin
// ❌ 错误示例：直接访问关联属性
override fun selectUserById(userId: Long): SysUserVo? {
    val user = sqlClient.findById(SysUser::class, userId) ?: return null

    // 尝试访问 roles 列表
    user.roles.forEach { role ->  // ❌ UnloadedException
        println(role.roleName)
    }

    return entityToVo(user)
}
```

**错误信息**:
```
org.babyfish.jimmer.UnloadedException: The property "SysUser.roles" is unloaded
    at org.babyfish.jimmer.sql.ast.impl.TupleImplementor.throwUnloadedException
    at com.github.alphafoxz.foxden.domain.system.entity.SysUserImpl.getRoles(SysUserImpl.kt:88)
```

### 根本原因

Jimmer 的关联属性默认是**懒加载**（Lazy）的：

| 关联类型 | 默认加载方式 | 说明 |
|---------|-------------|------|
| `@ManyToOne` | Lazy | 需要显式 Fetcher 或手动查询 |
| `@ManyToMany` | Lazy | 需要显式 Fetcher 或手动查询 |
| `@OneToMany` | Lazy | 需要显式 Fetcher 或手动查询 |

这与传统的 JPA/Hibernate 不同，Jimmer **不会**自动触发关联查询，必须显式指定要加载的关联。

### 解决方案对比

#### 方案 1: 使用 Fetcher API（高级用法）

**优点**：
- ✅ 避免 N+1 查询问题
- ✅ 类型安全
- ✅ 支持复杂的嵌套查询
- ✅ Jimmer 推荐的最佳实践

**缺点**：
- ❌ API 相对复杂
- ❌ 需要提前定义 Fetcher
- ❌ 学习曲线陡峭

**示例代码**：

```kotlin
// 定义 Fetcher（通常在实体类同目录下定义）
val USER_WITH_ROLES_FETCHER = SysUserFetcher.$
    .allScalarFields()  // 所有标量字段
    .roles {            // 加载角色关联
        allScalarFields()
    }

// 使用 Fetcher 查询
override fun selectUserById(userId: Long): SysUserVo? {
    val user = sqlClient.findById(
        SysUser::class,
        userId,
        USER_WITH_ROLES_FETCHER  // ✅ 使用 Fetcher
    ) ?: return null

    // 现在可以安全访问 roles
    user.roles.forEach { role ->
        println(role.roleName)
    }

    return entityToVo(user)
}
```

**在查询中使用 Fetcher**：

```kotlin
// 使用 fetch() 方法
val users = sqlClient.createQuery(SysUser::class) {
    where(table.status eq "0")
    select(table.fetch(USER_WITH_ROLES_FETCHER))
}.execute()

// 使用 fetcher 参数
val user = sqlClient.findById(
    SysUser::class,
    userId,
    SysUserFetcher.$.allScalarFields().roles { allScalarFields() }
)
```

#### 方案 2: 手动查询（简单用法 - 推荐）

**优点**：
- ✅ 简单直观，易于理解
- ✅ 与 ruoyi/MyBatis 的模式相似
- ✅ 不需要学习复杂的 Fetcher API
- ✅ 适合基本的关联查询场景

**缺点**：
- ⚠️ 可能产生 N+1 查询（但通常影响不大）
- ⚠️ 需要手动组合数据

**示例代码**：

```kotlin
@Service
class SysUserServiceImpl(
    private val sqlClient: KSqlClient,
    private val roleService: SysRoleService  // 注入角色服务
) : SysUserService {

    override fun selectUserById(userId: Long): SysUserVo? {
        // 步骤 1: 查询用户实体（不加载关联）
        val user = sqlClient.findById(SysUser::class, userId) ?: return null

        // 步骤 2: 转换为 VO（不包含关联数据）
        val vo = entityToVo(user, withRoles = false)

        // 步骤 3: 手动查询角色数据并设置到 VO
        if (vo.userId != null) {
            vo.roles = roleService.selectRolesByUserId(vo.userId!!)
        }

        return vo
    }

    // 实体转 VO 方法
    private fun entityToVo(user: SysUser, withRoles: Boolean = false): SysUserVo {
        return SysUserVo().apply {
            userId = user.id
            deptId = user.deptId
            userName = user.userName
            nickName = user.nickName
            email = user.email
            phonenumber = user.phonenumber
            status = user.status

            // 根据参数决定是否加载角色
            if (withRoles) {
                roles = user.roles.map { role ->
                    SysRoleVo().apply {
                        roleId = role.id
                        roleName = role.roleName
                        roleKey = role.roleKey
                    }
                }
            }
        }
    }
}
```

**在 SysMenuServiceImpl 中的应用**：

```kotlin
@Service
class SysMenuServiceImpl(
    private val sqlClient: KSqlClient,
    private val roleService: SysRoleService  // 注入角色服务
) : SysMenuService {

    override fun selectMenuTreeByUserId(userId: Long): List<SysMenu> {
        val user = sqlClient.findById(SysUser::class, userId)
            ?: return emptyList()

        // ✅ 手动查询角色数据（避免懒加载问题）
        val roles = roleService.selectRolesByUserId(userId)

        val menus = if (isAdmin(roles)) {
            // 管理员 - 返回所有菜单
            sqlClient.createQuery(SysMenu::class) {
                orderBy(table.orderNum.asc())
                select(table)
            }.execute()
        } else {
            // TODO: 根据用户角色过滤菜单
            sqlClient.createQuery(SysMenu::class) {
                orderBy(table.orderNum.asc())
                select(table)
            }.execute()
        }

        return menus
    }

    /**
     * 判断是否是管理员（基于角色列表）
     */
    private fun isAdmin(roles: List<SysRoleVo>): Boolean {
        return roles.any {
            it.roleKey == "admin" || it.roleKey == "role_admin"
        }
    }
}
```

### 与 ruoyi/MyBatis 的对比

**ruoyi-vue-pro (MyBatis-Plus)**：

```java
// MyBatis 会自动加载关联（如果配置了）
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> {

    public SysUserVO selectUserById(Long userId) {
        // MyBatis-Plus 会自动注入关联查询
        SysUser user = this.getById(userId);

        // 直接获取角色（已自动加载）
        List<SysRole> roles = user.getRoles();

        return convert(user, roles);
    }
}
```

**FoxDen (Jimmer - 手动查询方案)**：

```kotlin
// Jimmer 需要手动查询关联
@Service
class SysUserServiceImpl(
    private val sqlClient: KSqlClient,
    private val roleService: SysRoleService
) : SysUserService {

    fun selectUserById(userId: Long): SysUserVo? {
        val user = sqlClient.findById(SysUser::class, userId) ?: return null

        // 手动查询角色（类似 MyBatis 的方式）
        val roles = roleService.selectRolesByUserId(userId)

        return entityToVo(user, roles)
    }
}
```

**对比结论**：
- ruoyi 使用 MyBatis-Plus，关联查询通过 XML 或注解配置
- FoxDen 使用 Jimmer，手动查询方案与 ruoyi 的**结果等效**，实现方式类似
- 手动查询方案更直观，适合从 MyBatis 迁移过来的开发者

### 最佳实践建议

#### 何时使用 Fetcher API

✅ **推荐使用 Fetcher 的场景**：
- 复杂的嵌套查询（如：用户 -> 角色 -> 菜单 -> 权限）
- 需要避免 N+1 查询问题（如：批量查询用户及其角色）
- 需要递归查询树形结构（如：菜单树、部门树）
- 多个模块都需要相同的关联数据结构

**示例：批量查询用户及其角色（避免 N+1）**

```kotlin
// 使用 Fetcher 一次性加载所有用户及其角色
val users = sqlClient.createQuery(SysUser::class) {
    where(table.deptId eq deptId)
    select(table.fetch(
        SysUserFetcher.$
            .allScalarFields()
            .roles {  // ✅ 一次性加载所有角色
                allScalarFields()
            }
    ))
}.execute()

// 不会产生 N+1 查询
// Jimmer 会生成类似这样的 SQL：
// SELECT u.*, r.* FROM sys_user u
// LEFT JOIN sys_user_role ur ON u.id = ur.user_id
// LEFT JOIN sys_role r ON ur.role_id = r.id
// WHERE u.dept_id = ?
```

#### 何时使用手动查询

✅ **推荐使用手动查询的场景**：
- 简单的单条记录查询（如：根据 ID 查询用户及其角色）
- 需要额外的业务逻辑处理（如：过滤、排序、转换）
- 从 MyBatis 迁移过来的代码
- 关联数据需要从多个来源组合

**示例：查询用户并过滤角色**

```kotlin
fun selectUserWithActiveRoles(userId: Long): SysUserVo? {
    val user = sqlClient.findById(SysUser::class, userId) ?: return null

    // 手动查询并过滤角色
    val activeRoles = roleService.selectRolesByUserId(userId)
        .filter { it.status == "0" }  // 只返回启用的角色

    return SysUserVo().apply {
        userId = user.id
        userName = user.userName
        roles = activeRoles  // 使用过滤后的角色
    }
}
```

### 完整示例：Controller 层的使用

```kotlin
@RestController
@RequestMapping("/system/user")
class SysUserController(
    private val userService: SysUserService,
    private val roleService: SysRoleService
) {

    /**
     * 根据用户 ID 获取详细信息（包含角色）
     */
    @GetMapping("/{userId}")
    fun getInfo(@PathVariable userId: Long): R<SysUserInfoVo> {
        val user = DataPermissionHelper.ignore(java.util.function.Supplier {
            userService.selectUserById(userId)
        }) ?: return R.fail("用户不存在")

        // 手动查询角色和岗位
        val roles = roleService.selectRolesByUserId(userId)
        val posts = postService.selectPostsByUserId(userId)

        return R.ok(SysUserInfoVo(
            user = user,
            roles = roles,
            posts = posts,
            roleIds = roles.map { it.roleId },
            postIds = posts.map { it.postId }
        ))
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/getInfo")
    fun getInfo(): R<UserInfoVo> {
        val loginUser = LoginHelper.getLoginUser() ?: return R.fail("获取用户信息失败")

        val user = DataPermissionHelper.ignore(java.util.function.Supplier {
            userService.selectUserById(loginUser.userId!!)
        })

        return R.ok(UserInfoVo(
            user = user,
            permissions = loginUser.menuPermission,
            roles = loginUser.rolePermission
        ))
    }
}
```

### 总结

| 方案 | 适用场景 | 优点 | 缺点 |
|------|---------|------|------|
| **Fetcher API** | 复杂嵌套查询、批量查询、树形结构 | 避免 N+1、类型安全、性能最优 | API 复杂、学习成本高 |
| **手动查询** | 简单查询、单条记录、需要业务处理 | 简单直观、易迁移、灵活 | 可能 N+1（通常影响不大） |

**推荐策略**：
1. **默认使用手动查询**：简单直接，符合现有代码风格
2. **批量查询使用 Fetcher**：避免 N+1 问题
3. **复杂嵌套使用 Fetcher**：一次性加载多级关联
4. **树形结构使用 Fetcher**：支持递归查询

**关键原则**：
- 不要直接访问实体的关联属性（会抛出 `UnloadedException`）
- 始终使用 Fetcher 或手动查询来加载关联数据
- 根据实际需求选择合适的方案

---

## 保存数据

### Jimmer Draft API

Jimmer 使用 Draft 模式进行数据修改。每个实体都会生成一个对应的 Draft 接口，包含可变的属性（`var`）。

#### 核心 API

```kotlin
// Draft 接口由 KSP 自动生成
// 位置：build/generated/ksp/main/kotlin/.../entity/SysUserDraft.kt

public interface SysUserDraft : SysUser, CommDelFlagDraft, CommIdDraft, CommInfoDraft, CommTenantDraft {
    override var userName: String  // 可变属性
    override var nickName: String?
    override var email: String?
    // ... 其他可变属性

    public object `$` {
        // 创建新实体或修改现有实体
        public fun produce(
            base: SysUser? = null,  // null = 创建新实体，非 null = 修改现有实体
            block: SysUserDraft.() -> Unit
        ): SysUser
    }
}
```

### 插入新对象

```kotlin
override fun insertUser(user: SysUserBo): Int {
    val newUser = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce {
        // 使用 this 隐式引用 Draft 对象
        userName = user.userName ?: throw ServiceException("用户名不能为空")
        nickName = user.nickName
        email = user.email
        phonenumber = user.phonenumber
        password = user.password
        sex = user.sex
        status = user.status ?: SystemConstants.NORMAL
        deptId = user.deptId
        remark = user.remark
        userType = user.userType
        avatar = null
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

    val updated = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce(existing) {
        // existing 作为 base 参数，会基于现有对象创建 Draft
        user.deptId?.let { deptId = it }
        user.nickName?.let { nickName = it }
        user.email?.let { email = it }
        user.phonenumber?.let { phonenumber = it }
        user.sex?.let { sex = it }
        user.status?.let { status = it }
        user.remark?.let { remark = it }
        user.userType?.let { userType = it }
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

    val updated = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce(existing) {
        this.status = status  // 使用 this 显式引用 Draft 对象
    }

    val result = sqlClient.save(updated)
    return if (result.isModified) 1 else 0
}

override fun resetUserPwd(userId: Long, password: String): Int {
    val existing = sqlClient.findById(SysUser::class, userId)
        ?: throw ServiceException("用户不存在")

    val updated = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce(existing) {
        this.password = password
    }

    val result = sqlClient.save(updated)
    return if (result.isModified) 1 else 0
}
```

### 重要注意事项

1. **Draft 对象只在 lambda 内部有效**：Draft 对象是临时的，只在 `produce` 方法的 lambda 内部可访问。`produce` 方法返回的是不可变的实体对象。

2. **参数名冲突**：如果 lambda 参数名与实体属性名冲突（如 `user` 参数），使用局部变量或 `this` 显式引用：
   ```kotlin
   // ❌ 错误：参数 user 会干扰属性访问
   fun updateUser(user: SysUserBo) {
       SysUserDraft.`$`.produce(existing) {
           userName = user.userName  // 可能导致歧义
       }
   }

   // ✅ 正确：使用局部变量或 this
   fun updateUser(user: SysUserBo) {
       val userNameVal = user.userName
       SysUserDraft.`$`.produce(existing) {
           userName = userNameVal
       }
   }
   ```

3. **$ 对象访问**：Draft 的 `$` 对象包含静态工厂方法，需要使用完整路径或导入：
   ```kotlin
   // 方式 1：使用完整路径
   com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce { ... }

   // 方式 2：导入 $ 对象（需要反引号）
   import com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`
   $.produce { ... }
   ```

4. **返回值类型**：`produce` 方法返回的是不可变实体（`SysUser`），不是 Draft（`SysUserDraft`）。

5. **SaveResult**：`sqlClient.save()` 返回 `SimpleSaveResult<T>`，包含 `isModified` 属性表示是否修改成功。

### 多对多关联（待研究）

```kotlin
// TODO: 需要研究 Jimmer 多对多关联的正确处理方式
// 以下代码存在问题：Draft 的 roles() 列表期望 SysRoleDraft，
// 但 produce() 返回的是不可变的 SysRole

override fun insertUserAuth(userId: Long, roleIds: Array<Long>) {
    val existing = sqlClient.findById(SysUser::class, userId)
        ?: throw ServiceException("用户不存在")

    val updated = com.github.alphafoxz.foxden.domain.system.entity.SysUserDraft.`$`.produce(existing) {
        this.roles().clear()
        roleIds.forEach { roleId ->
            val roleEntity = sqlClient.findById(SysRole::class, roleId)
            if (roleEntity != null) {
                // ❌ 错误：produce() 返回 SysRole，不能添加到 roles() 列表
                this.roles().add(SysRoleDraft.`$`.produce(roleEntity) {})
            }
        }
    }

    sqlClient.save(updated)
}
```

**需要研究的内容**：
- Jimmer 多对多关联的正确保存方式
- 如何在 Draft 中添加关联对象
- 是否需要使用专门的关联 API（如 `setAssociatedMode`）

---

## 研究资源

### 官方文档
- [Jimmer 官方文档](https://babyfish-ct.github.io/jimmer-doc/)
- [Jimmer GitHub 仓库](https://github.com/babyfish-ct/jimmer)
- [Jimmer 示例项目](https://github.com/babyfish-ct/jimmer-examples)

### 研究发现
1. **Draft API**：Jimmer 使用 Draft 模式，每个实体都有对应的 Draft 接口（由 KSP 生成）
2. **$ 对象**：Draft 接口包含 `$` 对象，提供 `produce()` 方法用于创建/修改实体
3. **save() 方法**：`KSqlClient.save()` 方法保存实体并返回 `SimpleSaveResult`
4. **限制**：多对多关联的处理需要进一步研究，直接使用 `produce()` 创建的关联对象无法添加到 Draft 的列表中

### 研究过程
- 检查了 KSP 生成的 Draft 源码（`build/generated/ksp/main/kotlin/.../entity/*Draft.kt`）
- 搜索了 Jimmer 官方文档和 GitHub 示例
- 测试了多种 Draft API 使用方式
- 记录了参数名冲突和类型不匹配等常见问题

---
                }
            }
        }
    )
}
```

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
fun SysUserService.updateByBo(data: Map<String, Any?>): Int {
    val bo = SysUserBo().apply {
        data["userId"]?.let { userId = it as Long }
        data["loginIp"]?.let { loginIp = it as String }
        data["loginDate"]?.let { loginDate = it as java.util.Date }
    }
    return this.updateUserProfile(bo)
}

// 使用
userService.updateByBo(mapOf(
    "userId" to 1L,
    "loginIp" to "192.168.1.1"
))
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

// 使用
@Service
class SysUserServiceImpl : SysUserService {
    override fun selectPageUserList(
        bo: SysUserBo,
        pageQuery: PageQuery
    ): TableDataInfo<SysUserVo> {
        // 实现分页查询
        // ...
    }
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
        bo.userName?.let {
            where(table.userName like "%${it}%")
        }
        // 精确匹配
        bo.status?.let {
            where(table.status eq it)
        }
        // 关联对象条件
        bo.deptId?.let {
            where(table.dept().id eq it)
        }

        orderBy(table.createTime.desc())
        select(table)
    }.execute()
}
```

### 模式 2：检查唯一性

```kotlin
fun checkUserNameUnique(bo: SysUserBo): Boolean {
    val count = sqlClient.createQuery(SysUser::class) {
        where(table.userName eq bo.userName)
        // 排除自己
        if (bo.userId != null) {
            where(table.id ne bo.userId)
        }
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
            // 转换为 VO
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
// 递归 Fetcher 查询树形菜单
val MENU_TREE_FETCHER = SysMenuFetcher.$
    .allScalarFields()
    .children {  // 递归关联
        allScalarFields()
        children {
            allScalarFields()
            // 支持任意深度
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

### 模式 5：逻辑删除

```kotlin
// Jimmer 自动过滤逻辑删除的数据
// 查询时自动添加 WHERE del_flag = false

// 手动软删除
fun softDeleteUser(userId: Long): Int {
    return sqlClient.deleteById(SysUser::class, userId)
    // 自动执行 UPDATE sys_user SET del_flag = true WHERE id = ?
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

// ✅ 正确：使用 Fetcher 确保属性被加载
val userWithNick = sqlClient.findById(
    SysUser::class,
    1L,
    SysUserFetcher.$.nickName()
)
```

### 2. 不可变对象更新

```kotlin
// ❌ 错误：不能直接修改
user.userName = "new_name"  // 编译错误

// ✅ 正确：使用 Save Command
sqlClient.update(
    user,
    newInferred(SysUser::class).by {
        userName = "new_name"
    }
)
```

### 3. 多租户过滤

```kotlin
// 所有查询会自动过滤当前租户
// SELECT * FROM sys_user WHERE tenant_id = ? AND del_flag = false

// 如需查询所有租户，使用动态租户
TenantHelper.dynamic(tenantId) {
    // 在此作用域内查询指定租户
}
```

---

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

### 5. 多对多关联保存

**问题**: Draft 的关联列表类型不匹配

**解决方案**: 使用简单的对象引用而非完整对象

```kotlin
val updated = SysUserDraft.`$`.produce(existing) {
    // 对于多对多关联，使用已存在的关联对象
    // 不要在 Draft 中创建新对象
}
```

---

## 最佳实践

### 1. 始终使用 Fetcher

```kotlin
// ✅ 推荐：使用 Fetcher
val user = sqlClient.findById(SysUser::class, id, SysUserFetcher.$.allScalarFields())

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

---

## 参考资料

- [Jimmer 官方文档](https://babyfish-ct.github.io/jimmer-doc/zh)
- [GitHub 仓库](https://github.com/babyfish-ct/jimmer)
- [项目介绍](https://babyfish-ct.github.io/jimmer-doc/zh/docs/overview/introduction/)
- [DTO 语言](https://babyfish-ct.github.io/jimmer-doc/zh/docs/dto/overview/)
- [Fetcher 教程](https://babyfish-ct.github.io/jimmer-doc/zh/docs/query/object-fetcher/overview/)
- [Save Command 教程](https://babyfish-ct.github.io/jimmer-doc/zh/docs/save/save-command/overview/)
- [SQL DSL 教程](https://babyfish-ct.github.io/jimmer-doc/zh/docs/query/dsl/overview/)

---

## 附录：Kotlin DSL 快速参考

> 本节提供 Jimmer Kotlin DSL 的常用代码模式和示例

### ✅ 已验证的正确模式

#### 1. 基础查询

```kotlin
// ✅ 正确：必须使用 Entity::class
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(table)
}.execute()
```

#### 2. 条件查询

```kotlin
// ✅ 精确匹配
where(table.userName eq "admin")

// ✅ 模糊查询
where(table.userName like "admin%")

// ✅ IN 查询
where(table.id `in` listOf(1L, 2L, 3L))

// ✅ 不等查询
where(table.id ne userId)

// ✅ 多条件（链式调用）
where(table.status eq "0").and(table.delFlag eq false)
```

#### 3. 动态条件

```kotlin
// ✅ 使用 ?.let 实现动态条件
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)

    // 动态添加条件
    user.userId?.let { where(table.id eq it) }
    user.userName?.takeIf { it.isNotBlank() }?.let {
        where(table.userName like it)
    }
    user.status?.takeIf { it.isNotBlank() }?.let {
        where(table.status eq it)
    }

    select(table)
}.execute()
```

#### 4. 分页查询

```kotlin
// ✅ 正确的分页查询
val pageable = PageRequest.of(pageNum - 1, pageSize)
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(table)
}.execute(pageable)

// 访问结果
users.content           // 数据列表
users.totalElements     // 总记录数
```

#### 5. 排序

```kotlin
// ✅ 升序
orderBy(table.id.asc())

// ✅ 降序
orderBy(table.id.desc())
```

#### 6. 单条查询

```kotlin
// ✅ 根据 ID 查询
val user = sqlClient.findById(SysUser::class, userId)

// ✅ 查询单条或 null
val user = sqlClient.createQuery(SysUser::class) {
    where(table.userName eq userName)
    select(table)
}.fetchOneOrNull()

// ✅ 判断是否存在
val exists = sqlClient.createQuery(SysUser::class) {
    where(table.userName eq userName)
    select(table.id)
}.fetchOneOrNull() != null
```

#### 7. 删除操作

```kotlin
// ✅ 根据ID删除
val result = sqlClient.deleteById(SysUser::class, userId)
val affectedRows = result.totalAffectedRowCount.toInt()

// ✅ 批量删除
val result = sqlClient.deleteByIds(SysUser::class, listOf(1L, 2L, 3L))
val affectedRows = result.totalAffectedRowCount.toInt()
```

#### 8. insert/update 操作

Jimmer 使用 Draft API 进行数据修改：

**插入新对象**
```kotlin
val newUser = SysUserDraft.`$`.produce {
    userName = "admin"
    nickName = "管理员"
    status = "0"
}
sqlClient.save(newUser)
```

**更新现有对象**
```kotlin
val existing = sqlClient.findById(SysUser::class, userId)
val updated = SysUserDraft.`$`.produce(existing) {
    nickName = "新昵称"
}
sqlClient.save(updated)
```

### ⚠️ 常见错误

#### 1. 类型参数错误

```kotlin
// ❌ 错误：使用 Entity 而非 Entity::class
sqlClient.createQuery(SysUser) { ... }  // 编译错误

// ✅ 正确：使用 Entity::class
sqlClient.createQuery(SysUser::class) { ... }
```

#### 2. 属性访问错误

```kotlin
// ❌ 错误：使用 nullable 语法
where(table.`id?` eq userId)

// ✅ 正确：直接使用属性名
where(table.id eq userId)
```

#### 3. 分页结果处理错误

```kotlin
// ❌ 错误：解构 executePageable 返回值
val (users, total) = query.executePageable(pageable)

// ✅ 正确：使用 execute() 返回的 Page 对象
val users = query.execute(pageable)
users.content        // 数据
users.totalElements  // 总数
```

### 🔧 KSP 配置要求

**重要**：使用 Jimmer DSL 的模块必须配置 KSP！

#### build.gradle.kts 配置

```kotlin
plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")  // 必须添加
}

dependencies {
    // ... 其他依赖
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")  // 必须添加
}
```

#### 需要配置的模块

- ✅ foxden-common-jimmer（已配置）
- ✅ foxden-domain-system（已配置）
- ⏳ 其他 domain 模块（如需要）

### 🎯 学习建议

1. **从简单查询开始**：先掌握 `createQuery`, `findById`, `deleteById`
2. **逐步增加复杂度**：动态条件、分页、关联查询
3. **参考已编译通过的代码**：项目中 `foxden-domain-system` 模块的实现示例
4. **利用 IDE 提示**：Kotlin DSL 提供完整的类型检查和自动补全
