# Jimmer Configuration Guide for FoxDen (PostgreSQL)

## Overview

This guide covers Jimmer configuration for FoxDen's Kotlin + PostgreSQL + Spring Boot stack.

## Application Configuration

### application.yaml

```yaml
jimmer:
  # Language Setting
  language: kotlin

  # PostgreSQL Dialect
  dialect: org.babyfish.jimmer.sql.dialect.PostgresDialect

  # SQL Logging
  show-sql: true
  pretty-sql: true
  inline-sql-variables: true

  # Database Validation
  database-validation-mode: ERROR

  # Executor Type
  executor-type: POOLED

  # Lazy Loading
  lazy-loading-enabled: true

  # Max Parameters (PostgreSQL default: 35555)
  max-in-parameter-count: 35555

  # Connection Management
  connection-max-active: 100
  connection-max-idle: 20
  connection-min-idle: 5
  connection-test-query: SELECT 1

  # Transaction Isolation (optional)
  default-transaction-isolation: 2  # READ_COMMITTED
```

## PostgreSQL Dialect

### Why PostgresDialect?

The PostgreSQL dialect provides:
- Native PostgreSQL SQL generation
- Proper type mapping (JSONB, Arrays, etc.)
- Case-insensitive ILIKE support
- LIMIT/OFFSET pagination
- PostgreSQL-specific functions

### Dialect Configuration

```kotlin
// In Java/Kotlin config class
@Bean
fun dialect(): Dialect = PostgresDialect()

// Or in application.yaml
jimmer:
  dialect: org.babyfish.jimmer.sql.dialectPostgresDialect
```

## DataSource Configuration

### Primary DataSource

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/foxden
    username: foxden
    password: foxden_password

    # HikariCP Settings
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      connection-test-query: SELECT 1
      pool-name: FoxDenHikariCP
```

### Multiple DataSources

```yaml
spring:
  datasource:
    # Primary
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/foxden
    username: foxden
    password: foxden_password

    # Read Replica
    read:
      driver-class-name: org.postgresql.Driver
      url: jdbc:postgresql://read-replica:5432/foxden
      username: foxden_read
      password: foxden_read_password
```

```kotlin
@Configuration
class DataSourceConfig {

    @Bean
    @Primary
    fun dataSource(): DataSource {
        return HikariDataSource().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/foxden"
            username = "foxden"
            password = "foxden_password"
            driverClassName = "org.postgresql.Driver"
        }
    }

    @Bean
    fun readDataSource(): DataSource {
        return HikariDataSource().apply {
            jdbcUrl = "jdbc:postgresql://read-replica:5432/foxden"
            username = "foxden_read"
            password = "foxden_read_password"
            driverClassName = "org.postgresql.Driver"
        }
    }
}
```

## Type Mapping

### PostgreSQL to Kotlin Types

| PostgreSQL | Kotlin | Notes |
|------------|---------|--------|
| BIGINT | Long | Auto-generated ID |
| VARCHAR | String | Default string type |
| TEXT | String | Long text |
| BOOLEAN | Boolean | Use for delFlag |
| TIMESTAMP | LocalDateTime | Audit fields |
| DATE | LocalDate | Date only |
| JSONB | Map<String, Any>? | Requires configuration |
| ARRAY<String> | List<String>? | Array types |
| NUMERIC | BigDecimal | Precise decimal |

### JSONB Configuration

```kotlin
// In build.gradle.kts
dependencies {
    implementation("org.babyfish.jimmer:jimmer-jackson:0.10.6")
}
```

```kotlin
@Entity
interface SysConfig {
    @Column(name = "config_value")
    val configValue: Map<String, Any>?
}
```

## Validation Configuration

### Database Validation Mode

```yaml
jimmer:
  # Options: NONE, ERROR, WARNING
  database-validation-mode: ERROR
```

- **ERROR**: Throw exception on validation failure (production)
- **WARNING**: Log warnings on validation failure (development)
- **NONE**: Skip validation (not recommended)

### Connection Validation

```yaml
spring:
  datasource:
    hikari:
      connection-test-query: SELECT 1
      validation-timeout: 5000
```

## SQL Logging Configuration

### Development Logging

```yaml
jimmer:
  show-sql: true
  pretty-sql: true
  inline-sql-variables: true
```

### Logback Configuration

```xml
<!-- logback-spring.xml -->
<configuration>
    <logger name="org.babyfish.jimmer.sql" level="DEBUG"/>

    <!-- Pretty print SQL -->
    <logger name="org.babyfish.jimmer.sql.Executable" level="INFO"/>
</configuration>
```

### Production Logging

```yaml
jimmer:
  show-sql: false  # Disable for performance
```

## Connection Pool Configuration

### HikariCP Tuning

```yaml
spring:
  datasource:
    hikari:
      # Pool sizing
      maximum-pool-size: 20
      minimum-idle: 5

      # Timeout settings
      connection-timeout: 30000      # 30 seconds
      idle-timeout: 600000          # 10 minutes
      max-lifetime: 1800000         # 30 minutes

      # Validation
      connection-test-query: SELECT 1
      validation-timeout: 5000

      # Performance
      auto-commit: true
      transaction-isolation: TRANSACTION_READ_COMMITTED
```

### PooledExecutor Configuration

```kotlin
@Configuration
class JimmerConfig {

