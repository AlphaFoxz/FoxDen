---
name: foxden-project
description: Expert knowledge for FoxDen multi-tenant SaaS system built with Kotlin, Spring Boot, and Jimmer ORM. Activates when working on FoxDen codebase tasks.
---

# FoxDen Project Expert

You are an expert on the FoxDen project, a multi-tenant SaaS system built with Kotlin, Spring Boot, and Jimmer ORM.

## Technology Stack

- **Language**: Kotlin 2.3.0
- **Framework**: Spring Boot 3.5.10
- **ORM**: Jimmer 0.10.6 (Kotlin-first with KSP)
- **Database**: PostgreSQL
- **Security**: Sa-Token 1.44.0 with JWT
- **Build**: Gradle with Kotlin DSL

## Architecture

### Jimmer ORM Entity Model

Use trait-based entity design with reusable `@MappedSuperclass` traits:
- `CommId` - Primary key (auto-generated ID)
- `CommTenant` - Multi-tenancy support (tenantId)
- `CommInfo` - Audit fields (createBy, createTime, updateBy, updateTime, remark)
- `CommDelFlag` - Soft delete (delFlag with @LogicalDeleted("true"))

Example entity composition:
```kotlin
@Entity
@Table(name = "sys_user")
interface SysUser : CommDelFlag, CommId, CommInfo, CommTenant {
    val userName: String
    val nickName: String
    @ManyToMany
    @JoinTable(name = "sys_user_role")
    val roles: List<SysRole>
}
```

### Authentication Strategies

The system uses strategy pattern for multiple auth methods. Each strategy implements `AuthStrategy`:
- `PasswordAuthStrategy` - Username/password with BCrypt
- `SmsAuthStrategy` - SMS verification code
- `EmailAuthStrategy` - Email verification code
- `SocialAuthStrategy` - OAuth social login (JustAuth)
- `XcxAuthStrategy` - WeChat mini-program

Strategies are auto-registered with Spring using naming convention: `{type}AuthStrategy`

### Data Permissions

Use `@DataPermission` and `@DataColumn` annotations for data filtering:
- `@DataPermission` - Class or method-level annotation
- `@DataColumn` - Defines placeholder keys and column mappings
- `DataPermissionHelper.ignore { }` - Bypass filtering when needed

### Multi-Tenancy

- Tenant ID from HTTP header/parameter (`TenantConstants.TENANT_ID`)
- `TenantHelper.getTenantId()` - Retrieve current tenant
- `TenantHelper.dynamic(tenantId) { }` - Execute with specific tenant context
- Super admin can switch tenants dynamically

## Project Locations

- **Current Project**: `/mnt/f/idea_projects/FoxDen/` (Kotlin/Jimmer migration)
- **Legacy Project**: `/mnt/f/idea_projects/FoxDen/old-version/` (Java/MyBatis - Reference only)

**IMPORTANT**: When searching legacy code, always search in `old-version/` directory, never parent directories.

## Code Style

- Use Kotlin features: extension functions, data classes, coroutines
- Explicit nullability with `?` and `?: ""` for null safety
- Avoid `!!` non-null assertions unless necessary
- Naming:
  - Entity interfaces: PascalCase (e.g., `SysUser`)
  - Methods/functions: camelCase (e.g., `selectUserById`)
  - Constants: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)

## Layered Architecture

Controller → Service → Domain (Entity)

- Use constructor injection for dependencies
- DTO pattern: BO (input) → VO (output)
- Use business exceptions (ServiceException, UserException, etc.)

## Jimmer Query Patterns

### Basic Query
```kotlin
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(table)
}.execute()
```

### Dynamic Conditions
```kotlin
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    user.userName?.takeIf { it.isNotBlank() }?.let {
        where(table.userName like it)
    }
    select(table)
}.execute()
```

### Pagination
```kotlin
val pageable = PageRequest.of(pageNum - 1, pageSize)
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(table)
}.execute(pageable)
```

### Handling Lazy Loading

**Important**: Jimmer associations (`@ManyToMany`, `@OneToMany`) are lazy-loaded by default. Accessing them causes `UnloadedException`.

**Solution**: Manually query related data or use Fetcher API.

```kotlin
// ❌ Wrong - causes UnloadedException
val user = sqlClient.findById(SysUser::class, userId)
val roles = user.roles  // Exception!

// ✅ Correct - manual query
val user = sqlClient.findById(SysUser::class, userId)
val roles = roleService.selectRolesByUserId(userId)
```

## Common Patterns

### Service with Repository
```kotlin
@Service
class SysUserServiceImpl(
    private val sqlClient: KSqlClient,
    private val roleService: SysRoleService
) : SysUserService {
    override fun selectUserById(userId: Long): SysUserVo? {
        val user = sqlClient.findById(SysUser::class, userId) ?: return null
        // Manual query for associations
        val roles = roleService.selectRolesByUserId(userId)
        return entityToVo(user, roles)
    }
}
```

### Controller with Permissions
```kotlin
@RestController
@RequestMapping("/system/user")
class SysUserController(
    private val userService: SysUserService
) {
    @SaCheckPermission("system:user:query")
    @GetMapping("/list")
    fun list(userBo: SysUserBo): R<TableDataInfo<SysUserVo>> {
        return R.ok(userService.selectUserList(userBo))
    }
}
```

## Build Commands

```bash
# Build entire project
./gradlew build

# Run application
./gradlew :foxden-app:foxden-app-admin:bootRun

# Run tests
./gradlew test

# Stop Gradle Daemon
./gradlew --stop
```

## Prohibited Actions

- ❌ Don't modify core architecture without understanding
- ❌ Don't delete important comments
- ❌ Don't introduce unnecessary dependencies
- ❌ Don't hardcode sensitive information (passwords, keys)
- ❌ Don't bypass security checks
- ❌ Don't search legacy code in parent directories (use old-version/)

## Key Documentation References

- `.claude/JIMMER_GUIDE.md` - Comprehensive Jimmer ORM guide
- `.claude/OLD_VERSION_GUIDE.md` - Legacy project navigation
- `.claude/migration-guide.md` - Project migration guide
- `CLAUDE.md` - Project overview and architecture
