# FoxDen System Application

FoxDen 系统管理应用，提供完整的后台管理系统API接口。

## 技术栈

- Kotlin 2.3.0
- Spring Boot 3.5.10
- Jimmer ORM
- Sa-Token (认证授权)

## 模块功能

### 系统管理

#### 1. 用户管理 (SysUserController)
- 用户列表查询（分页）
- 用户新增/修改/删除
- 用户导入/导出
- 重置密码
- 状态修改
- 角色授权
- 部门用户查询

#### 2. 角色管理 (SysRoleController)
- 角色列表查询（分页）
- 角色新增/修改/删除
- 角色权限配置
- 数据权限配置
- 状态修改
- 角色选择框

#### 3. 菜单管理 (SysMenuController)
- 菜单列表查询
- 菜单新增/修改/删除
- 路由信息获取
- 菜单树形结构
- 角色菜单关联

#### 4. 部门管理 (SysDeptController)
- 部门列表查询
- 部门新增/修改/删除
- 部门树形结构
- 数据权限验证

#### 5. 岗位管理 (SysPostController)
- 岗位列表查询
- 岗位新增/修改/删除

#### 6. 字典管理 (SysDictTypeController & SysDictDataController)
- 字典类型管理
- 字典数据管理
- 字典缓存刷新
- 字典导出

#### 7. 参数配置 (SysConfigController)
- 参数配置列表
- 参数新增/修改/删除
- 参数缓存刷新
- 根据键名查询参数值

#### 8. 通知公告 (SysNoticeController)
- 通知公告列表
- 通知新增/修改/删除

#### 9. 租户管理 (SysTenantController)
- 租户列表查询（分页）
- 租户新增/修改/删除
- 租户导出
- 租户状态修改

### 监控管理

#### 10. 操作日志 (SysOperLogController)
- 操作日志列表（分页）
- 操作日志导出
- 操作日志删除
- 清空操作日志

#### 11. 登录日志 (SysLogininforController)
- 登录日志列表（分页）
- 登录日志导出
- 登录日志删除
- 清空登录日志

#### 12. 在线用户 (SysUserOnlineController)
- 在线用户列表
- 强制用户退出

#### 13. 缓存监控 (CacheController)
- 缓存监控信息
- 缓存名称列表
- 缓存键名列表
- 缓存内容查询
- 清理缓存

## API接口清单

### 系统管理接口

| 接口 | 方法 | 路径 | 说明 |
|-----|------|------|------|
| 用户列表 | GET | /system/user/list | 获取用户列表（分页） |
| 用户详情 | GET | /system/user/{userId} | 获取用户详情 |
| 新增用户 | POST | /system/user | 新增用户 |
| 修改用户 | PUT | /system/user | 修改用户 |
| 删除用户 | DELETE | /system/user/{userIds} | 删除用户 |
| 重置密码 | PUT | /system/user/resetPwd | 重置用户密码 |
| 状态修改 | PUT | /system/user/changeStatus | 修改用户状态 |
| 导出用户 | POST | /system/user/export | 导出用户数据 |
| 部门用户 | GET | /system/user/dept/{deptId} | 根据部门查询用户 |

| 接口 | 方法 | 路径 | 说明 |
|-----|------|------|------|
| 角色列表 | GET | /system/role/list | 获取角色列表（分页） |
| 角色详情 | GET | /system/role/{roleId} | 获取角色详情 |
| 新增角色 | POST | /system/role | 新增角色 |
| 修改角色 | PUT | /system/role | 修改角色 |
| 删除角色 | DELETE | /system/role/{roleIds} | 删除角色 |
| 数据权限 | PUT | /system/role/dataScope | 修改数据权限 |
| 状态修改 | PUT | /system/role/changeStatus | 修改角色状态 |
| 导出角色 | POST | /system/role/export | 导出角色数据 |
| 角色选择 | GET | /system/role/optionselect | 获取角色选择框列表 |

