# Service Layer Implementation Rules

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

## Save vs createUpdate Decision Tree

| Operation | Method Type | Use |
|-----------|-------------|-----|
| **INSERT** | `insertXxx()`, `registerXxx()` | `sqlClient.save(Draft)` - Draft API for type-safe entity creation |
| **UPDATE** | `updateXxx()` | `sqlClient.createUpdate()` - Direct UPDATE, better performance |
| **DELETE** | `deleteXxx()` | `sqlClient.deleteById/deleteByIds()` |

**Rule of thumb:**
- Creating new entity or handling associations? → `save(Draft)`
- Updating existing fields? → `createUpdate()`

## Query Patterns

### Dynamic Conditions with Null Safety

```kotlin
override fun selectPageUserList(bo: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
    val pager = sqlClient.createQuery(SysUser::class) {
        where(table.delFlag eq "0")
        bo.userName?.takeIf { it.isNotBlank() }?.let {
            where(table.userName ilike "%$it%")
        }
        bo.status?.let { where(table.status eq it) }
        orderBy(table.id.asc())
        select(table)
    }.fetchPage(pageQuery.pageNum - 1, pageQuery.pageSize)
    return TableDataInfo.build(pager.rows.map { entityToVo(it) }, pager.totalRowCount)
}
```

### Fetch Single Entity

```kotlin
override fun selectUserById(userId: Long): SysUserVo? {
    val user = sqlClient.findById(SysUser::class, userId) ?: return null
    return entityToVo(user)
}
```

### Check Uniqueness

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

## Update Patterns

### Single Field Update

```kotlin
override fun updateUserStatus(userId: Long, status: String): Int {
    return sqlClient.createUpdate(SysUser::class) {
        where(table.id eq userId)
        set(table.status, status)
        set(table.updateTime, LocalDateTime.now())
    }.execute()
}
```

### Multi-Field Update with Null Safety

```kotlin
override fun updateUser(user: SysUserBo): Int {
    val userIdVal = user.userId ?: return 0
    return sqlClient.createUpdate(SysUser::class) {
        where(table.id eq userIdVal)
        user.deptId?.let { set(table.deptId, it) }
        user.nickName?.let { set(table.nickName, it) }
        user.email?.let { set(table.email, it) }
        set(table.updateTime, LocalDateTime.now())
    }.execute()
}
```

## Data Permissions

Bypass permission filtering when needed:

```kotlin
override fun selectUserById(userId: Long): SysUserVo? {
    val user = DataPermissionHelper.ignore(java.util.function.Supplier {
        sqlClient.findById(SysUser::class, userId)
    })
    return user?.let { entityToVo(it) }
}
```

## Prohibited

- ❌ Don't use `sqlClient.save(Draft)` for UPDATE - use `createUpdate()` instead
- ❌ Don't access lazy associations without explicit query
- ❌ Don't use `!!` non-null assertions
- ❌ Don't forget `?: return null` for findById
- ❌ Don't mix data permission logic with business logic
- ❌ Don't use JdbcTemplate for queries that Jimmer can handle
- ❌ Don't forget wildcards (`%`) for LIKE queries
- ❌ Don't forget `like` is case-sensitive, use `ilike` for case-insensitive

## Common DSL Operators

```kotlin
// Comparison
where(table.id eq value)          // =
where(table.id ne value)          // <>
where(table.id gt value)          // >
where(table.id ge value)          // >=
where(table.id lt value)          // <
where(table.id le value)          // <=

// Like
where(table.userName like "abc")   // Case sensitive
where(table.userName ilike "abc")  // Case insensitive

// Null checks
where(table.email.isNull())
where(table.email.isNotNull())

// In list
where(table.id.`in`(listOf(1L, 2L, 3L)))

// Logical
where(table.delFlag eq "0")
where(table.status eq "1")       // AND (default)
where(table.userName ilike "a", table.nickName ilike "a").or()  // OR
where(table.delFlag eq "0").not() // NOT
```
