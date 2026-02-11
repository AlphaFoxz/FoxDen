---
glob:
  - "**/service/**/*ServiceImpl.kt"
  - "**/service/impl/*.kt"
---

# Service Layer Implementation Rules

When implementing services in FoxDen, follow these patterns:

## Constructor Injection

Use constructor injection for all dependencies:

```kotlin
@Service
class SysUserServiceImpl(
    private val sqlClient: KSqlClient,
    private val roleService: SysRoleService,
    private val deptService: SysDeptService
) : SysUserService {
    // Implementation
}
```

## Basic Query Pattern

Use `createQuery` with Kotlin DSL:

```kotlin
override fun selectUserList(bo: SysUserBo): List<SysUserVo> {
    val users = sqlClient.createQuery(SysUser::class) {
        where(table.delFlag eq "0")
        orderBy(table.id.asc())
        select(table)
    }.execute()

    return users.map { entityToVo(it) }
}
```

## Dynamic Query Conditions

Use `?.let` and `takeIf` for dynamic conditions:

```kotlin
override fun selectPageUserList(bo: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
    val pager = sqlClient.createQuery(SysUser::class) {
        where(table.delFlag eq "0")

        // Dynamic conditions
        bo.userId?.let { where(table.id eq it) }
        bo.userName?.takeIf { it.isNotBlank() }?.let {
            where(table.userName like "%${it}%")
        }
        bo.status?.takeIf { it.isNotBlank() }?.let {
            where(table.status eq it)
        }

        orderBy(table.id.asc())
        select(table)
    }.fetchPage(
        pageQuery.getPageNumOrDefault(),
        pageQuery.getPageSizeOrDefault()
    )

    return TableDataInfo.build(pager.rows.map { entityToVo(it) }, pager.totalRowCount)
}
```

## Fetch Single Entity

```kotlin
// ✅ findById
override fun selectUserById(userId: Long): SysUserVo? {
    val user = sqlClient.findById(SysUser::class, userId) ?: return null
    return entityToVo(user)
}

// ✅ fetchOne with conditions
override fun selectUserByUserName(userName: String): SysUserVo? {
    val user = sqlClient.createQuery(SysUser::class) {
        where(table.userName eq userName)
        select(table)
    }.fetchOneOrNull() ?: return null

    return entityToVo(user)
}
```

## Pagination

Use `fetchPage` for pagination:

```kotlin
override fun selectPageUserList(bo: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
    val pager = sqlClient.createQuery(SysUser::class) {
        where(table.delFlag eq "0")
        orderBy(table.id.asc())
        select(table)
    }.fetchPage(
        pageQuery.getPageNumOrDefault(),
        pageQuery.getPageSizeOrDefault()
    )

    return TableDataInfo.build(pager.rows.map { entityToVo(it) }, pager.totalRowCount)
}
```

## Insert with Draft API

```kotlin
override fun insertUser(bo: SysUserBo): Int {
    val newUser = SysUserDraft.`$`.produce {
        userName = bo.userName ?: throw ServiceException("用户名不能为空")
        nickName = bo.nickName
        email = bo.email
        status = bo.status ?: SystemConstants.NORMAL
        delFlag = "0"
        createTime = LocalDateTime.now()
    }

    val result = sqlClient.save(newUser)
    return if (result.isModified) 1 else 0
}
```

## Update with Draft API

```kotlin
override fun updateUser(bo: SysUserBo): Int {
    val userIdVal = bo.userId ?: return 0
    val existing = sqlClient.findById(SysUser::class, userIdVal)
        ?: throw ServiceException("用户不存在")

    val updated = SysUserDraft.`$`.produce(existing) {
        bo.nickName?.let { nickName = it }
        bo.email?.let { email = it }
        bo.status?.let { status = it }
        updateTime = LocalDateTime.now()
    }

    val result = sqlClient.save(updated)
    return if (result.isModified) 1 else 0
}
```

## Delete

```kotlin
override fun deleteUserById(userId: Long): Int {
    val result = sqlClient.deleteById(SysUser::class, userId)
    return result.totalAffectedRowCount.toInt()
}

override fun deleteUserByIds(userIds: Array<Long>): Int {
    val result = sqlClient.deleteByIds(SysUser::class, userIds.toList())
    return result.totalAffectedRowCount.toInt()
}
```

## Check Uniqueness

```kotlin
override fun checkUserNameUnique(user: SysUserBo): Boolean {
    val existing = sqlClient.createQuery(SysUser::class) {
        where(table.userName eq user.userName)
        user.userId?.let { where(table.id ne it) }
        select(table)
    }.fetchOneOrNull()

    return existing == null || existing.id == user.userId
}
```

## Count Records

```kotlin
override fun hasChildByDeptId(deptId: Long): Boolean {
    val count = sqlClient.createQuery(SysDept::class) {
        where(table.parentId eq deptId)
        where(table.delFlag eq "0")
        select(table.id)
    }.execute().count()

    return count > 0
}
```

## Transaction Management

Use `@Transactional` for multi-step operations:

```kotlin
@Transactional
override fun insertUser(bo: SysUserBo): Int {
    // Insert user
    // Assign roles
    // Assign posts
    // All in one transaction
}
```

## Data Permissions

Use `DataPermissionHelper.ignore` to bypass permission filtering when needed:

```kotlin
override fun selectUserById(userId: Long): SysUserVo? {
    val user = DataPermissionHelper.ignore(java.util.function.Supplier {
        sqlClient.findById(SysUser::class, userId)
    })
    // ...
}
```

## Common DSL Patterns

### Comparison Operators

```kotlin
where(table.id eq value)           // =
where(table.id ne value)           // <>
where(table.id gt value)           // >
where(table.id ge value)           // >=
where(table.id lt value)           // <
where(table.id le value)           // <=
```

### Like Operators

```kotlin
where(table.userName like "abc")     // Case sensitive, %abc% (add wildcards manually)
where(table.userName ilike "abc")    // Case insensitive
```

### Null Checks

```kotlin
where(table.email.isNull())          // IS NULL
where(table.email.isNotNull())      // IS NOT NULL
```

### In List

```kotlin
where(table.id.`in`(listOf(1L, 2L, 3L))
```

### Logical Operators

```kotlin
// AND (default when multiple where calls)
where(table.delFlag eq "0")
where(table.status eq "1")

// OR
where(
    table.userName ilike "admin",
    table.nickName ilike "admin"
).or()

// NOT
where(table.delFlag eq "0").not()
```

### Order By

```kotlin
orderBy(table.id.asc())
orderBy(table.createTime.desc())

// Multiple orders
orderBy(
    table.orderNum.asc(),
    table.id.asc()
)
```

## Prohibited

- ❌ Don't access lazy associations without explicit query
- ❌ Don't use `!!` non-null assertions
- ❌ Don't forget `?: return null` for findById
- ❌ Don't mix data permission logic with business logic
- ❌ Don't use JdbcTemplate for queries that Jimmer can handle
- ❌ Don't forget wildcards (%) for LIKE queries
- ❌ Don't forget that `like` is case-sensitive, use `ilike` for case-insensitive