    @Bean
    fun sqlClient(dataSource: DataSource): KSqlClient {
        return KSqlClient {
            setConnectionManager(SimpleConnectionManager(dataSource))
            setExecutor(PooledDataSourceExecutor(dataSource))
            setDialect(PostgresDialect())
        }
    }
}
```

## Transaction Configuration

### Transaction Manager

```kotlin
@Configuration
@EnableTransactionManagement
class TransactionConfig {

    @Bean
    fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }
}
```

### Transaction Isolation Levels

```kotlin
@Transactional(isolation = Isolation.READ_COMMITTED)
fun someMethod() {
    // Uses READ_COMMITTED isolation
}
```

Isolation levels for PostgreSQL:
- **0**: READ_UNCOMMITTED (not supported by PostgreSQL)
- **1**: READ_COMMITTED (default)
- **2**: REPEATABLE_READ
- **3**: SERIALIZABLE

## Cache Configuration

### Local Cache

```yaml
jimmer:
  cache:
    # Enable local cache
    local:
      enabled: true
      capacity: 1000
      ttl: 10m
      object-cache-expire-on-write: true
```

### Redis Cache

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      timeout: 5000
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
```

```kotlin
@Configuration
class JimmerCacheConfig {

    @Bean
    fun cacheStrategy(): CacheStrategy {
        return CacheStrategy(
            CacheFactory.local(
                maxSize = 1000,
                ttl = Duration.ofMinutes(10)
            ),
            CacheFactory.redis(
                redisTemplate = redisTemplate,
                keyPrefix = "jimmer:",
                ttl = Duration.ofMinutes(30)
            )
        )
    }
}
```

## Multi-Tenancy Configuration

### Tenant Properties

```kotlin
@Configuration
@ConfigurationProperties(prefix = "foxden.tenant")
class TenantProperties {
    var enabled: Boolean = true
    var defaultTenantId: String = "000000"
    var ignoreTables: List<String> = listOf()
}
```

### Tenant Filter Configuration

```kotlin
@Component
class TenantConfig(
    private val tenantProperties: TenantProperties
) : GlobalFilter {

    override fun apply(
        entity: EntityType<*>,
        where: MutableTableFilter?
    ): MutableTableFilter? {
        // Skip if tenant disabled
        if (!tenantProperties.enabled) {
            return null
        }

        // Skip ignored tables
        if (entity.tableName in tenantProperties.ignoreTables) {
            return null
        }

        // Apply tenant filter to entities with CommTenant
        if (hasTenantField(entity)) {
            return MutableTableFilter {
                val tenantId = TenantHelper.getTenantId()
                where(table.tenantId eq tenantId)
            }
        }

        return null
    }

    private fun hasTenantField(entity: EntityType<*>): Boolean {
        return entity.props.any { it.name == "tenantId" }
    }
}
```

## Performance Tuning

### Batch Size Configuration

```kotlin
@Service
class UserService(
    private val sqlClient: KSqlClient
) {
    private val batchSize = 1000

    fun batchInsertUsers(users: List<UserBo>) {
        users.chunked(batchSize).forEach { chunk ->
            val drafts = chunk.map { bo ->
                UserDraft.`$`.produce { ... }
            }
            sqlClient.insertAll(drafts)
        }
    }
}
```

### Fetch Size Configuration

```kotlin
@Configuration
class JimmerConfig {

    @Bean
    fun sqlClient(dataSource: DataSource): KSqlClient {
        return KSqlClient {
            setConnectionManager(SimpleConnectionManager(dataSource))
            setExecutor(object : PooledDataSourceExecutor(dataSource) {
                override fun getDefaultFetchSize(): Int = 1000
            })
            setDialect(PostgresDialect())
        }
    }
}
```

### Prepared Statement Cache

```yaml
spring:
  datasource:
    hikari:
      max-lifetime: 1800000           # 30 minutes
      leak-detection-threshold: 60000  # Detect leaks
```

## Environment-Specific Configuration

### Development

```yaml
# application-dev.yaml
jimmer:
  show-sql: true
  pretty-sql: true
  database-validation-mode: ERROR

logging:
  level:
    org.babyfish.jimmer.sql: DEBUG
```

### Production

```yaml
# application-prod.yaml
jimmer:
  show-sql: false
  database-validation-mode: WARNING

logging:
  level:
    org.babyfish.jimmer.sql: WARN
```

### Testing

```yaml
# application-test.yaml
jimmer:
  show-sql: true
  database-validation-mode: ERROR

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/foxden_test
```

## Common Issues

### Issue: Connection Timeout

```yaml
spring:
  datasource:
    hikari:
      connection-timeout: 60000  # Increase to 60s
      connection-test-query: SELECT 1
```

### Issue: Out of Memory

```yaml
jimmer:
  executor-type: SIMPLE  # Use SimpleExecutor instead of POOLED
```

### Issue: Slow Queries

```yaml
jimmer:
  show-sql: true
  pretty-sql: true
```

Enable logging and analyze the generated SQL.

## Anti-Patterns to Avoid

- ❌ Don't use `NONE` for database-validation-mode in production
- ❌ Don't forget to set correct dialect (PostgresDialect)
- ❌ Don't use very large connection pool sizes
- ❌ Don't forget to configure connection-test-query
- ❌ Don't enable show-sql in production
- ❌ Don't forget to tune max-lifetime for connections
- ❌ Don't use wrong transaction isolation level