| 接口 | 方法 | 路径 | 说明 |
|-----|------|------|------|
| 菜单列表 | GET | /system/menu/list | 获取菜单列表 |
| 菜单详情 | GET | /system/menu/{menuId} | 获取菜单详情 |
| 新增菜单 | POST | /system/menu | 新增菜单 |
| 修改菜单 | PUT | /system/menu | 修改菜单 |
| 删除菜单 | DELETE | /system/menu/{menuId} | 删除菜单 |
| 路由信息 | GET | /system/menu/getRouters | 获取路由信息 |
| 菜单树 | GET | /system/menu/treeselect | 获取菜单树 |
| 角色菜单树 | GET | /system/menu/roleMenuTreeselect/{roleId} | 获取角色菜单树 |

### 监控管理接口

| 接口 | 方法 | 路径 | 说明 |
|-----|------|------|------|
| 操作日志 | GET | /monitor/operlog/list | 获取操作日志列表 |
| 导出操作日志 | POST | /monitor/operlog/export | 导出操作日志 |
| 删除操作日志 | DELETE | /monitor/operlog/{operIds} | 删除操作日志 |
| 清空操作日志 | DELETE | /monitor/operlog/clean | 清空操作日志 |
| 登录日志 | GET | /monitor/logininfor/list | 获取登录日志列表 |
| 导出登录日志 | POST | /monitor/logininfor/export | 导出登录日志 |
| 删除登录日志 | DELETE | /monitor/logininfor/{infoIds} | 删除登录日志 |
| 清空登录日志 | DELETE | /monitor/logininfor/clean | 清空登录日志 |
| 在线用户 | GET | /monitor/online/list | 获取在线用户列表 |
| 强制退出 | DELETE | /monitor/online/{tokenId} | 强制用户退出 |
| 缓存监控 | GET | /monitor/cache | 获取缓存监控信息 |
| 缓存名称 | GET | /monitor/cache/getNames | 获取缓存名称列表 |
| 缓存键名 | GET | /monitor/cache/getKeys/{cacheName} | 获取缓存键名列表 |
| 缓存内容 | GET | /monitor/cache/getValue/{cacheName}/{cacheKey} | 获取缓存内容 |
| 清理缓存名称 | DELETE | /monitor/cache/clearCacheName/{cacheName} | 清理指定缓存名称 |
| 清理缓存键名 | DELETE | /monitor/cache/clearCacheKey/{cacheKey} | 清理指定缓存键 |
| 清理全部缓存 | DELETE | /monitor/cache/clearCacheAll | 清理全部缓存 |

## 权限说明

所有接口均使用 Sa-Token 进行权限控制，需要具有相应权限才能访问。

权限格式：`模块:功能:操作`
- `system:user:list` - 用户列表查询权限
- `system:role:add` - 角色新增权限
- `monitor:operlog:export` - 操作日志导出权限
- ...

## 运行方式

### 开发环境
```bash
./gradlew :foxden-app:foxden-app-system:build
```

### 生产环境
```bash
./gradlew :foxden-app:foxden-app-system:build
```

## 依赖关系

本模块依赖以下模块：
- foxden-common-* (所有公共模块)
- foxden-domain-system (系统域)
- foxden-domain-tenant (租户域)

## 开发说明

### 添加新的Controller

1. 在 `controller` 包下创建新的Controller类
2. 继承 `BaseController` 以获得通用方法支持
3. 使用 `@RestController` 和 `@RequestMapping` 注解
4. 使用 Sa-Token 注解进行权限控制：
   - `@SaCheckPermission` - 权限检查
   - `@SaCheckRole` - 角色检查
5. 使用 `@Log` 注解记录操作日志
6. 使用 `@RepeatSubmit` 注解防止重复提交

### 返回值规范

- 使用 `R<T>` 作为统一返回值
- 列表查询返回 `TableDataInfo<T>`
- 导出接口直接写入HttpServletResponse
- 使用 `toAjax()` 方法将操作结果转换为R对象

## 注意事项

1. 本模块已完成从Java到Kotlin的迁移
2. 所有接口均遵循RESTful规范
3. 需要配合 foxden-app-admin 模块使用
4. 生产环境请配置PostgreSQL数据库

## 迁移说明

- ✅ 完成从Java到Kotlin的迁移
- ✅ 使用Kotlin惯用写法
- ✅ 保留原有功能特性
- ✅ 遵循Spring Boot最佳实践

## 许可证

Copyright © 2025 FoxDen Team
