---
glob:
  - "**/controller/**/*.kt"
---

# Controller and API Rules

When implementing controllers in FoxDen, follow these conventions:

## Route Ordering

**CRITICAL**: Explicit paths must come BEFORE path variable routes to prevent conflicts:

```kotlin
@RestController
@RequestMapping("/system/user")
class SysUserController {

    // ✅ CORRECT - explicit path first
    @GetMapping("/getInfo")
    fun getInfo(): R<UserInfoVo> { ... }

    // ✅ Then path variable routes
    @GetMapping("/{userId}")
    fun getInfo(@PathVariable userId: Long?): R<SysUserInfoVo> { ... }
}
```

## Permission Checks

Use `@SaCheckPermission` for authorization:

```kotlin
@SaCheckPermission("system:user:query")
@GetMapping("/list")
fun list(bo: SysUserBo): R<TableDataInfo<SysUserVo>> {
    return R.ok(userService.selectUserList(bo))
}
```

## Request/Response Pattern

- Use BO (Business Object) for input parameters
- Use VO (View Object) for response data
- Wrap responses in `R<T>` for consistent API format

```kotlin
@PostMapping
fun add(@RequestBody bo: SysUserBo): R<Void> {
    userService.insertUser(bo)
    return R.ok()
}

@GetMapping("/{id}")
fun getInfo(@PathVariable id: Long): R<SysUserVo> {
    return R.ok(userService.selectUserById(id))
}
```

## Validation

Use `ValidatorUtils.validate()` for custom validation:

```kotlin
@PostMapping
fun add(@Valid @RequestBody bo: SysUserBo): R<Void> {
    ValidatorUtils.validate(bo)
    userService.insertUser(bo)
    return R.ok()
}
```

## Exception Handling

Use business exceptions for error handling:

```kotlin
@GetMapping("/{userId}")
fun getInfo(@PathVariable userId: Long): R<SysUserVo> {
    val user = userService.selectUserById(userId)
        ?: return R.fail("User not found")
    return R.ok(user)
}
```

## Current User Access

Use `LoginHelper` to access current user information:

```kotlin
@GetMapping("/profile")
fun getProfile(): R<SysUserVo> {
    val loginUser = LoginHelper.getLoginUser() ?: return R.fail("Not logged in")
    val user = userService.selectUserById(loginUser.userId!!)
    return R.ok(user)
}
```

## Prohibited

- ❌ Don't put path variable routes before explicit routes
- ❌ Don't forget permission checks on sensitive endpoints
- ❌ Don't return raw objects without `R.ok()` wrapper
- ❌ Don't use `!!` on `LoginHelper.getLoginUser()`
