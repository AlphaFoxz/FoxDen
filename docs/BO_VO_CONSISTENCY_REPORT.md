# BO/VO 一致性检查报告

生成时间: 2026-02-12
检查范围: FoxDen Kotlin项目 vs 老ruoyi系统

## 概览

| 状态 | 数量 | 说明 |
|------|------|------|
| ✅ 完全一致 | 2 | Role, Menu |
| ⚠️ 基本一致 | 4 | User, Dept, DictType, Config |
| ❌ 不一致 | 4 | Post, DictData, Notice, Tenant |

---

## 详细分析

### 1. SysUserBo / SysUserVo

**状态**: ⚠️ 基本一致

#### 缺失字段（从老系统）

| 字段名 | 类型 | 说明 | 位置 |
|--------|------|------|------|
| `userIds` | `String` | 批量操作用户ID列表 | 老系统79行 |
| `excludeUserIds` | `String` | 工作流排除用户ID | 老系统80行 |
| `loginIp` | `String` | 最后登录IP | SysUserVo |
| `loginDate` | `LocalDateTime` | 最后登录时间 | SysUserVo |

#### 需要修复的代码

```kotlin
// foxden-domain-system/src/main/kotlin/com/github/alphafoxz/foxden/domain/system/bo/SysUserBo.kt
data class SysUserBo(
    // ... 现有字段

    // 新增字段
    var userIds: String? = null,        // 批量操作
    var excludeUserIds: String? = null,  // 工作流排除
)
```

```kotlin
// foxden-domain-system/src/main/kotlin/com/github/alphafoxz/foxden/domain/system/vo/SysUserVo.kt
data class SysUserVo(
    // ... 现有字段

    // 新增字段
    var loginIp: String? = null,         // 最后登录IP
    var loginDate: LocalDateTime? = null,   // 最后登录时间
)
```

---

### 2. SysRoleBo / SysRoleVo

**状态**: ✅ 完全一致

---

### 3. SysMenuBo

**状态**: ✅ 完全一致

---

### 4. SysDeptBo

**状态**: ⚠️ 缺失字段

#### 缺失字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `belongDeptId` | `Long?` | 所属部门ID |

#### 需要修复的代码

```kotlin
// foxden-domain-system/src/main/kotlin/com/github/alphafoxz/foxden/domain/system/bo/SysDeptBo.kt
data class SysDeptBo(
    // ... 现有字段

    // 新增字段
    var belongDeptId: Long? = null,  // 所属部门ID
)
```

---

### 5. SysPostBo

**状态**: ❌ 不一致

#### 缺失字段

| 字段名 | 类型 | 说明 | 验证 |
|--------|------|------|------|
| `postCategory` | `String?` | 岗位类别编码 | - |
| `belongDeptId` | `Long?` | 所属部门ID | - |

#### 验证不一致

| 字段 | 老系统 | FoxDen | 修复建议 |
|------|---------|---------|---------|
| `deptId` | `@NotNull` | 可空 | 添加 `@NotNull(message = "部门不能为空")` |

#### 需要修复的代码

```kotlin
// foxden-domain-system/src/main/kotlin/com/github/alphafoxz/foxden/domain/system/bo/SysPostBo.kt
data class SysPostBo(
    @get:NotNull(message = "部门不能为空")
    var deptId: Long? = null,

    // 新增字段
    var postCategory: String? = null,    // 岗位类别编码
    var belongDeptId: Long? = null,      // 所属部门ID
)
```

---

### 6. SysDictTypeBo

**状态**: ❌ 缺少验证

#### 缺失验证

| 字段 | 老系统验证 | FoxDen | 修复建议 |
|------|-------------|---------|---------|
| `dictType` | `@Pattern(regexp = "^[a-z][a-z0-9_]{1,30}$")` | 无 | 添加正则验证 |

#### 需要修复的代码

```kotlin
// foxden-domain-system/src/main/kotlin/com/github/alphafoxz/foxden/domain/system/bo/SysDictTypeBo.kt
data class SysDictTypeBo(
    @get:Pattern(
        regexp = "^[a-z][a-z0-9_]{1,30}$",
        message = "字典类型必须以小写字母开头，且只能包含小写字母、数字和下划线"
    )
    var dictType: String? = null,
)
```

---

### 7. SysConfigBo

**状态**: ❌ 值格式不一致

#### 字段值格式差异

| 字段 | 老系统值 | FoxDen值 | 兼容性 |
|------|-----------|-----------|---------|
| `configType` | "Y"(系统)/"N"(用户) | "0"/"1" | ❌ 不兼容 |

#### 修复建议

**方案一**：统一为 "Y/N"（推荐，与老系统兼容）

```kotlin
// 在常量类中定义
object ConfigConstants {
    const val CONFIG_TYPE_SYSTEM = "Y"  // 系统类型
    const val CONFIG_TYPE_USER = "N"    // 用户类型
}
```

**方案二**：保持 "0/1"，在Service层转换

---

### 8. SysNoticeBo

**状态**: ❌ 缺少字段和验证

#### 缺失字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `createByName` | `String?` | 创建者名称 |

#### 缺失验证

| 字段 | 老系统验证 | FoxDen | 修复建议 |
|------|-------------|---------|---------|
| `noticeTitle` | `@Xss` | 无 | 添加 `@Xss` 验证 |

#### 需要修复的代码

