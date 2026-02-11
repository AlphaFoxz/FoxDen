# 租户功能使用指南

## 概述

FoxDen 项目已完成多租户功能的迁移，包括核心业务逻辑、Redis 缓存隔离和租户过滤器。

## 已完成功能

### 1. TenantHelper 核心功能 ✅

**位置**: `foxden-common-jimmer/src/main/kotlin/com/github/alphafoxz/foxden/common/jimmer/helper/TenantHelper.kt`

**功能**:
- `getTenantId()` - 获取当前租户ID（优先使用动态租户）
- `dynamic(tenantId) { }` - 动态切换租户执行代码块
- `dynamicTenant(tenantId, runnable)` - 动态切换租户（Java 兼容）
- `ignore { }` - 忽略租户过滤（跨租户操作）
- `isEnable()` - 租户功能开关
- `isSuperAdmin()` - 超级管理员判断
- `isSystemAdmin()` - 系统管理员判断
- `clearDynamic()` - 清除动态租户上下文
- `isDefaultTenant(tenantId)` - 判断是否为默认租户

### 2. SysTenantServiceImpl 完整实现 ✅

**位置**: `foxden-domain-system/src/main/kotlin/com/github/alphafoxz/foxden/domain/system/service/impl/SysTenantServiceImpl.kt`

**核心方法**:
- `selectTenantList()` - 查询租户列表（支持多条件动态过滤）
- `queryByTenantId()` - 根据租户ID查询
- `checkTenantAllowed()` - 校验租户操作权限（禁止操作默认租户）
- `checkTenantNameUnique()` - 校验企业名称唯一性
- `checkTenantUserNameUnique()` - 用户名校验（委托给用户服务）
- `updateTenant()` - 更新租户信息
- `updateTenantStatus()` - 更新租户状态
- `deleteTenantById()` - 软删除租户
- `syncTenantPackage()` - 同步租户套餐菜单权限

**insertTenant 完整流程**:
1. 生成唯一租户ID（6位随机数，避免重复）
2. 创建租户记录
3. 根据套餐创建租户角色
4. 创建部门（企业名称作为部门名）
5. 创建角色-部门关联
6. 创建管理员用户（密码加密）
7. 更新部门负责人
8. 创建用户-角色关联
9. 从默认租户同步字典数据
10. 从默认租户同步配置数据

**syncTenantDict** - 同步所有租户的字典:
- 遍历所有正常状态的租户
- 从默认租户复制字典类型和数据
- 排除已自定义的字典项

**syncTenantConfig** - 同步所有租户的配置:
- 遍历所有正常状态的租户
- 从默认租户复制系统配置
- 排除已存在的配置项

### 3. Redis 缓存租户隔离 ✅

**TenantKeyPrefixHandler** 位置: `foxden-common-tenant/src/main/kotlin/com/github/alphafoxz/foxden/common/tenant/handler/TenantKeyPrefixHandler.kt`

**功能**:
- 自动为 Redis Key 添加租户前缀: `tenantId:keyPrefix:name`
- 支持全局缓存 key（不加租户前缀）
- 提供前缀添加和移除功能

**TenantConfig** 位置: `foxden-common-tenant/src/main/kotlin/com/github/alphafoxz/foxden/common/tenant/config/TenantConfig.kt`

**配置**:
- 自动配置 Redisson 使用租户前缀处理器
- 支持单机和集群模式
- 通过 `tenant.enable=true` 配置启用

### 4. Jimmer 租户过滤器 ✅

**TenantFilter** 位置: `foxden-common-jimmer/src/main/kotlin/com/github/alphafoxz/foxden/common/jimmer/filter/TenantFilter.kt`

**功能**:
- `needTenantFilter()` - 判断实体是否需要租户过滤
- `tenantCondition(table)` - 为查询添加租户条件
- `tenantConditionNullable(table)` - 添加可空的租户条件
- `tenantCondition(table, ignore)` - 带忽略标志的租户条件
- `getTableName()` - 获取实体对应的表名

**排除表** (不需要租户过滤):
- sys_tenant
- sys_tenant_package
- gen_table
- gen_table_column
- sys_config
- sys_dict_type
- sys_dict_data
- sys_menu
- sys_role
- sys_dept

