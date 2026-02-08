# Jimmer ORM 使用指南

> 本文档旨在帮助 AI 理解和使用 Jimmer ORM 框架
> 官方文档：https://babyfish-ct.github.io/jimmer-doc/zh

## 目录

- [什么是 Jimmer](#什么是-jimmer)
- [核心概念](#核心概念)
- [实体定义](#实体定义)
- [查询数据](#查询数据)
- [保存数据](#保存数据)
- [FoxDen 项目中的 Jimmer 使用](#foxden-项目中的-jimmer-使用)
- [常见模式](#常见模式)

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

```kotlin
// 定义 Fetcher
val USER_FETCHER = SysUserFetcher.$
    .allScalarFields()  // 所有标量字段
    .dept {             // 关联的部门
        id()
        name()
    }
    .roles {            // 关联的角色列表
        id()
        roleName()
        roleKey()
    }

// 使用 Fetcher 查询
fun getUserWithRoles(userId: Long): SysUser? {
    return sqlClient.findById(SysUser::class, userId, USER_FETCHER)
}
```

**Fetcher 优势：**
- ✅ 避免 N+1 问题（自动 JOIN 或批量查询）
- ✅ 按需加载字段
- ✅ 类型安全
- ✅ 支持递归查询（树形结构）

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

## 参考资料

- [Jimmer 官方文档](https://babyfish-ct.github.io/jimmer-doc/zh)
- [GitHub 仓库](https://github.com/babyfish-ct/jimmer)
- [项目介绍](https://babyfish-ct.github.io/jimmer-doc/zh/docs/overview/introduction/)
- [DTO 语言](https://babyfish-ct.github.io/jimmer-doc/zh/docs/dto/overview/)
- [Fetcher 教程](https://babyfish-ct.github.io/jimmer-doc/zh/docs/query/object-fetcher/overview/)
- [Save Command 教程](https://babyfish-ct.github.io/jimmer-doc/zh/docs/save/save-command/overview/)
- [SQL DSL 教程](https://babyfish-ct.github.io/jimmer-doc/zh/docs/query/dsl/overview/)
