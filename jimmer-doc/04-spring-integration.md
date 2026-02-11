# Jimmer Spring Integration Guide for FoxDen

## Overview

This guide covers Jimmer's Spring Boot integration for FoxDen's Kotlin + PostgreSQL stack.

## Project Setup

### Gradle Dependencies

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.babyfish.jimmer:jimmer-spring-boot-starter:0.10.6")
    implementation("org.babyfish.jimmer:jimmer-kotlin:0.10.6")
    ksp("org.babyfish.jimmer:jimmer-ksp:0.10.6")
}
```

### KSP Configuration

```kotlin
// ksp.useKSP2 = true in gradle.properties
```

## Application Configuration

### application.yaml

```yaml
jimmer:
  # Language: Kotlin
  language: kotlin

  # Database Dialect for PostgreSQL
  dialect: org.babyfish.jimmer.sql.dialect.PostgresDialect

  # SQL Logging
  show-sql: true
  pretty-sql: true

  # Database Validation
  database-validation-mode: ERROR

  # Executor Type (optional)
  executor-type: POOLED

  # Lazy Loading (optional)
  lazy-loading-enabled: true

  # Max In Parameter Count (PostgreSQL default)
  max-in-parameter-count: 35555
```

### DataSource Configuration

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/foxden
    username: foxden
    password: foxden

    # HikariCP (default)
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## Bean Configuration

### Manual Configuration (if needed)

```kotlin
@Configuration
class JimmerConfig {

    @Bean
    fun jimmerProperties(): JimmerProperties {
        return JimmerProperties().apply {
            dialect = PostgresDialect()
            showSql = true
            prettySql = true
        }
    }

    @Bean
    fun sqlClient(
        dataSource: DataSource,
        jimmerProperties: JimmerProperties
    ): KSqlClient {
        return KSqlClient {
            setConnectionManager(dataSource)
            setDialect(PostgresDialect())
            setExecutor(PooledDataSourceExecutor(dataSource))
        }
    }
}
```

### Auto-Configuration (Recommended)

With Spring Boot Starter, `KSqlClient` bean is auto-created. Just inject it:

```kotlin
@Service
class SysUserServiceImpl(
    private val sqlClient: KSqlClient  // Auto-injected
) : SysUserService {
    // Implementation
}
```

## Service Layer Integration

### Constructor Injection

```kotlin
@Service
class SysUserServiceImpl(
    private val sqlClient: KSqlClient,
    private val roleService: SysRoleService,
    private val deptService: SysDeptService
) : SysUserService {

    override fun selectUserList(bo: SysUserBo): List<SysUserVo> {
        return sqlClient.createQuery(SysUser::class) {
            where(table.delFlag eq "0")
            select(table)
        }.execute().map { entityToVo(it) }
    }
}
```

### Multiple SqlClient Instances

```kotlin
@Configuration
class JimmerConfig {

    @Bean
    @Primary
    fun sqlClient(dataSource: DataSource): KSqlClient {
        return KSqlClient {
            setConnectionManager(SimpleConnectionManager(dataSource))
            setDialect(PostgresDialect())
        }
    }

    @Bean
    fun readOnlySqlClient(readOnlyDataSource: DataSource): KSqlClient {
        return KSqlClient {
            setConnectionManager(SimpleConnectionManager(readOnlyDataSource))
            setDialect(PostgresDialect())
        }
    }
}
```

## Transaction Management

### @Transactional Annotation

```kotlin
import org.springframework.transaction.annotation.Transactional

@Service
class SysUserServiceImpl(
    private val sqlClient: KSqlClient
) : SysUserService {

    @Transactional
    override fun insertUser(bo: SysUserBo): Int {
        // All operations in one transaction
        val newUser = SysUserDraft.`$`.produce {
            userName = bo.userName
            nickName = bo.nickName
        }

        val result = sqlClient.save(newUser)
        return if (result.isModified) 1 else 0
    }
}
```

### Transaction Propagation

```kotlin
@Transactional(propagation = Propagation.REQUIRES_NEW)
override fun insertUserWithAudit(bo: SysUserBo): Int {
    // New transaction
}

@Transactional(propagation = Propagation.REQUIRED)
override fun updateUser(bo: SysUserBo): Int {
    // Join existing transaction or create new
}
```

### Rollback Handling

```kotlin
@Transactional(rollbackFor = [Exception::class])
override fun insertUser(bo: SysUserBo): Int {
    // Rollback on any exception
}

@Transactional(rollbackFor = [ServiceException::class])
override fun updateUser(bo: SysUserBo): Int {
    // Rollback only on ServiceException
}
```

## Exception Handling

### Global Exception Handler

```kotlin
@RestControllerAdvice
class JimmerExceptionHandler {

    @ExceptionHandler(BatchIntegrityViolationException::class)
    fun handleBatchIntegrityViolation(e: BatchIntegrityViolationException): R<Void> {
        val message = when {
            e.message?.contains("unique") == true -> "数据已存在"
            e.message?.contains("foreign key") == true -> "关联数据不存在"
            else -> "数据保存失败"
        }
        return R.fail(message)
    }

