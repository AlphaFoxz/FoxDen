<div align="center">

![FoxDen Logo](docs/logo.png)

# FoxDen

基于 Spring Boot、Kotlin 和 Jimmer ORM 构建的现代化多租户 SaaS 系统

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-green.svg?logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Jimmer](https://img.shields.io/badge/Jimmer-0.10.6-orange.svg)](https://babyfish-ct.github.io/jimmer-doc/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

[English](README.md) | [简体中文](README.zh-CN.md)

</div>

---

## 📖 项目简介

FoxDen 是一个功能完备的多租户 SaaS 平台，采用现代 JVM 技术栈和最佳实践构建，致力于提供企业级的高可用、可扩展、易维护的系统解决方案。

### ✨ 核心特性

- 🏢 **多租户架构**：内置租户隔离与管理机制
- 🔐 **高级安全**：基于 Sa-Token 的多种认证策略（密码、短信、邮箱、OAuth、微信）
- 🎯 **角色权限控制**：细粒度权限管理 + 数据权限过滤
- 🗄️ **现代 ORM**：Jimmer ORM，编译期校验，自动解决 N+1 查询问题
- 📊 **数据权限**：基于用户角色和部门的自动数据过滤
- 🚀 **高性能**：Redisson 分布式缓存、分布式锁
- 📝 **完整日志**：操作日志、登录日志、审计追踪
- 🌐 **社交登录**：集成 JustAuth，支持 20+ 社交平台
- 📄 **Excel 导入导出**：EasyExcel 集成，便捷数据处理
- 📦 **对象存储**：支持 MinIO、阿里云 OSS、腾讯云 COS

---

## 🛠️ 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| **开发语言** | Kotlin | 2.3.0 |
| **框架** | Spring Boot | 3.4.1 |
| **ORM** | Jimmer | 0.10.6 |
| **数据库** | PostgreSQL / H2 | - |
| **缓存** | Redis (Redisson) | 3.35.0 |
| **安全** | Sa-Token | 1.44.0 |
| **API 文档** | SpringDoc OpenAPI | 2.8.2 |
| **Excel** | EasyExcel | 4.0.3 |
| **构建工具** | Gradle (Kotlin DSL) | - |
| **JDK** | Java | 21 |

---

## 🚀 快速开始

### 前置要求

- JDK 21+
- Gradle 8.x
- PostgreSQL（生产环境）或 H2（开发环境）
- Redis（可选，用于缓存）

### 克隆与构建

```bash
git clone https://github.com/alphafoxz/foxden.git
cd foxden

# 构建项目
./gradlew build

# 或运行测试
./gradlew test
```

### 运行应用

```bash
# 启动管理端应用（端口 12003）
./gradlew :foxden-app:foxden-app-admin:bootRun
```

### 访问地址

- **应用地址**: http://localhost:12003
- **API 文档**: http://localhost:12003/swagger-ui.html
- **H2 控制台**（仅开发环境）: http://localhost:12003/h2-console
- **健康检查**: http://localhost:12003/actuator/health

### 默认配置

```yaml
# 数据库（PostgreSQL）
spring:
  datasource:
    url: jdbc:postgresql://localhost:12001/postgres
    username: postgres
    password: 123456

# 服务器
server:
  port: 12003
```

---

## 📁 项目结构

```
foxden/
├── foxden-bom/                      # 依赖管理
├── foxden-common/                   # 通用模块
│   ├── foxden-common-core/         # 核心工具
│   ├── foxden-common-jimmer/       # Jimmer ORM 工具
│   ├── foxden-common-web/          # Web 通用（验证码、XSS）
│   ├── foxden-common-security/     # Sa-Token 集成
│   ├── foxden-common-redis/        # Redis 缓存
│   ├── foxden-common-encrypt/      # API 加密
│   └── ... (其他通用模块)
├── foxden-domain/                   # 领域层
│   ├── foxden-domain-system/       # 系统域
│   ├── foxden-domain-tenant/       # 租户域
│   └── foxden-domain-infrastructure/
└── foxden-app/                      # 应用层
    ├── foxden-app-admin/           # 管理端应用
    └── foxden-app-system/          # 系统控制器
```

---

## 🎯 核心功能

### 系统管理

- **用户管理**：用户增删改查、密码重置、角色分配
- **角色管理**：角色增删改查、权限配置、数据权限
- **菜单管理**：动态菜单树、路由配置
- **部门管理**：部门树形结构、数据权限
- **字典管理**：系统字典、缓存刷新
- **参数配置**：系统参数、动态配置
- **通知公告**：公告发布
- **日志管理**：操作日志、登录日志

### 租户管理

- 租户注册与管理
- 租户套餐配置
- 租户隔离与数据过滤
- 租户状态监控

### 认证授权

- 多种认证策略：
  - 密码认证（BCrypt）
  - 短信验证码
  - 邮箱验证码
  - OAuth 社交登录（20+ 平台）
  - 微信小程序
- JWT 令牌管理
- 登录失败锁定
- 验证码校验（图形/数学）

---

## 🏗️ 架构设计

### Trait 驱动的实体设计

FoxDen 使用 Jimmer 的 Trait 模型实现最大化代码复用：

```kotlin
@Entity
@Table(name = "sys_user")
interface SysUser : CommDelFlag, CommId, CommInfo, CommTenant {
    val userName: String
    val nickName: String?
    @ManyToMany
    @JoinTable(name = "sys_user_role")
    val roles: List<SysRole>
}
```

**可用的 Trait**：
- `CommId`：主键，自增
- `CommTenant`：多租户支持
- `CommInfo`：审计字段（创建/更新人/时间）
- `CommDelFlag`：软删除支持

### 数据权限过滤

使用 AOP 实现自动数据权限过滤：

```kotlin
@DataPermission(
    value = [
        DataColumn(key = ["deptName"], value = ["dept_id"])
    ]
)
fun selectUserList(bo: SysUserBo): List<SysUserVo> {
    // Jimmer 自动注入数据权限过滤条件
}
```

---

## 📚 文档

- [项目指南](CLAUDE.md) - 详细的项目文档
- [Jimmer ORM 指南](.claude/JIMMER_GUIDE.md) - Jimmer 使用指南
- [迁移指南](.claude/migration-guide.md) - Java 到 Kotlin 迁移笔记

---

## 🤝 贡献指南

欢迎贡献代码！请随时提交 Pull Request。

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 📄 开源协议

本项目采用 Apache License 2.0 协议 - 查看 [LICENSE](LICENSE) 文件了解详情。

---

## 🙏 致谢

- [Jimmer ORM](https://github.com/babyfish-ct/jimmer) - 创新的 ORM 框架
- [Sa-Token](https://github.com/dromara/sa-token) - 轻量级认证框架
- [JustAuth](https://github.com/justauth/JustAuth) - 社交登录集成
- [RuoYi-Vue-Pro](https://github.com/YunaiV/ruoyi-vue-pro) - 业务逻辑参考

---

## 📮 联系方式

- 作者：AlphaFoxZ
- 邮箱：[alphafoxz@qq.com](mailto:alphafoxz@qq.com)
- 问题反馈：[GitHub Issues](https://github.com/alphafoxz/foxden/issues)

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！⭐**

</div>
