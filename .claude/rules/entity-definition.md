---
glob:
  - "**/entity/**/*.kt"
  - "**/domain/**/entity/*.kt"
---

# Jimmer Entity Definition Rules

When defining Jimmer entities in FoxDen, follow these conventions:

## Entity Structure

All entities MUST be Kotlin interfaces composed from reusable traits:

```kotlin
@Entity
@Table(name = "table_name")
interface SysEntity : CommDelFlag, CommId, CommInfo, CommTenant {
    // Properties
}
```

## Trait Composition Order

Use this consistent order for trait composition:
1. `CommDelFlag` - Soft delete support
2. `CommId` - Primary key
3. `CommInfo` - Audit fields
4. `CommTenant` - Multi-tenancy (if needed)

## Property Definitions

- Use `val` for all properties (immutable)
- Explicit nullable types with `?`
- Add `@OnDissociate(DissociateAction.DELETE)` for sensitive associations
- Use `@ManyToMany`, `@OneToMany`, `@ManyToOne` for associations

## Example

```kotlin
@Entity
@Table(name = "sys_user")
interface SysUser : CommDelFlag, CommId, CommInfo, CommTenant {
    val userName: String
    val nickName: String
    val email: String?
    @OnDissociate(DissociateAction.DELETE)
    val password: String?

    @ManyToMany
    @JoinTable(name = "sys_user_role",
        inverseJoinKey = "role_id",
        joinKey = "user_id")
    val roles: List<SysRole>

    @ManyToOne
    @JoinColumn(name = "dept_id")
    val dept: SysDept?
}
```

## Prohibited

- ❌ Don't use `data class` for entities
- ❌ Don't use `var` for properties
- ❌ Don't forget nullable annotations (`?`)
- ❌ Don't create circular references without lazy loading
