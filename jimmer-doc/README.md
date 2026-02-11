# Jimmer Documentation for FoxDen

This directory contains extracted and curated Jimmer ORM documentation specifically for the FoxDen project (Kotlin + PostgreSQL + Spring Boot).

## Purpose

These files are designed to be used as reference prompts when working with Jimmer in the FoxDen codebase. They contain practical examples and patterns specific to our technology stack.

## Documentation Files

### 1. [01-entity-definition.md](01-entity-definition.md)
**Entity Definition Guide**

- Entity structure and trait composition
- Property definitions and type annotations
- Association mapping (OneToOne, ManyToOne, ManyToMany, OneToMany)
- Logical deletion patterns
- PostgreSQL-specific configurations
- Enum mapping
- Common anti-patterns

**Use when**: Creating new entities, modifying entity relationships, or defining database mappings.

### 2. [02-query-dsl.md](02-query-dsl.md)
**Query DSL Guide**

- Basic query patterns
- Where conditions (comparison, like, null checks, in list)
- Logical operators (AND, OR, NOT)
- Dynamic query conditions
- Ordering and pagination
- Join queries
- Object fetcher for partial loading
- Count and existence queries
- PostgreSQL-specific features (ilike)
- Data permission bypass

**Use when**: Writing service queries, implementing search functionality, or fetching data from the database.

### 3. [03-save-mutation.md](03-save-mutation.md)
**Save and Mutation Guide**

- Insert operations with Draft API
- Update operations with null handling
- Update associations
- Delete operations (soft and hard delete)
- Save strategies (ONLY_NOT_NULL, IGNORE_SAVED)
- Transaction management
- Batch operations
- Complex operations with nested objects
- OnDissociate actions
- Error handling

**Use when**: Creating, updating, or deleting entities, handling associations, or implementing transactional operations.

### 4. [04-spring-integration.md](04-spring-integration.md)
**Spring Integration Guide**

- Gradle dependencies and KSP configuration
- Application configuration
- Bean configuration (auto and manual)
- Service layer integration
- Transaction management (@Transactional)
- Exception handling
- Cache integration (Redis)
- Multi-tenancy integration
- Data permissions with GlobalFilter
- Testing integration

**Use when**: Configuring Spring Boot with Jimmer, setting up transactions, implementing caching, or integrating multi-tenancy.

### 5. [05-configuration.md](05-configuration.md)
**Configuration Guide**

- Application YAML configuration
- PostgreSQL dialect setup
- DataSource configuration (primary and read replicas)
- Type mapping (PostgreSQL to Kotlin)
- Validation configuration
- SQL logging for development
- Connection pool tuning (HikariCP)
- Transaction isolation levels
- Cache configuration (local and Redis)
- Environment-specific configurations

**Use when**: Setting up the application configuration, tuning performance, or configuring database connections.

### 6. [06-best-practices.md](06-best-practices.md)
**Best Practices Guide**

- Entity design patterns
- Query optimization patterns
- Mutation best practices
- Service layer guidelines
- Performance tips
- Multi-tenancy patterns
- Testing strategies
- Common anti-patterns to avoid
- Comprehensive checklist

**Use when**: Reviewing code, optimizing performance, or ensuring consistency across the codebase.

## Quick Reference

### Common Imports

```kotlin
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.ast.mutation.*
import org.babyfish.jimmer.sql.fetcher.Fetcher
import com.github.alphafoxz.foxden.domain.system.entity.*
```

### Entity Template

```kotlin
@Entity
@Table(name = "table_name")
interface SysEntity : CommDelFlag, CommId, CommInfo, CommTenant {
    val fieldName: String
    val optionalField: String?
}
```

### Service Template

```kotlin
@Service
class SysEntityServiceImpl(
    private val sqlClient: KSqlClient
) : SysEntityService {

    override fun selectById(id: Long): SysEntityVo? {
        val entity = sqlClient.findById(SysEntity::class, id) ?: return null
        return entityToVo(entity)
    }
}
```

### Query Template

```kotlin
val results = sqlClient.createQuery(SysEntity::class) {
    where(table.delFlag eq false)
    bo.field?.takeIf { it.isNotBlank() }?.let {
        where(table.field ilike "%${it}%")
    }
    orderBy(table.id.asc())
    select(table)
}.execute()
```

### Save Template

```kotlin
val draft = SysEntityDraft.`$`.produce {
    field = bo.field ?: throw ServiceException("Field required")
    createTime = LocalDateTime.now()
}

val result = sqlClient.save(draft)
return if (result.isModified) 1 else 0
```

## Technology Stack

- **Language**: Kotlin 2.3.0
- **Framework**: Spring Boot 3.5.10
- **ORM**: Jimmer 0.10.6
- **Build**: Gradle (Kotlin DSL) with KSP
- **Database**: PostgreSQL
- **Cache**: Redis (Redisson)
- **Security**: Sa-Token

## Related Documentation

- [Project CLAUDE.md](../.claude/CLAUDE.md) - Overall project documentation
- [Entity Definition Rules](../.claude/rules/entity-definition.md) - FoxDen-specific entity rules
- [Kotlin Style Rules](../.claude/rules/kotlin-style.md) - Kotlin coding standards
- [Service Layer Rules](../.claude/rules/service-layer.md) - Service implementation patterns

## Contributing

When adding new patterns or examples:

1. Follow the existing markdown structure
2. Use code fences with kotlin language identifier
3. Include both ✅ Good and ❌ Bad examples
4. Focus on practical, FoxDen-specific usage
5. Cross-reference related files where appropriate

## Version

This documentation is based on:
- Jimmer: 0.10.6
- PostgreSQL: Latest stable release
- Spring Boot: 3.5.10
- Kotlin: 2.3.0
