# Jimmer Save and Mutation Guide for FoxDen

## Overview

This guide covers Jimmer's save operations for creating, updating, and deleting entities in FoxDen.

## Save Command Basics

Jimmer's save command is a powerful feature that:
- Uses native upsert capabilities
- Handles batch operations
- Translates constraint violations
- Saves incomplete objects
- Supports both entities and DTOs

## Insert Operations

### Simple Insert with Draft API

```kotlin
override fun insertUser(bo: SysUserBo): Int {
    val newUser = SysUserDraft.`$`.produce {
        userName = bo.userName ?: throw ServiceException("用户名不能为空")
        nickName = bo.nickName
        email = bo.email
        phonenumber = bo.phonenumber
        sex = bo.sex
        avatar = bo.avatar
        password = bo.password?.let { SecurityUtils.encryptPassword(it) }
        status = bo.status ?: SystemConstants.NORMAL
        delFlag = "0"
        createTime = LocalDateTime.now()
    }

    val result = sqlClient.save(newUser)
    return if (result.isModified) 1 else 0
}
```

### Insert with Associations

```kotlin
override fun insertUser(bo: SysUserBo): Long {
    val newUser = SysUserDraft.`$`.produce {
        userName = bo.userName
        nickName = bo.nickName

        // ManyToMany: Add roles
        bo.roleIds?.forEach { roleId ->
            into(roles).add {
                id = roleId
            }
        }

        // ManyToMany: Add posts
        bo.postIds?.forEach { postId ->
            into(posts).add {
                id = postId
            }
        }

        // ManyToOne: Set dept
        bo.deptId?.let {
            dept = SysDeptDraft.`$`.produce { id = it }
        }

        createTime = LocalDateTime.now()
    }

    val result = sqlClient.save(newUser)
    return result.modifiedEntity.id
}
```

## Update Operations

### Update Existing Entity

```kotlin
override fun updateUser(bo: SysUserBo): Int {
    val userIdVal = bo.userId ?: return 0

    // Fetch existing
    val existing = sqlClient.findById(SysUser::class, userIdVal)
        ?: throw ServiceException("用户不存在")

    // Update with Draft API
    val updated = SysUserDraft.`$`.produce(existing) {
        bo.nickName?.let { nickName = it }
        bo.email?.let { email = it }
        bo.phonenumber?.let { phonenumber = it }
        bo.sex?.let { sex = it }
        bo.status?.let { status = it }
        bo.deptId?.let { deptId = it }
        updateTime = LocalDateTime.now()
    }

    val result = sqlClient.save(updated)
    return if (result.isModified) 1 else 0
}
```

### Update with Null Handling

```kotlin
// To explicitly set a property to null
val updated = SysUserDraft.`$`.produce(existing) {
    if (bo.email != null) {
        email = bo.email  // Set to specific value
    } else {
        email = null  // Explicitly set to null
    }
    updateTime = LocalDateTime.now()
}
```

### Update Associations

```kotlin
override fun updateUser(bo: SysUserBo): Int {
    val existing = sqlClient.findById(SysUser::class, bo.userId!!)
        ?: throw ServiceException("用户不存在")

    val updated = SysUserDraft.`$`.produce(existing) {
        nickName = bo.nickName

        // Replace roles completely
        if (bo.roleIds != null) {
            roles.clear()
            bo.roleIds.forEach { roleId ->
                into(roles).add { id = roleId }
            }
        }

        // Replace posts
        if (bo.postIds != null) {
            posts.clear()
            bo.postIds.forEach { postId ->
                into(posts).add { id = postId }
            }
        }

        updateTime = LocalDateTime.now()
    }

    val result = sqlClient.save(updated)
    return if (result.isModified) 1 else 0
}
```

## Delete Operations

### Delete by ID

```kotlin
override fun deleteUserById(userId: Long): Int {
    val result = sqlClient.deleteById(SysUser::class, userId)
    return result.totalAffectedRowCount.toInt()
}
```

### Delete by IDs

```kotlin
override fun deleteUserByIds(userIds: Array<Long>): Int {
    val result = sqlClient.deleteByIds(SysUser::class, userIds.toList())
    return result.totalAffectedRowCount.toInt()
}
```

### Soft Delete (Logical Delete)

```kotlin
// Entities with @LogicalDeleted
override fun deleteUserById(userId: Long): Int {
    val user = sqlClient.findById(SysUser::class, userId)
        ?: return 0

    val updated = SysUserDraft.`$`.produce(user) {
        delFlag = true  // Sets delFlag = true (logical delete)
    }

    val result = sqlClient.save(updated)
    return if (result.isModified) 1 else 0
}
```

## Save with Key Strategy

### OnlySaveNotNull

```kotlin
val result = sqlClient.save(newUser, SaveMode.ONLY_NOT_NULL)
```

### IgnoreSaved

```kotlin
val result = sqlClient.save(newUser, SaveMode.IGNORE_SAVED)
```

### Upsert

```kotlin
// Default behavior - upsert based on ID
val result = sqlClient.save(newUser)
```

## Transaction Management

### Single Transaction

```kotlin
@Transactional
override fun insertUser(bo: SysUserBo): Int {
    // Insert user
    val newUser = SysUserDraft.`$`.produce { ... }
    val result = sqlClient.save(newUser)

    // Assign roles
    bo.roleIds?.forEach { roleId ->
        val userRole = UserRoleDraft.`$`.produce {
            this.userId = result.modifiedEntity.id
            this.roleId = roleId
        }
        sqlClient.insert(userRole)
    }

    // All in one transaction
    return if (result.isModified) 1 else 0
}
```

