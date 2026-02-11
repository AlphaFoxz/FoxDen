# Jimmer Best Practices for FoxDen

## Overview

This guide covers best practices for using Jimmer ORM in FoxDen's Kotlin + PostgreSQL + Spring Boot stack.

## Entity Design

### 1. Use Trait Composition

```kotlin
// ✅ Good - Reusable traits
@Entity
interface SysUser : CommDelFlag, CommId, CommInfo, CommTenant {
    val userName: String
    val nickName: String
}

// Define traits once
@MappedSuperclass
interface CommId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long
}
```

### 2. Immutable Entities

```kotlin
// ✅ Good - All val
@Entity
interface SysUser {
    val userName: String
    val nickName: String
}

// ❌ Bad - Using var
@Entity
interface SysUser {
    var userName: String
    var nickName: String
}
```

### 3. Explicit Nullability

```kotlin
// ✅ Good - Explicit nullable
val email: String?
val phonenumber: String?

// ❌ Bad - Platform type (from Java)
val email: String!
```

### 4. Logical Deletion

```kotlin
// ✅ Good - Use Boolean for delFlag
@LogicalDeleted("true")
val delFlag: Boolean

// Or use String if needed
@LogicalDeleted("1")
val delFlag: String
```

### 5. Proper Association Mapping

```kotlin
// ✅ Good - Object associations
@ManyToOne
@JoinColumn(name = "dept_id")
val dept: SysDept?

// ❌ Bad - Only ID property (unless using IdView)
val deptId: Long?  // Missing association
```

## Query Patterns

### 1. Use ObjectFetcher for Partial Loading

```kotlin
// ✅ Good - Fetch only needed properties
val user = sqlClient.findById(SysUser::class, userId) {
    userName()
    nickName()
    dept {
        deptName()
    }
}

// ❌ Bad - Fetches entire entity
val user = sqlClient.findById(SysUser::class, userId)
```

### 2. Avoid N+1 Queries

```kotlin
// ✅ Good - Batch fetch associations
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(
        table.fetch {
            allScalarFields()
            dept {
                allScalarFields()
            }
            roles {
                allScalarFields()
            }
        }
    )
}.execute()

// ❌ Bad - Triggers N+1 queries
val users = sqlClient.createQuery(SysUser::class) {
    select(table)
}.execute()

users.forEach { user ->
    println(user.dept?.name)  // Extra query per user
}
```

### 3. Dynamic Query Conditions

```kotlin
// ✅ Good - Null-safe conditions
sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq "0")
    bo.userName?.takeIf { it.isNotBlank() }?.let {
        where(table.userName ilike "%${it}%")
    }
    bo.deptId?.let { where(table.dept.id eq it) }
    select(table)
}.execute()

// ❌ Bad - Unnecessary conditions
sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq "0")
    if (bo.userName != null) {
        where(table.userName eq bo.userName)
    }
    // May cause issues with null values
    select(table)
}.execute()
```

### 4. Pagination

```kotlin
// ✅ Good - Use fetchPage with 0-based index
val pager = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    orderBy(table.id.asc())
    select(table)
}.fetchPage(pageNum - 1, pageSize)  // Note: fetchPage expects 0-based index

return TableDataInfo.build(pager.rows, pager.totalRowCount)

// ❌ Bad - Manual pagination
val all = sqlClient.createQuery(SysUser::class) {
    select(table)
}.execute()

val page = all.drop((pageNum - 1) * pageSize).take(pageSize)
// Loads all data into memory!
```

## Mutation Patterns

### 1. Use Draft API

```kotlin
// ✅ Good - Draft API for type safety
val newUser = SysUserDraft.`$`.produce {
    userName = bo.userName
    nickName = bo.nickName
}

// ❌ Bad - Manual property copying
val newUser = SysUser()
newUser.userName = bo.userName
newUser.nickName = bo.nickName
```

### 2. Handle Null Values Properly

```kotlin
// ✅ Good - Explicit null handling
val updated = SysUserDraft.`$`.produce(existing) {
    if (bo.nickName != null) {
        nickName = bo.nickName
    } else {
        nickName = null  // Explicitly set to null
    }
}

// ❌ Bad - Conditional update
val updated = SysUserDraft.`$`.produce(existing) {
    bo.nickName?.let { nickName = it }
    // nickName not updated if bo.nickName is null
}
```

### 3. Transaction Boundaries