## 使用示例

### 1. 基础租户查询

```kotlin
// 服务层实现
override fun selectTenantList(bo: SysTenantBo): List<SysTenantVo> {
    val tenants = sqlClient.createQuery(SysTenant::class) {
        // 自动使用当前租户
        where(table.delFlag eq false)
        bo.companyName?.let { where(table.companyName like "%${it}%") }
        select(table)
    }.execute()

    return tenants.map { entityToVo(it) }
}
```

### 2. 动态切换租户

```kotlin
// 方式 1: Kotlin 函数
val result = TenantHelper.dynamic("000001") {
    // 在这个租户上下文中执行代码
    userService.selectUserById(userId)
}

// 方式 2: Java 兼容
TenantHelper.dynamicTenant("000001", Runnable {
    userService.selectUserById(userId)
})
```

### 3. 忽略租户过滤

```kotlin
// 跨租户查询数据
val allUsers = TenantHelper.ignore {
    sqlClient.createQuery(SysUser::class) {
        where(table.delFlag eq false)
        select(table)
    }.execute()
}
```

### 4. 使用租户过滤器

```kotlin
// 显式添加租户条件
override fun selectUserList(bo: SysUserBo): List<SysUserVo> {
    val users = sqlClient.createQuery(SysUser::class) {
        where(table.delFlag eq false)
        // 方式 1: 使用 TenantFilter（推荐）
        TenantFilter.tenantCondition(table)?.let { where(it) }

        // 方式 2: 手动添加（等效）
        // where(table.tenantId eq TenantHelper.getTenantId())

        bo.userName?.let { where(table.userName like "%${it}%") }
        select(table)
    }.execute()

    return users.map { entityToVo(it) }
}
```

### 5. 判断是否需要租户过滤

```kotlin
if (TenantFilter.needTenantFilter(SysUser::class)) {
    // 该实体需要租户过滤
    where(table.tenantId eq TenantHelper.getTenantId())
} else {
    // 该实体不需要租户过滤（如租户表本身）
}
```

### 6. 创建新租户

```kotlin
val bo = SysTenantBo(
    contactUserName = "张三",
    contactPhone = "13800138000",
    companyName = "示例公司",
    userName = "admin",
    password = "123456",
    packageId = 1L,
    accountCount = 100
)

// insertTenant 会自动完成以下流程：
// 1. 生成租户ID
// 2. 创建租户记录
// 3. 创建租户角色
// 4. 创建部门
// 5. 创建管理员用户
// 6. 同步字典和配置
val result = tenantService.insertTenant(bo)
```

## 配置说明

### 启用租户功能

在 `application.yaml` 中添加：

```yaml
tenant:
  enable: true
```

### Redis Key 格式

启用租户后，Redis key 格式为：
- 普通缓存: `{tenantId}:{keyPrefix}:{key}`
- 全局缓存: `{global}:{key}` (不加租户前缀)

例如：
- `000000:sys_config:user:1`
- `global:captcha_codes:123456`

## 注意事项

1. **默认租户 ID**: `000000`
2. **超级管理员角色**: `superadmin`
3. **租户管理员角色**: `admin`
4. **软删除标志**: `delFlag = true`
5. **租户功能开关**: `tenant.enable`

## 测试建议

1. 创建测试租户
2. 测试租户数据隔离（不同租户的数据互不可见）
3. 测试动态租户切换
4. 测试租户套餐同步
5. 测试 Redis 缓存隔离
6. 测试跨租户查询（使用 ignore）

## 与老项目对比

| 功能 | 老项目 | 新项目 | 状态 |
|-----|---------|---------|------|
| TenantHelper | ✅ | ✅ | 完成 |
| SysTenantServiceImpl | ✅ | ✅ | 完成 |
| PlusTenantLineHandler | ✅ | TenantFilter | 完成 |
| TenantKeyPrefixHandler | ✅ | ✅ | 完成 |
| syncTenantDict | ✅ | ✅ | 完成 |
| syncTenantConfig | ✅ | ✅ | 完成 |
| 工作流同步 | ✅ | ⚠️ | 可选 |

**注**: 工作流同步功能需要根据实际需求决定是否实现。
