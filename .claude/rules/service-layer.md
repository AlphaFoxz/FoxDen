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

## Handling Jimmer Lazy Loading

**CRITICAL**: Jimmer associations are lazy-loaded. Always query related data explicitly:

```kotlin
// ❌ WRONG - causes UnloadedException
override fun selectUserById(userId: Long): SysUserVo? {
    val user = sqlClient.findById(SysUser::class, userId)
    val roles = user.roles  // Exception!
    return entityToVo(user)
}

// ✅ CORRECT - manual query
override fun selectUserById(userId: Long): SysUserVo? {
    val user = sqlClient.findById(SysUser::class, userId) ?: return null
    val roles = roleService.selectRolesByUserId(userId)
    return entityToVo(user, roles)
}
```

## Dynamic Query Conditions

Use `?.let` and `takeIf` for dynamic conditions:

```kotlin
override fun selectUserList(bo: SysUserBo): List<SysUserVo> {
    val users = sqlClient.createQuery(SysUser::class) {
        where(table.delFlag eq false)

        // Dynamic conditions
        bo.userId?.let { where(table.id eq it) }
        bo.userName?.takeIf { it.isNotBlank() }?.let {
            where(table.userName like it)
        }
        bo.status?.takeIf { it.isNotBlank() }?.let {
            where(table.status eq it)
        }

        orderBy(table.createTime.desc())
        select(table)
    }.execute()

    return users.map { entityToVo(it, withRoles = false) }
}
```

## Pagination

Use Spring's `PageRequest` with Jimmer:

```kotlin
override fun selectUserPage(bo: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
    val pageable = PageRequest.of(pageQuery.pageNum - 1, pageQuery.pageSize)
    val page = sqlClient.createQuery(SysUser::class) {
        where(table.delFlag eq false)
        select(table)
    }.execute(pageable)

    return TableDataInfo.build(page.content.map { entityToVo(it) }, page.totalElements)
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

## Transaction Management

Use `@Transactional` for multi-step operations:

```kotlin
@Transactional
override fun insertUser(bo: SysUserBo): Long? {
    // Insert user
    // Assign roles
    // Assign posts
    // All in one transaction
}
```

## Prohibited

- ❌ Don't access lazy associations without explicit query
- ❌ Don't use `!!` non-null assertions
- ❌ Don't forget `?: return null` for findById
- ❌ Don't mix data permission logic with business logic