```kotlin
// ✅ Good - Clear transaction boundaries
@Transactional
override fun insertUserWithRoles(bo: SysUserBo): Long {
    val userId = insertUser(bo)
    insertUserRoles(userId, bo.roleIds)
    insertUserPosts(userId, bo.postIds)
    return userId
}

// ❌ Bad - No transaction
override fun insertUserWithRoles(bo: SysUserBo): Long {
    val userId = insertUser(bo)
    // If this fails, user is orphaned
    insertUserRoles(userId, bo.roleIds)
    return userId
}
```

### 4. Cascade Operations

```kotlin
// ✅ Good - Configure dissociate actions
@Entity
interface SysUser {
    @OnDissociate(DissociateAction.DELETE)
    val password: SysUserPassword?

    @OnDissociate(DissociateAction.SET_NULL)
    val dept: SysDept?
}

// ❌ Bad - Manual cascade handling
override fun deleteUser(userId: Long) {
    deleteUserPassword(userId)
    clearUserDept(userId)
    deleteUserRoles(userId)
    deleteUser(userId)
}
```

## Service Layer

### 1. Constructor Injection

```kotlin
// ✅ Good - Constructor injection
@Service
class SysUserServiceImpl(
    private val sqlClient: KSqlClient,
    private val roleService: SysRoleService
) : SysUserService

// ❌ Bad - Field injection
@Service
class SysUserServiceImpl {
    @Autowired
    private lateinit var sqlClient: KSqlClient
}
```

### 2. Exception Handling

```kotlin
// ✅ Good - Specific exceptions
override fun selectUserById(userId: Long): SysUserVo? {
    val user = sqlClient.findById(SysUser::class, userId)
        ?: return null
    return entityToVo(user)
}

// ❌ Bad - Generic exception handling
override fun selectUserById(userId: Long): SysUserVo {
    return try {
        entityToVo(sqlClient.findById(SysUser::class, userId)!!)
    } catch (e: Exception) {
        throw ServiceException("Error")
    }
}
```

### 3. Data Permissions

```kotlin
// ✅ Good - Bypass when needed
override fun selectUserById(userId: Long): SysUserVo? {
    val user = DataPermissionHelper.ignore {
        sqlClient.findById(SysUser::class, userId)
    }
    return user?.let { entityToVo(it) }
}

// ❌ Bad - Always bypassing security
@DataPermission(DataPermissionType.IGNORE)
override fun selectUserById(userId: Long): SysUserVo? {
    // Security disabled for this method
}
```

## Performance

### 1. Batch Operations

```kotlin
// ✅ Good - Batch insert
fun batchInsertUsers(users: List<UserBo>): Int {
    val drafts = users.map { bo ->
        UserDraft.`$`.produce {
            userName = bo.userName
            nickName = bo.nickName
        }
    }
    return sqlClient.insertAll(drafts).totalAffectedRowCount
}

// ❌ Bad - Individual inserts
fun batchInsertUsers(users: List<UserBo>): Int {
    var count = 0
    users.forEach { bo ->
        val draft = UserDraft.`$`.produce { ... }
        sqlClient.insert(draft)
        count++
    }
    return count
}
```

### 2. Index Usage

```sql
-- Create indexes for common query fields
CREATE INDEX idx_user_name ON sys_user(user_name);
CREATE INDEX idx_user_dept ON sys_user(dept_id);
CREATE INDEX idx_user_status ON sys_user(status, del_flag);
```

### 3. Avoid Fetching Unnecessary Data

```kotlin
// ✅ Good - Count only
val count = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(table.id)
}.execute().size

// ❌ Bad - Fetch all entities to count
val count = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(table)
}.execute().size
```

## Multi-Tenancy

### 1. Tenant Isolation

```kotlin
// ✅ Good - Automatic tenant filtering
@Service
class SysUserServiceImpl(
    private val sqlClient: KSqlClient
) {
    override fun selectUserList(): List<SysUserVo> {
        // Tenant filtering automatic with CommTenant
        return sqlClient.createQuery(SysUser::class) {
            where(table.delFlag eq false)
            select(table)
        }.execute().map { entityToVo(it) }
    }
}

// ❌ Bad - Manual tenant filtering
override fun selectUserList(tenantId: String): List<SysUserVo> {
    return sqlClient.createQuery(SysUser::class) {
        where(table.delFlag eq false)
        where(table.tenantId eq tenantId)  // Error-prone
        select(table)
    }.execute().map { entityToVo(it) }
}
```

