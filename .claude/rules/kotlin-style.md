# Kotlin Code Style Rules

These rules apply to all Kotlin code in FoxDen project.

## Null Safety

- Always use explicit nullable types (`String?`)
- Use `?.let` for null-safe operations
- Use `?:` for default values
- Avoid `!!` non-null assertions unless absolutely necessary

```kotlin
// ✅ Good
val email = user.email ?: ""
user.userName?.let { println(it) }

// ❌ Bad
val email = user.email!!  // Dangerous
```

## Property Style

- Prefer `val` over `var`
- Use default values in constructor parameters
- Use data classes for DTOs/VOs

```kotlin
// ✅ Good
data class SysUserVo(
    var userId: Long? = null,
    var userName: String? = null,
    var nickName: String? = null
)

// ❌ Bad
class SysUserVo {
    var userId: Long? = null
    var userName: String? = null
}
```

## String Templates

Use string templates instead of concatenation:

```kotlin
// ✅ Good
log.info("User ${user.userName} logged in from ${ipAddress}")

// ❌ Bad
log.info("User " + user.userName + " logged in from " + ipAddress)
```

## Extension Functions

Use extension functions for utilities:

```kotlin
// ✅ Good
fun SysUser.toVo(): SysUserVo = SysUserVo(
    userId = id,
    userName = userName
)

// ❌ Bad
object UserConverter {
    fun toVo(user: SysUser): SysUserVo { ... }
}
```

## Collection Operations

Use Kotlin's functional-style operations:

```kotlin
// ✅ Good
val activeUsers = users.filter { it.status == "0" }
val userNames = users.map { it.userName }

// ❌ Bad
val activeUsers = mutableListOf<SysUser>()
for (user in users) {
    if (user.status == "0") {
        activeUsers.add(user)
    }
}
```

## When Expressions

Use `when` as expression instead of `if-else` chains:

```kotlin
// ✅ Good
val result = when (status) {
    "0" -> "Active"
    "1" -> "Inactive"
    else -> "Unknown"
}

// ❌ Bad
val result = if (status == "0") {
    "Active"
} else if (status == "1") {
    "Inactive"
} else {
    "Unknown"
}
```

## Naming Conventions

- **Classes/Interfaces**: PascalCase (`SysUser`, `UserService`)
- **Functions/Properties**: camelCase (`selectUserById`, `userName`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_RETRY_COUNT`, `DEFAULT_TENANT_ID`)
- **Packages**: lowercase with dots (`com.github.alphafoxz.foxden.domain.system`)

## Imports

- Avoid wildcard imports
- Group imports: stdlib, third-party, project

```kotlin
// ✅ Good
import java.util.LocalDateTime
import com.github.alphafoxz.foxden.domain.system.entity.SysUser
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo

// ❌ Bad
import com.github.alphafoxz.foxden.domain.system.entity.*
```

## Prohibited

- ❌ Don't use Java-style getters/setters (use Kotlin properties)
- ❌ Don't use semicolons
- ❌ Don't use `new` keyword for object creation
- ❌ Don't use `it` parameter name when meaning unclear
