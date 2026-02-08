# FoxDen 部署配置文档

## 统一端口配置（2025-02-08 更新）

### 🌐 服务端口
- **Spring Boot 应用**: `12003`
- **应用访问地址**: `http://localhost:12003`

### 🗄️ 数据库端口
- **PostgreSQL 数据库**: `12001`
- **连接地址**: `jdbc:postgresql://localhost:12001/postgres`
- **用户名**: `postgres`
- **密码**: `123456`

### 📦 Docker 容器
- **应用容器**: `foxden-postgres` (端口 12001 映射到内部 5432)
- **原始数据库**: `kangbao-postgres` (端口 11001，已废弃)

## 环境配置

### 开发环境 (application-dev.yaml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:12001/postgres
    username: postgres
    password: 123456

server:
  port: 12003
```

### 生产环境
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:12001/postgres
    username: postgres
    password: ${DB_PASSWORD}

server:
  port: 12003
```

## 数据库实例说明

### 测试数据库 (端口 12001)
- **容器名**: `foxden-postgres`
- **用途**: 开发和测试
- **特点**: 可进行完整的增删改查操作
- **数据**: 包含完整的测试数据副本（从端口 11001 迁移）
- **状态**: ✅ 已验证所有字段映射，可安全使用

### 原始数据库 (端口 11001) - 已废弃
- **容器名**: `kangbao-postgres`
- **状态**: 只读备份，不再用于开发

## 数据库字段映射说明

### Entity 字段映射规则

1. **主键字段映射**
   - 数据库列名: `user_id`, `role_id`, `menu_id` 等
   - Entity 属性: `id`
   - 映射方式: `@Column(name = "user_id")`

2. **布尔字段映射 (is_ 前缀)**
   - `is_frame` → `frameFlag`
   - `is_cache` → `cacheFlag`
   - `is_default` → `defaultFlag`
   - `is_https` → `httpsFlag`

3. **常规字段映射**
   - 数据库: `user_name` → Entity: `userName` (自动 camelCase 转换)

## Entity 映射修复记录 (2025-02-08)

### 已修复的 Entity (14个)
1. SysUser - 主键映射
2. SysRole - 主键映射
3. SysMenu - 主键映射 + 布尔字段映射
4. SysDept - 主键映射
5. SysDictData - 主键映射 + 布尔字段映射 + 关联对象冲突修复
6. SysDictType - 主键映射 + 关联对象反向引用修复
7. SysPost - 主键映射
8. SysConfig - 主键映射
9. SysNotice - 主键映射
10. SysOssConfig - 主键映射 + 布尔字段映射
11. SysTenantPackage - 主键映射
12. SysLogininfor - 主键映射
13. SysOperLog - 主键映射
14. SysOss - 主键映射

### 修复的问题
- ✅ 移除 `CommId` trait 继承，直接在 Entity 中定义主键
- ✅ 使用 `@Column(name = "...")` 注解映射到正确数据库列名
- ✅ 修复关联对象冲突（`insertable=false, updatable=false`）

## 验证测试结果

### CRUD 测试 ✅
- INSERT: 成功插入测试数据
- UPDATE: 成功更新用户昵称
- DELETE: 软删除功能正常
- SELECT: 查询操作正常

### 关联查询测试 ✅
- JOIN: 用户-部门关联查询正常
- GROUP BY: 部门人数统计正常
- 事务: ROLLBACK 回滚正常

### 数据统计
- 活跃用户: 3 个
- 已删除用户: 3 个
- 总表数: 60 个
- 索引数: 128 个
- 注释数: 1072 条

## 服务启动命令

### 开发模式
```bash
./gradlew :foxden-app:foxden-app-admin:bootRun
```

### 构建命令
```bash
# 完整构建
./gradlew build

# 清理构建
./gradlew clean build
```

### 停止应用
```bash
# 停止 Gradle 守护进程
./gradlew --stop

# 或强制停止 Java 进程
pkill -9 -f "bootRun"
```

## 注意事项

1. ⚠️ **不要修改端口 11001 的数据库** - 它是只读备份
2. ✅ **所有开发测试使用端口 12001** - 可以自由进行增删改查
3. ✅ **应用统一使用端口 12003** - 避免端口冲突
4. ✅ **所有文档已更新** - 移除了旧端口引用

## 数据库迁移历史

### 迁移操作 (2025-02-08)
- 源数据库: `kangbao-postgres:11001`
- 目标数据库: `foxden-postgres:12001`
- 迁移内容:
  - 完整表结构 (10954 行 SQL)
  - 所有数据 (7513 行 SQL)
  - 索引 (128 个)
  - 注释 (1072 条)
- 验证状态: ✅ 100% 一致

### 字段映射修复 (2025-02-08)
- 修复 Entity 与数据库表结构映射
- 解决主键字段名称不匹配问题
- 解决布尔字段 `is_` 前缀映射问题
- 修复关联对象列冲突问题

## 相关文档

- [迁移指南](.claude/migration-guide.md) - 详细的 Kotlin 迁移记录
- [Jimmer 使用指南](.claude/JIMMER_GUIDE.md) - Jimmer ORM 使用文档
- [项目说明](CLAUDE.md) - 项目架构和开发指南