### Transactional with Annotation

```kotlin
@Transactional(rollbackFor = [Exception::class])
override fun insertUserWithRoles(bo: SysUserBo): Long {
    // All operations in this method are transactional
    ...
}
```

## Batch Operations

### Batch Insert

```kotlin
fun batchInsertUsers(users: List<SysUserBo>): Int {
    val drafts = users.map { bo ->
        SysUserDraft.`$`.produce {
            userName = bo.userName
            nickName = bo.nickName
            createTime = LocalDateTime.now()
        }
    }

    val result = sqlClient.saveAll(drafts)
    return result.totalAffectedRowCount
}
```

### Batch Update

```kotlin
fun batchUpdateStatus(userIds: List<Long>, status: String): Int {
    val updated = userIds.map { userId ->
        val existing = sqlClient.findById(SysUser::class, userId) ?: return@map null
        SysUserDraft.`$`.produce(existing) {
            this.status = status
            updateTime = LocalDateTime.now()
        }
    }.filterNotNull()

    val result = sqlClient.saveAll(updated)
    return result.totalAffectedRowCount
}
```

## Complex Operations

### Insert with Nested Objects

```kotlin
override fun insertUserWithDept(bo: SysUserBo): Long {
    val newUser = SysUserDraft.`$`.produce {
        userName = bo.userName
        nickName = bo.nickName

        // Create nested dept
        into(dept).apply {
            deptName = bo.deptName ?: "默认部门"
            parentId = bo.parentDeptId
            orderNum = 0
            status = "0"
            createTime = LocalDateTime.now()
        }

        // Or reference existing dept by ID
        // dept = SysDeptDraft.`$`.produce { id = bo.deptId }

        createTime = LocalDateTime.now()
    }

    val result = sqlClient.save(newUser)
    return result.modifiedEntity.id
}
```

### Save with DTO Input

```kotlin
// Using Input DTO directly
override fun insertUser(input: UserInput): Long {
    val result = sqlClient.save(input)
    return result.modifiedEntity.id
}
```

## OnDissociate Actions

### DELETE Action

```kotlin
@Entity
interface SysUser : CommDelFlag, CommId {
    @OnDissociate(DissociateAction.DELETE)
    val password: SysUserPassword?
}

// When user is removed from association, password is auto-deleted
```

### SET_NULL Action

```kotlin
@OnDissociate(DissociateAction.SET_NULL)
val dept: SysDept?
```

### NONE Action (Default)

```kotlin
@OnDissociate(DissociateAction.NONE)
val posts: List<SysPost>
```

## Common Patterns

### Update Specific Fields Only

```kotlin
override fun updateUserStatus(userId: Long, status: String): Int {
    val existing = sqlClient.findById(SysUser::class, userId) ?: return 0
    val updated = SysUserDraft.`$`.produce(existing) {
        this.status = status
        updateTime = LocalDateTime.now()
    }
    val result = sqlClient.save(updated)
    return if (result.isModified) 1 else 0
}
```

### Insert with Return

```kotlin
override fun insertUser(bo: SysUserBo): Long {
    val newUser = SysUserDraft.`$`.produce {
        userName = bo.userName
        nickName = bo.nickName
        createTime = LocalDateTime.now()
    }

    val result = sqlClient.save(newUser)
    return result.modifiedEntity.id  // Returns generated ID
}
```

### Save or Update

```kotlin
override fun saveOrUpdateUser(bo: SysUserBo): Int {
    val draft = if (bo.userId != null) {
        // Update
        val existing = sqlClient.findById(SysUser::class, bo.userId)
            ?: return 0
        SysUserDraft.`$`.produce(existing) {
            nickName = bo.nickName
            updateTime = LocalDateTime.now()
        }
    } else {
        // Insert
        SysUserDraft.`$`.produce {
            userName = bo.userName ?: throw ServiceException("用户名必填")
            nickName = bo.nickName
            createTime = LocalDateTime.now()
        }
    }

    val result = sqlClient.save(draft)
    return if (result.isModified) 1 else 0
}
```

## Error Handling

### Handle Constraint Violations

```kotlin
try {
    val result = sqlClient.save(newUser)
    return if (result.isModified) 1 else 0
} catch (e: SaveException) {
    when {
        e.message?.contains("duplicate key") == true -> {
            throw ServiceException("用户名已存在")
        }
        else -> throw ServiceException("保存失败: ${e.message}")
    }
}
```

### Check Before Save

```kotlin
override fun insertUser(bo: SysUserBo): Int {
    // Check uniqueness
    if (!checkUserNameUnique(bo)) {
        throw ServiceException("用户名已存在")
    }

    val newUser = SysUserDraft.`$`.produce { ... }
    val result = sqlClient.save(newUser)
    return if (result.isModified) 1 else 0
}
```

## Anti-Patterns to Avoid

- ❌ Don't forget to handle null values in Draft.produce
- ❌ Don't create circular references without proper cascade
- ❌ Don't use `!!` on findById results
- ❌ Don't forget to use `into()` for adding to collections
- ❌ Don't forget `@OnDissociate` for sensitive associations
- ❌ Don't mix DTO and entity in save operations
- ❌ Don't forget transaction boundaries for multi-step operations
- ❌ Don't forget that `save()` returns a result object, not the entity