```kotlin
// foxden-domain-system/src/main/kotlin/com/github/alphafoxz/foxden/domain/system/bo/SysNoticeBo.kt
data class SysNoticeBo(
    @get:Xss(message = "标题不能包含脚本字符")
    var noticeTitle: String? = null,

    // 新增字段
    var createByName: String? = null,  // 创建者名称
)
```

---

### 9. SysDictDataBo

**状态**: ❌ 缺失字段和值格式问题

#### 缺失字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `createDept` | `Long?` | 创建部门ID |

#### 字段值格式差异

| 字段 | 老系统值 | FoxDen值 | 兼容性 |
|------|-----------|-----------|---------|
| `isDefault` | "Y"(是)/"N"(否) | "0"/"1" | ❌ 不兼容 |

#### 需要修复的代码

```kotlin
// foxden-domain-system/src/main/kotlin/com/github/alphafoxz/foxden/domain/system/bo/SysDictDataBo.kt
data class SysDictDataBo(
    // 修改isDefault的注释和验证
    var isDefault: String? = null,  // Y-是 N-否

    // 新增字段
    var createDept: Long? = null,  // 创建部门
)
```

---

### 10. SysTenantBo

**状态**: ❌ 多个不一致

#### 字段名不一致

| FoxDen | 老系统 | 说明 |
|---------|---------|------|
| `userName` | `username` | 命名风格不一致 |

#### 缺失字段

| 字段名 | 类型 | 验证 | 说明 |
|--------|------|------|------|
| `id` | `Long?` | 无 | 主键ID |
| `companyName` | `String?` | `@NotBlank(groups=[AddGroup::class])` | 公司名称 |
| `username` | `String?` | `@NotBlank(groups=[AddGroup::class])` | 用户名 |

#### 类型不一致

| 字段 | 老系统 | FoxDen | 说明 |
|------|---------|---------|------|
| `accountCount` | `Long?` | `Int?` | 账号数量类型 |

#### 需要修复的代码

```kotlin
// foxden-domain-tenant/src/main/kotlin/com/github/alphafoxz/foxden/domain/tenant/bo/SysTenantBo.kt
data class SysTenantBo(
    // 修改字段名保持一致
    var username: String? = null,  // 用户名（从userName改为username）

    // 新增字段
    var id: Long? = null,  // 租户编号

    @get:NotBlank(message = "公司名称不能为空", groups = [AddGroup::class])
    var companyName: String? = null,  // 公司名称

    // 修改类型
    var accountCount: Long? = null,  // 账号数量（从Int改为Long）
)
```

---

## 修复优先级

### 🔴 高优先级（必须修复）

1. **SysTenantBo字段名** - `userName` → `username`
2. **SysTenantBo缺失字段** - `id`, `companyName`
3. **布尔值格式统一** - 决定使用 "Y/N" 还是 "0/1"
4. **SysPostBo验证** - 添加 `deptId` 的 `@NotNull`

### 🟡 中优先级（建议修复）

5. **SysDeptBo缺失字段** - `belongDeptId`
6. **SysPostBo缺失字段** - `postCategory`, `belongDeptId`
7. **SysDictDataBo缺失字段** - `createDept`
8. **SysDictTypeBo验证** - 添加字典类型正则验证
9. **SysNoticeBo验证** - 添加 `@Xss`
10. **SysNoticeBo缺失字段** - `createByName`

### 🟢 低优先级（可选）

11. **SysUserBo缺失字段** - `userIds`, `excludeUserIds`（工作流相关）
12. **SysUserVo缺失字段** - `loginIp`, `loginDate`
13. **类型一致性** - `Long` vs `Int` 统一

---

## 修复脚本

要执行所有修复，按以下顺序操作：

### 第一步：修复高优先级问题

```bash
# 1. 修复 SysTenantBo
# 文件: foxden-domain-tenant/src/main/kotlin/com/github/alphafoxz/foxden/domain/tenant/bo/SysTenantBo.kt

# 2. 修复 SysPostBo
# 文件: foxden-domain-system/src/main/kotlin/com/github/alphafoxz/foxden/domain/system/bo/SysPostBo.kt
```

### 第二步：修复中优先级问题

```bash
# 修复 SysDeptBo, SysDictDataBo, SysDictTypeBo, SysNoticeBo
```

---

## 验证清单

完成修复后，使用以下清单验证：

- [ ] 所有BO文件与老系统字段一致（排除必要的Kotlin优化）
- [ ] 所有VO文件与老系统字段一致
- [ ] 布尔值格式统一（全部使用 "Y/N" 或 "0/1"）
- [ ] 验证注解完整（`@NotNull`, `@NotBlank`, `@Pattern`, `@Xss`）
- [ ] 字段命名规范（CamelCase，与老系统保持一致）
- [ ] 数据类型兼容（`LocalDateTime` 代替 `Date`，`List` 代替数组）

---

## 附录：参考文件路径

### 当前系统
- BO: `foxden-domain/foxden-domain-system/src/main/kotlin/com/github/alphafoxz/foxden/domain/system/bo/`
- VO: `foxden-domain/foxden-domain-system/src/main/kotlin/com/github/alphafoxz/foxden/domain/system/vo/`

### 老系统
- POJO: `old-version/ruoyi-modules/ruoyi-system/src/main/java/com/ruoyi/web/controller/pojo/`

---

**注意**：某些差异可能是Kotlin语言特性的合理优化，例如：
- `Long[]` → `List<Long>` （Kotlin习惯用法）
- `Date` → `LocalDateTime` （Java 8+ 日期API）
- `@Xss` 自定义实现可能不同

以上差异不需要修复，除非影响业务逻辑。
