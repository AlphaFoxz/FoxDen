# Jimmer Kotlin DSL 学习笔记

**更新日期**: 2025-02-09

## ✅ 已验证的正确模式

### 1. 基础查询

```kotlin
// ✅ 正确：必须使用 Entity::class
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(table)
}.execute()
```

### 2. 条件查询

```kotlin
// ✅ 精确匹配
where(table.userName eq "admin")

// ✅ 模糊查询
where(table.userName like "admin%")

// ✅ IN 查询
where(table.id `in` listOf(1L, 2L, 3L))

// ✅ 不等查询
where(table.id ne userId)

// ✅ 多条件（链式调用）
where(table.status eq "0").and(table.delFlag eq false)
```

### 3. 动态条件

```kotlin
// ✅ 使用 ?.let 实现动态条件
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)

    // 动态添加条件
    user.userId?.let { where(table.id eq it) }
    user.userName?.takeIf { it.isNotBlank() }?.let {
        where(table.userName like it)
    }
    user.status?.takeIf { it.isNotBlank() }?.let {
        where(table.status eq it)
    }

    select(table)
}.execute()
```

### 4. 分页查询

```kotlin
// ✅ 正确的分页查询
val pageable = PageRequest.of(pageNum - 1, pageSize)
val users = sqlClient.createQuery(SysUser::class) {
    where(table.delFlag eq false)
    select(table)
}.execute(pageable)

// 访问结果
users.content           // 数据列表
users.totalElements     // 总记录数
```

### 5. 排序

```kotlin
// ✅ 升序
orderBy(table.id.asc())

// ✅ 降序
orderBy(table.id.desc())
```

### 6. 单条查询

```kotlin
// ✅ 根据 ID 查询
val user = sqlClient.findById(SysUser::class, userId)

// ✅ 查询单条或 null
val user = sqlClient.createQuery(SysUser::class) {
    where(table.userName eq userName)
    select(table)
}.fetchOneOrNull()

// ✅ 判断是否存在
val exists = sqlClient.createQuery(SysUser::class) {
    where(table.userName eq userName)
    select(table.id)
}.fetchOneOrNull() != null
```

### 7. 删除操作

```kotlin
// ✅ 根据ID删除
val result = sqlClient.deleteById(SysUser::class, userId)
val affectedRows = result.totalAffectedRowCount.toInt()

// ✅ 批量删除
val result = sqlClient.deleteByIds(SysUser::class, listOf(1L, 2L, 3L))
val affectedRows = result.totalAffectedRowCount.toInt()
```

## ✅ insert/update 操作

Jimmer 使用 Draft API 进行数据修改。正确用法：

### 插入新对象
```kotlin
val newUser = SysUserDraft.`$`.produce {
    userName = "admin"
    nickName = "管理员"
    status = "0"
}
sqlClient.save(newUser)
```

### 更新现有对象
```kotlin
val existing = sqlClient.findById(SysUser::class, userId)
val updated = SysUserDraft.`$`.produce(existing) {
    nickName = "新昵称"
}
sqlClient.save(updated)
```

详细用法请参考 `.claude/JIMMER_GUIDE.md` 文档。

## 🔧 KSP 配置要求

**重要**：使用 Jimmer DSL 的模块必须配置 KSP！

### build.gradle.kts 配置

```kotlin
plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")  // 必须添加
}

dependencies {
    // ... 其他依赖
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")  // 必须添加
}
```

### 需要配置的模块

- ✅ foxden-common-jimmer（已配置）
- ✅ foxden-domain-system（已添加，待编译验证）
- ⏳ 其他 domain 模块（如需要）

## ⚠️ 常见错误

### 1. 类型参数错误

```kotlin
// ❌ 错误：使用 Entity 而非 Entity::class
sqlClient.createQuery(SysUser) { ... }  // 编译错误

// ✅ 正确：使用 Entity::class
sqlClient.createQuery(SysUser::class) { ... }
```

### 2. 属性访问错误

```kotlin
// ❌ 错误：使用 nullable 语法
where(table.`id?` eq userId)

// ✅ 正确：直接使用属性名
where(table.id eq userId)
```

### 3. 分页结果处理错误

```kotlin
// ❌ 错误：解构 executePageable 返回值
val (users, total) = query.executePageable(pageable)

// ✅ 正确：使用 execute() 返回的 Page 对象
val users = query.execute(pageable)
users.content        // 数据
users.totalElements  // 总数
```

## 📚 参考资源

### 官方文档
- [Jimmer 官方文档](https://babyfish-ct.github.io/jimmer-doc/)
- [Kotlin DSL 指南](https://babyfish-ct.github.io/jimmer-doc/docs/kotlin/draft/)

### 项目资源
- `/mnt/f/idea_projects/FoxDen/.claude/migration-guide.md` - 迁移指南
- `foxden-domain/foxden-domain-system/.../SysUserServiceImpl.kt` - 实现示例

## 🎯 学习建议

1. **从简单查询开始**：先掌握 `createQuery`, `findById`, `deleteById`
2. **逐步增加复杂度**：动态条件、分页、关联查询
3. **等待 insert/update 解决方案**：当前需要深入研究或参考官方示例
4. **参考已编译通过的代码**：项目中 foxden-common-jimmer 模块的示例