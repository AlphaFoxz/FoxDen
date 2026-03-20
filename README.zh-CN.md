<div align="center">

![FoxDen Logo](docs/logo.png)

# FoxDen

基于 Spring Boot、Kotlin 和 Jimmer ORM 构建的现代化多租户 SaaS 系统

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.11-green.svg?logo=spring-boot)](https://spring.io/projects/spring-boot)
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
- ⏰ **任务调度**：SnailJob 分布式任务调度
- 🔄 **工作流引擎**：WarmFlow 工作流引擎
- 🔒 **接口幂等**：API 幂等性保护
- 🚦 **接口限流**：API 限流支持
- 🎭 **敏感数据**：敏感字段自动脱敏
- 📡 **服务端推送**：SSE 实时推送
- 🌐 **服务翻译**：字典翻译服务

---

## 🛠️ 技术栈

| 分类 | 技术 | 版本     |
|------|------|--------|
| **开发语言** | Kotlin | 2.3.0  |
| **框架** | Spring Boot | 3.5.11 |
| **ORM** | Jimmer | 0.10.6 |
| **数据库** | PostgreSQL | -      |
| **缓存** | Redis (Redisson) | 3.52.0 |
| **安全** | Sa-Token | 1.44.0 |
| **API 文档** | SpringDoc OpenAPI | 2.8.15 |
| **Excel** | EasyExcel | 4.0.3  |
| **分布式锁** | Lock4j | 2.2.7  |
| **任务调度** | SnailJob | 1.9.0  |
| **工作流** | WarmFlow | 1.8.4  |
| **构建工具** | Gradle (Kotlin DSL) | -      |
| **JDK** | Java | 21     |

---

## 🚀 快速开始

### 前置要求

- JDK 21+
- Gradle 8.x
- PostgreSQL
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

- **管理端应用**: http://localhost:12003
- **API 文档**: http://localhost:12003/swagger-ui.html
- **健康检查**: http://localhost:12003/actuator/health

### 运行特定应用

```bash
# 管理端应用（端口 12003）
./gradlew :foxden-app:foxden-app-admin:bootRun

# 系统管理（端口 12004）
./gradlew :foxden-app:foxden-app-system:bootRun

# 任务调度（端口 12005）
./gradlew :foxden-app:foxden-app-job:bootRun

# 工作流（端口 12006）
./gradlew :foxden-app:foxden-app-workflow:bootRun
```

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
├── foxden-bom/                      # 依赖管理 (Bill of Materials)
├── foxden-common/                   # 通用模块
│   ├── foxden-common-core/         # 核心工具、常量、异常、DTO
│   ├── foxden-common-jimmer/       # Jimmer ORM 工具、Trait、数据权限
│   ├── foxden-common-web/          # Web 通用（验证码、XSS、国际化）
│   ├── foxden-common-security/     # Sa-Token 安全配置
│   ├── foxden-common-redis/        # Redis 缓存（Redisson）
│   ├── foxden-common-encrypt/      # API 加解密（RSA+AES）
│   ├── foxden-common-log/          # 日志注解和事件发布
│   ├── foxden-common-oss/          # 对象存储（MinIO、阿里云、腾讯云）
│   ├── foxden-common-excel/        # Excel 导入导出（EasyExcel）
│   ├── foxden-common-mail/         # 邮件功能
│   ├── foxden-common-sms/          # 短信功能
│   ├── foxden-common-social/       # 社交登录（JustAuth）
│   ├── foxden-common-doc/          # SpringDoc OpenAPI 文档
│   ├── foxden-common-idempotent/   # 幂等性处理
│   ├── foxden-common-ratelimiter/  # 限流功能
│   ├── foxden-common-json/         # JSON 配置
│   ├── foxden-common-job/          # 任务调度（SnailJob）
│   ├── foxden-common-sensitive/    # 敏感数据脱敏
│   ├── foxden-common-sse/          # 服务端推送（SSE）
│   ├── foxden-common-tenant/       # 租户通用工具
│   └── foxden-common-translation/  # 字典翻译服务
├── foxden-domain/                   # 领域层
│   ├── foxden-domain-system/       # 系统域（用户、角色、菜单、部门等）
│   ├── foxden-domain-tenant/       # 租户域
│   ├── foxden-domain-infrastructure/ # 基础设施服务
│   ├── foxden-domain-gen/          # 代码生成
│   ├── foxden-domain-job/          # 任务调度域
│   ├── foxden-domain-test/         # 测试域
│   └── foxden-domain-workflow/     # 工作流域（WarmFlow）
└── foxden-app/                      # 应用层
    ├── foxden-app-admin/           # 管理端应用（认证、登录、注册）
    ├── foxden-app-system/          # 系统管理控制器
    ├── foxden-app-job/             # 任务调度应用
    └── foxden-app-workflow/        # 工作流应用
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
- **客户端管理**：客户端应用管理
- **OSS 管理**：对象存储配置与文件管理

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

### 任务调度（SnailJob）

- SnailJob 分布式任务调度
- Cron 表达式调度
- 任务监控与执行日志
- 失败重试机制
- 任务依赖管理

### 工作流引擎（WarmFlow）

- 可视化流程设计器
- 流程定义与部署
- 任务分配与审批
- 流程实例监控
- 历史查询与审计追踪

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

- [项目指南](.claude/CLAUDE.md) - 详细的项目文档
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
