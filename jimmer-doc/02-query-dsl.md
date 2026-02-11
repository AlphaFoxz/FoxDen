# Jimmer Query DSL Guide for FoxDen

## Overview

This guide covers Jimmer's Kotlin DSL for querying data in FoxDen's PostgreSQL database.

## Basic Query Pattern

### Simple Query

```kotlin
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    orderBy(table.id.asc())
    select(table)
}.execute()
```

### Injection

Services inject `KSqlClient`:

```kotlin
@Service
class SysUserServiceImpl(
    private val sqlClient: KSqlClient
) : SysUserService {
    // Implementation
}
```

## Where Conditions

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
// Case sensitive (add wildcards manually)
where(table.userName like "abc")     // '%abc%'

// Case insensitive (PostgreSQL)
where(table.userName ilike "abc")    // '%abc%'

// With wildcards
where(table.userName ilike "%${keyword}%")
```

### Null Checks

```kotlin
where(table.email.isNull())          // IS NULL
where(table.email.isNotNull())      // IS NOT NULL
```

### In List

```kotlin
where(table.id.`in`(listOf(1L, 2L, 3L))
where(table.status.`in`(listOf("0", "1")))
```

### Logical Operators

```kotlin
// AND (multiple where calls)
where(table.delFlag eq "0")
where(table.status eq "1")

// OR
where(
    table.userName ilike "admin",
    table.nickName ilike "admin"
).or()

// NOT
where(table.delFlag eq "0").not()

// Grouped OR with AND
where(
    where(table.status eq "0"),
    where(table.status eq "1")
).or()
```

## Dynamic Query Conditions

### Null-Safe Conditions

```kotlin
override fun selectPageUserList(bo: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
    val pager = sqlClient.createQuery(SysUser::class) {
        where(table.delFlag eq "0")

        // Dynamic conditions
        bo.userId?.let { where(table.id eq it) }
        bo.userName?.takeIf { it.isNotBlank() }?.let {
            where(table.userName ilike "%${it}%")
        }
        bo.status?.takeIf { it.isNotBlank() }?.let {
            where(table.status eq it)
        }

        orderBy(table.id.asc())
        select(table)
    }.fetchPage(
        pageQuery.getPageNumOrDefault() - 1,  // Jimmer fetchPage expects 0-based index
        pageQuery.getPageSizeOrDefault()
    )

    return TableDataInfo.build(pager.rows.map { entityToVo(it) }, pager.totalRowCount)
}
```

### Optional Range Conditions

```kotlin
bo.startTime?.let {
    where(table.createTime ge it)
}
bo.endTime?.let {
    where(table.createTime le it)
}
```

## Ordering

### Single Order

```kotlin
orderBy(table.id.asc())
orderBy(table.createTime.desc())
```

### Multiple Orders

```kotlin
orderBy(
    table.orderNum.asc(),
    table.id.asc()
)
```

## Fetching Data

### Execute (List)

```kotlin
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(table)
}.execute()
```

### FetchOne (Single Result)

```kotlin
val user = sqlClient.createQuery(SysUser::class) {
    where(table.userName eq "admin")
    select(table)
}.fetchOneOrNull()

val user2 = sqlClient.createQuery(SysUser::class) {
    where(table.userName eq "admin")
    select(table)
}.fetchOne()  // Throws if not found
```

### FetchPage (Pagination)

```kotlin
val pager = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    orderBy(table.id.asc())
    select(table)
}.fetchPage(pageNum, pageSize)

// Access results
val users = pager.rows
val total = pager.totalRowCount
val totalPages = pager.totalPageCount
```

## Join Queries

### Simple Join

```kotlin
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    // Automatically joins dept
    select(
        table,
        table.dept
    )
}.execute()
```

### Join with Condition

```kotlin
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    where(table.dept.status eq "1")  // Auto join
    select(table)
}.execute()
```

### Multiple Joins

```kotlin
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    where(table.dept.parentId eq 1L)  // Join dept
    where(table.roles.any { it.roleKey eq "admin" })  // Join roles
    select(table)
}.execute()
```

## Object Fetcher

### Basic Fetcher

```kotlin
val user = sqlClient.findById(SysUser::class, userId) {
    // Fetch only specific properties
    allScalarFields()  // All scalar fields
    dept {
        allScalarFields()
    }
    roles {
        allScalarFields()
    }
}
```

### Partial Fetching

```kotlin
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(
        table.fetch {
            userName()
            nickName()
            dept {
                name()
            }
        }
    )
}.execute()
```

## Count Queries

```kotlin
val count = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(table.id)
}.execute().size()

// Or
val count = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(table.id)
}.fetchPage(1, 1).totalRowCount
```

## Existence Checks

```kotlin
val exists = sqlClient.createQuery(SysUser::class) {
    where(table.userName eq "admin")
    select(table.id)
}.execute().isNotEmpty()

val notExists = sqlClient.createQuery(SysUser::class) {
    where(table.userName eq "admin")
    select(table.id)
}.execute().isEmpty()
```

## Subquery

```kotlin
val users = sqlClient.createQuery(SysUser::class) {
    where(table.id.`in`(
        sqlClient.createSubQuery(SysUserRole::class) {
            where(table.roleId eq 1L)
            select(table.userId)
        }
    ))
    select(table)
}.execute()
```

## Common Patterns

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

### Has Child Records

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

### Date Range Query

```kotlin
val records = sqlClient.createQuery(SysOperLog::class) {
    bo.startTime?.let { where(table.operTime ge it) }
    bo.endTime?.let { where(table.operTime le it) }
    orderBy(table.operTime.desc())
    select(table)
}.execute()
```

## PostgreSQL-Specific Features

### Case-Insensitive Search

```kotlin
// Use ilike for PostgreSQL
where(table.userName ilike "%${keyword}%")
```

### Date/Time Functions

```kotlin
// Date comparison
where(table.createTime ge LocalDateTime.now().minusDays(7))

// Date trunc (if needed)
where(table.createTime.date() eq LocalDate.now())
```

## Data Permission Bypass

Use `DataPermissionHelper.ignore` to bypass filtering:

```kotlin
override fun selectUserById(userId: Long): SysUserVo? {
    val user = DataPermissionHelper.ignore(Supplier {
        sqlClient.findById(SysUser::class, userId)
    })
    return user?.let { entityToVo(it) }
}
```

## Anti-Patterns to Avoid

- ❌ Don't forget wildcards (`%`) for LIKE queries
- ❌ Don't use `like` when you need case-insensitive (use `ilike`)
- ❌ Don't forget to use `?.let` for optional conditions
- ❌ Don't use N+1 queries without proper fetch plans
- ❌ Don't fetch entire entities when you only need a few fields
- ❌ Don't forget that `fetchOneOrNull()` returns null for no results
- ❌ Don't use `!!` on `fetchOneOrNull()` results
