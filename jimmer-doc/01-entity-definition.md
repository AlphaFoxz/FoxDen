# Jimmer Entity Definition Guide for FoxDen

## Overview

This guide covers Jimmer entity definition patterns for FoxDen's Kotlin + PostgreSQL + Spring Boot stack.

## Core Entity Structure

All Jimmer entities are Kotlin interfaces composed from reusable traits.

### Entity Template

```kotlin
@Entity
@Table(name = "table_name")
interface SysEntity : CommDelFlag, CommId, CommInfo, CommTenant {
    // Properties
}
```

### Trait Composition Order (Standard)

Use this consistent order for trait composition:

1. `CommDelFlag` - Soft delete support
2. `CommId` - Primary key (`id: Long`)
3. `CommInfo` - Audit fields (createDept, createBy, createTime, updateBy, updateTime, remark)
4. `CommTenant` - Multi-tenancy (`tenantId: String`)

### Example: User Entity

```kotlin
@Entity
@Table(name = "sys_user")
interface SysUser : CommDelFlag, CommId, CommInfo, CommTenant {
    @Column(name = "user_name")
    val userName: String

    @Column(name = "nick_name")
    val nickName: String

    val email: String?

    @OnDissociate(DissociateAction.DELETE)
    val password: String?

    val status: String?

    @ManyToMany
    @JoinTable(
        name = "sys_user_role",
        inverseJoinKey = "role_id",
        joinKey = "user_id"
    )
    val roles: List<SysRole>

    @ManyToOne
    @JoinColumn(name = "dept_id")
    val dept: SysDept?

    @ManyToMany
    @JoinTable(
        name = "sys_user_post",
        inverseJoinKey = "post_id",
        joinKey = "user_id"
    )
    val posts: List<SysPost>
}
```

## Property Definition Rules

### Type Annotations

- Use `val` for all properties (immutable)
- Explicit nullable types with `?`
- Use `@Column` for custom column names

```kotlin
// Required property
val userName: String

// Optional property
val email: String?

// Custom column name
@Column(name = "nick_name")
val nickName: String
```

### Association Annotations

#### ManyToOne

```kotlin
@ManyToOne
@JoinColumn(name = "dept_id")
val dept: SysDept?
```

#### OneToMany

```kotlin
@OneToMany(mappedBy = "dept")
val children: List<SysDept>
```

#### ManyToMany

```kotlin
@ManyToMany
@JoinTable(
    name = "sys_user_role",
    inverseJoinKey = "role_id",
    joinKey = "user_id"
)
val roles: List<SysRole>
```

#### OnDissociate Action

```kotlin
// Auto-delete associated password when user is deleted
@OnDissociate(DissociateAction.DELETE)
val password: SysUserPassword?
```

## Logical Deletion

### Soft Delete Pattern

Use `@LogicalDeleted` for soft delete support:

```kotlin
// In CommDelFlag trait
@LogicalDeleted("true")  // Value when record is deleted
val delFlag: Boolean
```

Jimmer automatically adds `WHERE del_flag = false` to all queries.

## PostgreSQL-Specific Configurations

### Dialect Configuration

```yaml
# application.yaml
jimmer:
  dialect: org.babyfish.jimmer.sql.dialect.PostgresDialect
```

### PostgreSQL Types

```kotlin
// JSONB support (if needed)
@Column(name = "metadata")
val metadata: Map<String, Any>?

// Array types (if needed)
@Column(name = "tags")
val tags: List<String>?
```

## Enum Mapping

```kotlin
enum class UserStatus(val code: String) {
    ACTIVE("0"),
    DISABLED("1");

    companion object {
        fun fromCode(code: String): UserStatus? =
            values().find { it.code == code }
    }
}

@Entity
interface SysUser {
    val status: UserStatus?
}
```

## Common Patterns

### IdView Pattern

```kotlin
// Define associated ID property
@IdView("dept")
val deptId: Long?
```

### Calculated Properties

```kotlin
// Derived from associations (in VO, not entity)
val deptName: String?
    get() = dept?.name
```

## Anti-Patterns to Avoid

- ❌ Don't use `data class` for entities
- ❌ Don't use `var` for properties (use `val`)
- ❌ Don't forget nullable annotations (`?`)
- ❌ Don't create circular references without lazy loading
- ❌ Don't use Java-style POJOs
- ❌ Don't forget `@LogicalDeleted` value parameter
- ❌ Don't use `!!` non-null assertions in production

## Code Generation

After defining entities, run code generation:

```bash
./gradlew kspKotlin
```

Generated code is in `build/generated/ksp/main/kotlin/` and includes:
- Draft classes for mutation
- Fetcher definitions
- Table definitions for DSL