    @ExceptionHandler(TooManyResultsException::class)
    fun handleTooManyResults(e: TooManyResultsException): R<Void> {
        return R.fail("查询结果不唯一")
    }
}
```

## Cache Integration

### Redis Cache with Jimmer

```kotlin
@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(
        connectionFactory: LettuceConnectionFactory
    ): CacheManager {
        return RedisCacheManager.Builder
            .connectionFactory(connectionFactory)
            .cacheDefaults(
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(30))
                    .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                            .fromSerializer(StringRedisSerializer())
                    )
                    .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                            .fromSerializer(GenericJackson2JsonRedisSerializer(null))
                    )
            )
            .build()
    }
}
```

### Cacheable Service Methods

```kotlin
@Service
class SysDictServiceImpl(
    private val sqlClient: KSqlClient
) : SysDictService {

    @Cacheable(value = ["dict"], key = "#dictType")
    override fun selectDictDataByType(dictType: String): List<DictDataVo> {
        return sqlClient.createQuery(SysDictData::class) {
            where(table.dictType eq dictType)
            where(table.status eq "0")
            orderBy(table.dictSort.asc())
            select(table)
        }.execute().map { entityToVo(it) }
    }

    @CacheEvict(value = ["dict"], key = "#bo.dictType")
    override fun updateDictData(bo: SysDictDataBo): Int {
        // Update logic
        ...
    }
}
```

## Multi-Tenancy Integration

### Tenant Context Filter

```kotlin
@Component
class TenantFilter(
    private val tenantHelper: TenantHelper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val tenantId = request.getHeader(TenantConstants.TENANT_ID)
        tenantId?.let {
            TenantHelper.setTenantId(it)
        }

        try {
            filterChain.doFilter(request, response)
        } finally {
            TenantHelper.clear()
        }
    }
}
```

### Tenant-Aware Queries

```kotlin
@Service
class SysUserServiceImpl(
    private val sqlClient: KSqlClient
) : SysUserService {

    override fun selectUserList(bo: SysUserBo): List<SysUserVo> {
        // Tenant filtering is automatic with CommTenant trait
        return sqlClient.createQuery(SysUser::class) {
            where(table.delFlag eq "0")
            select(table)
        }.execute().map { entityToVo(it) }
    }

    // Switch tenant dynamically
    override fun selectUserByTenant(tenantId: String, userId: Long): SysUserVo? {
        return TenantHelper.dynamic(tenantId) {
            sqlClient.findById(SysUser::class, userId)
        }?.let { entityToVo(it) }
    }
}
```

## Data Permissions Integration

### Global Filter for Data Permissions

```kotlin
@Component
class DataPermissionFilter : GlobalFilter {

    override fun apply(
        entity: EntityType<*>,
        where: MutableutableTableFilter?
    ): MutableTableFilter? {
        // Apply data permission filter based on user context
        val loginUser = LoginHelper.getLoginUser() ?: return null

        // Only apply to entities with @DataPermission
        if (!hasDataPermission(entity)) {
            return null
        }

        return MutableTableFilter {
            // Add data permission conditions
            val deptIds = getUserDeptIds(loginUser.userId)
            where(table.deptId.`in`(deptIds))
        }
    }

    private fun hasDataPermission(entity: EntityType<*>): Boolean {
        return entity.getAnnotations()
            .any { it is DataPermission }
    }

    private fun getUserDeptIds(userId: Long?): List<Long> {
        // Fetch user's accessible departments
        ...
    }
}
```

## Testing Integration

### Test Configuration

```kotlin
@SpringBootTest
class JimmerIntegrationTest {

    @Autowired
    private lateinit var sqlClient: KSqlClient

    @Test
    fun testInsertUser() {
        val user = SysUserDraft.`$`.produce {
            userName = "test"
            nickName = "Test User"
        }

        val result = sqlClient.save(user)

        assertTrue(result.isModified)
        assertNotNull(result.modifiedEntity.id)
    }

    @Test
    @Transactional
    fun testQueryUser() {
        val users = sqlClient.createQuery(SysUser::class) {
            where(table.userName eq "test")
            select(table)
        }.execute()

        assertFalse(users.isEmpty())
    }
}
```

## Best Practices

### 1. Constructor Injection Only

```kotlin
// ✅ Good
@Service
class UserService(
    private val sqlClient: KSqlClient
)

// ❌ Bad
@Service
class UserService {
    @Autowired
    private lateinit var sqlClient: KSqlClient
}
```

### 2. Use @Transactional for Multi-Step Operations

```kotlin
@Transactional
override fun insertUserWithRoles(bo: SysUserBo): Long {
    // Multiple operations in one transaction
    val userId = insertUser(bo)
    insertUserRoles(userId, bo.roleIds)
    insertUserPosts(userId, bo.postIds)
    return userId
}
```

### 3. Handle Lazy Loading Properly

```kotlin
// ✅ Good - Use ObjectFetcher
val user = sqlClient.findById(SysUser::class, userId) {
    allScalarFields()
    dept {
        allScalarFields()
    }
}

// ❌ Bad - N+1 queries
val user = sqlClient.findById(SysUser::class, userId)
val deptName = user.dept?.name  // Triggers extra query
```

### 4. Use DataSource for Multiple Databases

```yaml
spring:
  datasource:
    # Primary
    url: jdbc:postgresql://localhost:5432/foxden
    username: foxden

  # Secondary
  sec-datasource:
    url: jdbc:postgresql://localhost:5432/foxden_sec
    username: foxden_sec
```

## Anti-Patterns to Avoid

- ❌ Don't use field injection (@Autowired on fields)
- ❌ Don't forget @Transactional for multi-step operations
- ❌ Don't create new KSqlClient instances manually
- ❌ Don't mix transaction managers
- ❌ Don't forget to clear thread-local variables (tenant, etc.)
- ❌ Don't use synchronous/blocking calls in async methods
- ❌ Don't forget exception rollback configuration
