# 移除 MapStruct Plus 迁移计划

> **状态**: 待执行
> **创建时间**: 2025-02-08
> **优先级**: 中等
> **预计工作量**: 4-6 小时

---

## 📋 目录

- [背景分析](#背景分析)
- [当前状态](#当前状态)
- [迁移方案](#迁移方案)
- [详细步骤](#详细步骤)
- [风险评估](#风险评估)
- [回滚方案](#回滚方案)
- [后续优化](#后续优化)

---

## 背景分析

### 为什么移除 MapStruct Plus？

#### 1. **未被真正使用**

```kotlin
// 当前实现（MapstructUtils.kt）
fun <T> convert(source: Any?, clazz: Class<T>): T? {
    return BeanUtil.copyProperties(source, clazz)  // ❌ 使用反射，无编译期校验
}
```

**问题：**
- 引入了 `mapstruct-plus-spring-boot-starter` 依赖
- 实际使用的是 Hutool 的 `BeanUtil.copyProperties`（反射）
- **完全没有享受到 MapStruct 的编译期校验优势**

#### 2. **功能重叠**

| 功能 | MapStruct | Jimmer | 结论 |
|------|-----------|--------|------|
| Entity → DTO 转换 | ✅ | ✅✅ | Jimmer 更强 |
| 编译期校验 | ✅ | ✅✅ | Jimmer DTO 校验更全面 |
| 复杂嵌套查询 | ⚠️ 需手动配置 | ✅ 自动 | Jimmer 胜出 |
| 批量操作 | ✅ | ✅✅ | Jimmer 自动优化 |
| 零运行时开销 | ✅ | ✅ | 打平 |

#### 3. **Kotlin 原生方案更优**

```kotlin
// Kotlin 扩展函数：完全类型安全
fun SysUser.toVo(): SysUserVo {
    return SysUserVo().apply {
        userId = this@toVo.id  // ✅ 编译期检查
        userName = this@toVo.userName
        nickName = this@toVo.nickName ?: ""
    }
}
```

**优势：**
- ✅ 编译期完整校验
- ✅ IDE 自动补全
- ✅ 无需注解处理器
- ✅ 零运行时开销

#### 4. **减少依赖和维护成本**

- 移除 1 个依赖包（`mapstruct-plus-spring-boot-starter`）
- 删除 `MapstructUtils.kt`（约 60 行代码）
- 减少构建时间（减少 KSP/KAPT 处理）

---

## 当前状态

### 依赖情况

```gradle
// foxden-bom/build.gradle.kts
api("io.github.linpeilie:mapstruct-plus-spring-boot-starter:1.4.6")

// foxden-common-core/build.gradle.kts
api("io.github.linpeilie:mapstruct-plus-spring-boot-starter")
```

### 使用情况

```bash
# 搜索 MapStruct 注解
@Mapper → 0 个文件
@Mapping → 0 个文件

# 搜索工具类引用
MapstructUtils.convert() → 4 处
- foxden-app-admin/controller/AuthController.kt
- foxden-domain-system/service/impl/SysUserServiceImpl.kt
- .claude/migration-guide.md
- foxden-common-core/utils/MapstructUtils.kt
```

### 影响范围

| 模块 | 影响程度 | 说明 |
|------|---------|------|
| foxden-common-core | 🟡 中 | 删除 MapstructUtils.kt |
| foxden-app-admin | 🟢 低 | 1 处引用 |
| foxden-domain-system | 🟢 低 | 1 处引用 |
| 其他模块 | ⚪ 无 | 无影响 |

---

## 迁移方案

### 策略：分阶段替换

```
阶段 1：准备（不执行）         → 分析影响，制定计划 ✅
阶段 2：添加替换工具            → 创建 Kotlin 扩展函数
阶段 3：替换引用               → 逐步替换 MapstructUtils 调用
阶段 4：移除依赖               → 删除 MapStruct Plus
阶段 5：测试验证               → 单元测试 + 集成测试
阶段 6：优化清理               → 添加 Jimmer DTO（可选）
```

### 替换方案对比

| 场景 | 当前方案 | 替换为 | 优势 |
|------|---------|--------|------|
| 简单 Entity → VO | `MapstructUtils.convert()` | Kotlin 扩展函数 | 类型安全，IDE 友好 |
| 列表转换 | `MapstructUtils.convert(list)` | `List.map { it.toVo() }` | 零开销 |
| 复杂查询 | ❌ 不支持 | Jimmer DTO | 自动生成，编译期校验 |
| 保存数据 | ❌ 不支持 | Jimmer Save Command | 批量优化 |

---

## 详细步骤

### 阶段 1：准备工作（已完成 ✅）

- [x] 分析 MapStruct 使用情况
- [x] 评估影响范围
- [x] 制定迁移计划
- [x] 创建待办文档

### 阶段 2：创建替换工具

#### 2.1 创建 Kotlin 扩展函数

```kotlin
// 文件：foxden-common-core/src/main/kotlin/.../utils/ConverterExt.kt

package com.github.alphafoxz.foxden.common.core.utils

import com.github.alphafoxz.foxden.domain.system.entity.SysUser
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo

/**
 * 对象转换扩展函数
 * 替代 MapstructUtils，提供编译期类型安全
 */

// 示例：SysUser 转换
fun SysUser.toVo(): SysUserVo {
    return SysUserVo().apply {
        userId = this@toVo.id
        userName = this@toVo.userName
        nickName = this@toVo.nickName
        email = this@toVo.email
        phonenumber = this@toVo.phonenumber
        sex = this@toVo.sex
        avatar = this@toVo.avatar
        status = this@toVo.status
        deptId = this@toVo.deptId
        // ... 其他字段
    }
}

// 列表转换
fun List<SysUser>.toVoList(): List<SysUserVo> {
    return this.map { it.toVo() }
}

// 可空转换
fun SysUser?.toVoOrNull(): SysUserVo? {
    return this?.toVo()
}
```

**任务清单：**

- [ ] 创建 `ConverterExt.kt` 文件
- [ ] 为所有需要转换的 Entity 添加扩展函数
- [ ] 添加单元测试

**预计时间**: 1-2 小时

#### 2.2 创建通用转换工具（可选）

如果确实需要通用转换，可以使用以下方案之一：

```kotlin
// 方案 A：使用 kotlinx.serialization（推荐）
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

inline fun <reified T : Any> Any.convertViaJson(): T {
    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
    }
    val jsonString = json.encodeToString(this)
    return json.decodeFromString(jsonString)
}

// 方案 B：编译时安全的手动映射（推荐用于复杂场景）
inline fun <reified T : Any> Any.convert(): T {
    // 需要为每个类型提供实现
    error("请使用具体的扩展函数，如 toVo()")
}
```

### 阶段 3：替换引用

#### 3.1 替换 AuthController.kt

```kotlin
// ❌ 旧代码
import com.github.alphafoxz.foxden.common.core.utils.MapstructUtils

val vo = MapstructUtils.convert(tenant, LoginTenantVo::class.java)

// ✅ 新代码
import com.github.alphafoxz.foxden.domain.system.entity.SysTenant
import com.github.alphafoxz.foxden.domain.system.vo.LoginTenantVo

fun SysTenant.toLoginTenantVo(): LoginTenantVo {
    return LoginTenantVo().apply {
        tenantId = this@toLoginTenantVo.tenantId
        companyName = this@toLoginTenantVo.companyName
        // ... 其他字段
    }
}

val vo = tenant.toLoginTenantVo()
```

**任务清单：**

- [ ] 替换 `foxden-app-admin/controller/AuthController.kt`
- [ ] 测试认证功能

**预计时间**: 30 分钟

#### 3.2 替换 SysUserServiceImpl.kt

```kotlin
// ❌ 旧代码
val vo = MapstructUtils.convert(user, SysUserVo::class.java)

// ✅ 新代码
val vo = user.toVo()
```

**任务清单：**

- [ ] 替换 `foxden-domain-system/service/impl/SysUserServiceImpl.kt`
- [ ] 检查所有 Service 实现类
- [ ] 测试用户管理功能

**预计时间**: 30 分钟

#### 3.3 全局搜索替换

```bash
# 搜索所有引用
grep -r "MapstructUtils" foxden-app foxden-domain foxden-common

# 逐个替换
```

**任务清单：**

- [ ] 搜索所有 `MapstructUtils.convert` 调用
- [ ] 逐个替换为扩展函数调用
- [ ] 更新相关 import 语句

**预计时间**: 1 小时

### 阶段 4：移除依赖和代码

#### 4.1 移除依赖

```gradle
// foxden-bom/build.gradle.kts
dependencies {
    constraints {
        // ❌ 删除这行
        // api("io.github.linpeilie:mapstruct-plus-spring-boot-starter:${property("version.mapstructPlus")}")

        // ❌ 删除版本号（如果只用于此）
        // version.mapstructPlus=1.4.6
    }
}

// foxden-common-core/build.gradle.kts
dependencies {
    // ❌ 删除这行
    // api(project(":foxden-common:foxden-common-core"))

    // 保留 Hutool（用于其他功能）
    api("cn.hutool:hutool-core:${property("version.hutool")}")
}
```

**任务清单：**

- [ ] 从 `foxden-bom/build.gradle.kts` 移除 MapStruct Plus
- [ ] 从 `foxden-common-core/build.gradle.kts` 移除依赖
- [ ] 更新 `gradle.properties` 移除版本号（如果无其他用途）

**预计时间**: 15 分钟

#### 4.2 删除代码

```bash
# 删除工具类
rm foxden-common/foxden-common-core/src/main/kotlin/.../utils/MapstructUtils.kt
```

**任务清单：**

- [ ] 删除 `MapstructUtils.kt`
- [ ] 搜索确认无残留引用

**预计时间**: 5 分钟

### 阶段 5：测试验证

#### 5.1 单元测试

```kotlin
@Test
fun testUserToVoConversion() {
    // Arrange
    val user = sqlClient.findById(SysUser::class, 1L)!!

    // Act
    val vo = user.toVo()

    // Assert
    assertEquals(user.id, vo.userId)
    assertEquals(user.userName, vo.userName)
    // ... 其他断言
}

@Test
fun testUserListToVoList() {
    // Arrange
    val users = sqlClient.createQuery(SysUser::class) {
        select(table)
    }.execute()

    // Act
    val vos = users.toVoList()

    // Assert
    assertEquals(users.size, vos.size)
    assertEquals(users[0].id, vos[0].userId)
}
```

**任务清单：**

- [ ] 编写转换函数单元测试
- [ ] 测试列表转换
- [ ] 测试空值处理
- [ ] 测试边界情况

**预计时间**: 1-1.5 小时

#### 5.2 集成测试

```bash
# 运行完整测试套件
./gradlew test

# 手动测试关键功能
./gradlew :foxden-app:foxden-app-admin:bootRun

# 测试场景：
# 1. 用户登录
# 2. 查询用户列表
# 3. 查询用户详情
# 4. 更新用户信息
```

**任务清单：**

- [ ] 运行单元测试
- [ ] 运行集成测试
- [ ] 手动测试核心业务流程
- [ ] 性能测试（对比迁移前后）

**预计时间**: 1 小时

### 阶段 6：优化清理（可选）

#### 6.1 引入 Jimmer DTO（推荐）

对于复杂的查询场景，使用 Jimmer DTO 语言：

```dto
// src/main/dto/UserDetail.dto
export com.github.alphafoxz.foxden.domain.system.entity.SysUser
->package com.github.alphafoxz.foxden.domain.system.vo
UserDetailView {
    #allScalars
    dept {
        id
        deptName
    }
    roles {
        id
        roleName
        permissions {
            id
            permKey
        }
    }
}
```

**任务清单：**

- [ ] 创建 `src/main/dto` 目录
- [ ] 为复杂查询创建 DTO 文件
- [ ] 编译并使用生成的 DTO
- [ ] 更新相关查询代码

**预计时间**: 2-3 小时（可选）

---

## 风险评估

### 风险矩阵

| 风险 | 概率 | 影响 | 等级 | 缓解措施 |
|------|------|------|------|---------|
| 编译错误 | 低 | 中 | 🟡 | IDE 实时检查 |
| 运行时错误 | 低 | 高 | 🟡 | 完整的单元测试 |
| 性能下降 | 极低 | 中 | 🟢 | Kotlin 扩展函数内联，零开销 |
| 功能遗漏 | 低 | 中 | 🟡 | 逐模块替换，充分测试 |
| 时间超期 | 低 | 低 | 🟢 | 预留缓冲时间 |

### 详细风险分析

#### 1. 编译错误（低风险）

**场景**：字段名拼写错误、类型不匹配

**缓解措施**：
- ✅ Kotlin 编译器会在编译期检查
- ✅ IDE 实时提示错误
- ✅ 使用 IDE 自动补全避免拼写错误

```kotlin
// IDE 会立即提示错误
val vo = user.toVo()
vo.unknownField  // ❌ 编译错误：Unresolved reference
```

#### 2. 运行时错误（低风险）

**场景**：空指针异常、类型转换异常

**缓解措施**：
- ✅ Kotlin 空安全（`?`, `?:`）
- ✅ 完整的单元测试覆盖
- ✅ 边界情况测试

```kotlin
// 安全处理
fun SysUser?.toVoOrNull(): SysUserVo? {
    return this?.toVo()
}

val vo = user.toVoOrNull() ?: return error("User not found")
```

#### 3. 性能下降（极低风险）

**对比**：

| 方案 | 性能 |
|------|------|
| MapStruct | 编译期生成，零开销 |
| Kotlin 扩展函数 | 内联，零开销 |
| Jimmer DTO | 编译期生成，零开销 |
| Hutool BeanUtil | 反射，高开销 ⚠️ |

**结论**：迁移后性能不会下降，反而可能提升（去除了反射）

#### 4. 功能遗漏（低风险）

**场景**：某些字段未转换

**缓解措施**：
- ✅ 逐个替换，充分测试
- ✅ 代码审查
- ✅ 对比新旧输出

```bash
# 测试脚本：对比新旧输出
curl http://localhost:8080/system/user/list > old.json
# 迁移后
curl http://localhost:8080/system/user/list > new.json
diff old.json new.json
```

---

## 回滚方案

### 触发条件

- [ ] 测试失败率 > 5%
- [ ] 性能下降 > 10%
- [ ] 发现重大功能缺陷

### 回滚步骤

#### 方案 A：Git 回滚（推荐）

```bash
# 1. 回滚到迁移前的 commit
git revert <commit-hash>

# 2. 恢复依赖
git checkout HEAD~1 -- foxden-bom/build.gradle.kts
git checkout HEAD~1 -- foxden-common-core/build.gradle.kts

# 3. 恢复代码
git checkout HEAD~1 -- foxden-common-core/src/main/.../MapstructUtils.kt

# 4. 重新构建
./gradlew clean build

# 5. 部署
./gradlew :foxden-app:foxden-app-admin:bootRun
```

#### 方案 B：手动恢复

```bash
# 1. 恢复 MapstructUtils.kt
git show HEAD~1:foxden-common-core/.../MapstructUtils.kt > \
  foxden-common-core/src/main/.../utils/MapstructUtils.kt

# 2. 恢复依赖
# 编辑 build.gradle.kts，添加回 MapStruct Plus

# 3. 恢复调用
# 将 user.toVo() 改回 MapstructUtils.convert(user, SysUserVo::class.java)

# 4. 重新构建
./gradlew clean build
```

### 回滚验证

```bash
# 1. 运行测试
./gradlew test

# 2. 手动验证
curl http://localhost:8080/system/user/list

# 3. 检查日志
tail -f logs/foxden-app-admin.log
```

---

## 后续优化

### 短期优化（1-2 周）

#### 1. 添加更多扩展函数

```kotlin
// 为所有实体添加转换函数
fun SysRole.toVo(): SysRoleVo { ... }
fun SysMenu.toVo(): SysMenuVo { ... }
fun SysDept.toVo(): SysDeptVo { ... }
// ...
```

#### 2. 统一转换风格

```kotlin
// 定义转换基类接口
interface Convertible<E, V> {
    fun E.toVo(): V
    fun V.toEntity(): E
}

// 实现
object SysUserConverter : Convertible<SysUser, SysUserVo> {
    override fun SysUser.toVo() = SysUserVo().apply { ... }
    override fun SysUserVo.toEntity() = SysUserDraft {...}.modify()
}
```

### 中期优化（1-2 月）

#### 1. 引入 Jimmer DTO

**适用场景**：
- 复杂嵌套查询
- 需要动态返回字段
- 前后端字段不完全一致

**示例**：

```dto
// 用户列表视图（精简）
UserListView {
    id
    userName
    nickName
    dept {
        deptName
    }
}

// 用户详情视图（完整）
UserDetailView {
    #allScalars
    dept {
        #allScalars
    }
    roles {
        #allScalars
        menus {
            #allScalars
        }
    }
}
```

**优势**：
- ✅ 自动生成类型
- ✅ 编译期校验
- ✅ 支持递归
- ✅ 避免字段遗漏

#### 2. 性能监控

```kotlin
// 添加转换性能监控
import org.springframework.stereotype.Component
import io.micrometer.core.instrument.Timer

@Aspect
@Component
class ConversionMonitor {
    @Around("execution(* com.github.alphafoxz.foxden..*.toVo(..))")
    fun monitorConversion(joinPoint: ProceedingJoinPoint): Any? {
        val timer = Timer.start()
        try {
            return joinPoint.proceed()
        } finally {
            timer.stop()
            // 记录转换耗时
        }
    }
}
```

### 长期优化（3-6 月）

#### 1. 代码生成工具

为重复性的转换代码生成工具：

```kotlin
// 读取实体定义，自动生成扩展函数
// generateConverters.kt
fun generateConverters() {
    val entities = listOf(SysUser::class, SysRole::class, ...)
    entities.forEach { entity ->
        val voClass = findVoClass(entity)
        generateExtensionFunction(entity, voClass)
    }
}
```

#### 2. 类型安全的数据库查询 DSL

结合 Jimmer 和 Kotlin，构建更强大的查询 DSL：

```kotlin
// 示例
val users = sqlClient.query {
    select(SysUser::class) {
        +SysUser::userName
        +SysUser::nickName
        +SysUser::dept {
            +SysDept::deptName
        }
    }
    where {
        SysUser::status eq "0"
        SysUser::dept::deptName like "技术%"
    }
}.fetch()
```

---

## 附录

### A. 相关文档

- [Jimmer 使用指南](.claude/JIMMER_GUIDE.md)
- [项目架构文档](CLAUDE.md)
- [MapStruct 官方文档](https://mapstruct.org/)
- [Jimmer DTO 文档](https://babyfish-ct.github.io/jimmer-doc/zh/docs/dto/overview/)

### B. 工作清单汇总

**阶段 2：创建替换工具（1-2 小时）**
- [ ] 创建 `ConverterExt.kt` 文件
- [ ] 为所有 Entity 添加扩展函数
- [ ] 添加单元测试

**阶段 3：替换引用（2 小时）**
- [ ] 替换 `AuthController.kt`
- [ ] 替换 `SysUserServiceImpl.kt`
- [ ] 全局搜索替换

**阶段 4：移除依赖（20 分钟）**
- [ ] 移除 BOM 依赖
- [ ] 移除模块依赖
- [ ] 删除 MapstructUtils.kt

**阶段 5：测试验证（2-2.5 小时）**
- [ ] 编写单元测试
- [ ] 运行集成测试
- [ ] 手动测试
- [ ] 性能对比

**阶段 6：优化清理（2-3 小时，可选）**
- [ ] 创建 Jimmer DTO 文件
- [ ] 更新查询代码

**总计：4-6 小时（不含可选阶段）**

### C. 联系人

| 角色 | 姓名 | 职责 |
|------|------|------|
| 架构师 | - | 技术方案审核 |
| 开发负责人 | - | 迁移执行 |
| 测试负责人 | - | 测试验证 |

### D. 变更记录

| 日期 | 版本 | 变更内容 | 作者 |
|------|------|---------|------|
| 2025-02-08 | 1.0 | 初始版本 | Claude Code |

---

**最后更新**: 2025-02-08
**文档状态**: ✅ 待审核