### 2. Tenant Switching

```kotlin
// ✅ Good - Use TenantHelper
override fun syncUserToTenant(userId: Long, targetTenantId: String) {
    val user = TenantHelper.dynamic(targetTenantId) {
        sqlClient.findById(SysUser::class, userId)
    }
    // Process user in target tenant context
}

// ❌ Bad - Manual tenant handling
override fun syncUserToTenant(userId: Long, targetTenantId: String) {
    // Need to manually set tenantId
    val user = sqlClient.findById(SysUser::class, userId)
    val updated = UserDraft.`$`.produce(user!!) {
        tenantId = targetTenantId
    }
    sqlClient.save(updated)
}
```

## Testing

### 1. Test Transaction Management

```kotlin
// ✅ Good - Rollback after test
@Test
@Transactional
fun testInsertUser() {
    val user = insertUser(bo)
    assertNotNull(user)
    // Transaction rolled back automatically
}

// ❌ Bad - Manual cleanup
@Test
fun testInsertUser() {
    val user = insertUser(bo)
    assertNotNull(user)
    deleteUser(user.id)  // Manual cleanup
}
```

### 2. Test Data Isolation

```kotlin
// ✅ Good - Use test database
@SpringBootTest
class UserServiceTest(
    @Autowired private val service: SysUserService
) {
    @Test
    fun testQuery() {
        // Uses test database
    }
}

// ❌ Bad - Pollutes production data
@Test
fun testQuery() {
    // Runs against default database
}
```

## Common Anti-Patterns

### 1. Over-Fetching

```kotlin
// ❌ Bad - Fetches everything
val users = sqlClient.createQuery(SysUser::class) {
    select(table)
}.execute()

// ✅ Good - Fetch only needed fields
val users = sqlClient.createQuery(SysUser::class) {
    select(
        table.fetch {
            userName()
            nickName()
        }
    )
}.execute()
```

### 2. Using !! Operator

```kotlin
// ❌ Dangerous - Can throw NPE
val user = sqlClient.findById(SysUser::class, userId)!!
return entityToVo(user)

// ✅ Safe - Handle null properly
val user = sqlClient.findById(SysUser::class, userId) ?: return null
return entityToVo(user)
```

### 3. Forgetting Wildcards in LIKE

```kotlin
// ❌ Wrong - No wildcards
where(table.userName like keyword)

// ✅ Correct - With wildcards
where(table.userName ilike "%${keyword}%")
```

### 4. Case-Sensitive Search

```kotlin
// ❌ Wrong - Case sensitive
where(table.userName like "%${keyword}%")

// ✅ Correct - Case insensitive
where(table.userName ilike "%${keyword}%")
```

### 5. Not Paginating Large Results

```kotlin
// ❌ Bad - Can load millions of rows
val users = sqlClient.createQuery(SysUser::class) {
    select(table)
}.execute()

// ✅ Good - Always paginate with 0-based index
val pager = sqlClient.createQuery(SysUser::class) {
    select(table)
}.fetchPage(pageNum - 1, pageSize)  // Note: fetchPage expects 0-based index
```

## Checklist

### Entity Definition
- [ ] Using trait composition
- [ ] All properties are `val`
- [ ] Explicit nullable types (`?`)
- [ ] Proper association mapping
- [ ] `@OnDissociate` configured for sensitive associations
- [ ] `@LogicalDeleted` with explicit value

### Query
- [ ] Using `ObjectFetcher` for partial loading
- [ ] Avoiding N+1 queries
- [ ] Using `fetchPage` for pagination
- [ ] Using `ilike` for case-insensitive search
- [ ] Adding wildcards to LIKE queries
- [ ] Using `?.let` for optional conditions

### Mutation
- [ ] Using Draft API
- [ ] Handling null values properly
- [ ] Using `@Transactional` for multi-step operations
- [ ] Checking uniqueness before insert
- [ ] Using batch operations for bulk changes

### Service
- [ ] Constructor injection
- [ ] Proper exception handling
- [ ] Data permissions properly configured
- [ ] Using `DataPermissionHelper.ignore` when needed

### Performance
- [ ] Using batch operations
- [ ] Proper indexing on query fields
- [ ] Not fetching unnecessary data
- [ ] Using count queries instead of loading all data
