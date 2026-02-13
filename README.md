<div align="center">

![FoxDen Logo](docs/logo.png)

# FoxDen

**A Modern Multi-Tenant SaaS System Built with Spring Boot, Kotlin, and Jimmer ORM**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-green.svg?logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Jimmer](https://img.shields.io/badge/Jimmer-0.10.6-orange.svg)](https://babyfish-ct.github.io/jimmer-doc/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

[English](README.md) | [简体中文](README.zh-CN.md)

</div>

---

## 📖 Overview

FoxDen is a comprehensive multi-tenant SaaS platform designed for building enterprise-grade applications. It combines modern JVM technologies with best practices to deliver a robust, scalable, and maintainable system.

### ✨ Key Features

- 🏢 **Multi-Tenancy**: Built-in tenant isolation and management
- 🔐 **Advanced Security**: Sa-Token based authentication with multiple strategies (password, SMS, email, OAuth, WeChat)
- 🎯 **Role-Based Access Control**: Fine-grained permissions with data scope filtering
- 🗄️ **Modern ORM**: Jimmer ORM with compile-time validation and N+1 query prevention
- 📊 **Data Permissions**: Automatic data filtering based on user roles and departments
- 🚀 **High Performance**: Redis caching with Redisson, distributed locking
- 📝 **Comprehensive Logging**: Operation logs, login logs, and audit trails
- 🌐 **Social Login**: Built-in support for 20+ social platforms via JustAuth
- 📄 **Excel Import/Export**: EasyExcel integration for data handling
- 📦 **Object Storage**: Support for MinIO, Aliyun OSS, and QCloud COS
- ⏰ **Task Scheduling**: SnailJob distributed task scheduling
- 🔄 **Workflow Engine**: WarmFlow workflow engine for business process management
- 🔒 **Idempotency**: API idempotency protection
- 🚦 **Rate Limiting**: API rate limiting support
- 🎭 **Sensitive Data**: Automatic data masking for sensitive fields
- 📡 **Server-Sent Events**: Real-time push notifications via SSE
- 🌐 **Service Translation**: Dictionary translation service

---

## 🛠️ Technology Stack

| Category | Technology | Version |
|----------|-----------|---------|
| **Language** | Kotlin | 2.3.0 |
| **Framework** | Spring Boot | 3.5.10 |
| **ORM** | Jimmer | 0.10.6 |
| **Database** | PostgreSQL | - |
| **Cache** | Redis (Redisson) | 3.52.0 |
| **Security** | Sa-Token | 1.44.0 |
| **API Docs** | SpringDoc OpenAPI | 2.8.15 |
| **Excel** | EasyExcel | 4.0.3 |
| **Distributed Lock** | Lock4j | 2.2.7 |
| **Task Scheduling** | SnailJob | 1.9.0 |
| **Workflow** | WarmFlow | 1.8.4 |
| **Build** | Gradle (Kotlin DSL) | - |
| **JDK** | Java | 21 |

---

## 🚀 Quick Start

### Prerequisites

- JDK 21+
- Gradle 8.x
- PostgreSQL
- Redis (optional, for caching)

### Clone & Build

```bash
git clone https://github.com/alphafoxz/foxden.git
cd foxden

# Build the project
./gradlew build

# Or run tests
./gradlew test
```

### Run the Application

```bash
# Start admin application (port 12003)
./gradlew :foxden-app:foxden-app-admin:bootRun
```

### Access Points

- **Admin Application**: http://localhost:12003
- **API Documentation**: http://localhost:12003/swagger-ui.html
- **Health Check**: http://localhost:12003/actuator/health

### Run Specific Applications

```bash
# Admin application (port 12003)
./gradlew :foxden-app:foxden-app-admin:bootRun

# System management (port 12004)
./gradlew :foxden-app:foxden-app-system:bootRun

# Job scheduling (port 12005)
./gradlew :foxden-app:foxden-app-job:bootRun

# Workflow (port 12006)
./gradlew :foxden-app:foxden-app-workflow:bootRun
```

### Default Configuration

```yaml
# Database (PostgreSQL)
spring:
  datasource:
    url: jdbc:postgresql://localhost:12001/postgres
    username: postgres
    password: 123456

# Server
server:
  port: 12003
```

---

## 📁 Project Structure

```
foxden/
├── foxden-bom/                      # Dependency management (Bill of Materials)
├── foxden-common/                   # Common modules
│   ├── foxden-common-core/         # Core utilities, constants, exceptions, DTOs
│   ├── foxden-common-jimmer/       # Jimmer ORM utilities, traits, data permissions
│   ├── foxden-common-web/          # Web common (captcha, XSS filter, i18n)
│   ├── foxden-common-security/     # Sa-Token security configuration
│   ├── foxden-common-redis/        # Redis caching with Redisson
│   ├── foxden-common-encrypt/      # API encryption/decryption (RSA+AES)
│   ├── foxden-common-log/          # Logging annotations and event publishing
│   ├── foxden-common-oss/          # Object Storage Service (MinIO, Aliyun, QCloud)
│   ├── foxden-common-excel/        # Excel import/export with EasyExcel
│   ├── foxden-common-mail/         # Email functionality
│   ├── foxden-common-sms/          # SMS functionality
│   ├── foxden-common-social/       # Social login (JustAuth)
│   ├── foxden-common-doc/          # SpringDoc OpenAPI documentation
│   ├── foxden-common-idempotent/   # Idempotent request handling
│   ├── foxden-common-ratelimiter/  # Rate limiting
│   ├── foxden-common-json/         # JSON configuration
│   ├── foxden-common-job/          # Task scheduling (SnailJob)
│   ├── foxden-common-sensitive/    # Sensitive data masking
│   ├── foxden-common-sse/          # Server-Sent Events
│   ├── foxden-common-tenant/       # Tenant common utilities
│   └── foxden-common-translation/  # Dictionary translation service
├── foxden-domain/                   # Domain layer
│   ├── foxden-domain-system/       # System domain (user, role, menu, dept, etc.)
│   ├── foxden-domain-tenant/       # Tenant domain
│   ├── foxden-domain-infrastructure/ # Infrastructure services
│   ├── foxden-domain-gen/          # Code generation
│   ├── foxden-domain-job/          # Task scheduling domain
│   ├── foxden-domain-test/         # Test domain
│   └── foxden-domain-workflow/     # Workflow domain (WarmFlow)
└── foxden-app/                      # Application layer
    ├── foxden-app-admin/           # Admin application (auth, login, registration)
    ├── foxden-app-system/          # System management controllers
    ├── foxden-app-job/             # Task scheduling application
    └── foxden-app-workflow/        # Workflow application
```

---

## 🎯 Core Modules

### System Management

- **User Management**: User CRUD, password reset, role assignment
- **Role Management**: Role CRUD, permission configuration, data scope
- **Menu Management**: Dynamic menu tree, route configuration
- **Department Management**: Department tree, data permissions
- **Dictionary Management**: System dictionaries, cache refresh
- **Parameter Configuration**: System parameters, dynamic config
- **Notice Management**: Announcement publishing
- **Log Management**: Operation logs, login logs
- **Client Management**: Client application management
- **OSS Management**: Object storage configuration and file management

### Tenant Management

- Tenant registration and management
- Tenant package configuration
- Tenant isolation and data filtering
- Tenant status monitoring

### Authentication & Authorization

- Multiple authentication strategies:
  - Password authentication (BCrypt)
  - SMS verification code
  - Email verification code
  - OAuth social login (20+ platforms)
  - WeChat mini-program
- JWT token management
- Login failure lockout
- Captcha verification (image/math)

### Task Scheduling (SnailJob)

- Distributed task scheduling with SnailJob
- Cron expression based scheduling
- Task monitoring and execution logs
- Failed retry mechanism
- Task dependency management

### Workflow Engine (WarmFlow)

- Visual workflow designer
- Process definition and deployment
- Task assignment and approval
- Process instance monitoring
- Historical query and audit trails

---

## 🏗️ Architecture

### Trait-Based Entity Design

FoxDen uses Jimmer's trait-based entity model for maximum code reuse:

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

**Available Traits**:
- `CommId`: Primary key with auto-generation
- `CommTenant`: Multi-tenancy support
- `CommInfo`: Audit fields (created/updated by/time)
- `CommDelFlag`: Soft delete support

### Data Permission Filtering

Automatic data filtering using AOP:

```kotlin
@DataPermission(
    value = [
        DataColumn(key = ["deptName"], value = ["dept_id"])
    ]
)
fun selectUserList(bo: SysUserBo): List<SysUserVo> {
    // Jimmer automatically injects data permission filters
}
```

---

## 📚 Documentation

- [Project Guide](.claude/CLAUDE.md) - Detailed project documentation
- [Jimmer ORM Guide](.claude/JIMMER_GUIDE.md) - Jimmer usage guide
- [Migration Guide](.claude/migration-guide.md) - Java to Kotlin migration notes

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- [Jimmer ORM](https://github.com/babyfish-ct/jimmer) - The innovative ORM framework
- [Sa-Token](https://github.com/dromara/sa-token) - Lightweight authentication framework
- [JustAuth](https://github.com/justauth/JustAuth) - Social login integration
- [RuoYi-Vue-Pro](https://github.com/YunaiV/ruoyi-vue-pro) - Inspiration for business logic

---

## 📮 Contact

- Author: AlphaFoxZ
- Email: [alphafoxz@qq.com](mailto:alphafoxz@qq.com)
- Issues: [GitHub Issues](https://github.com/alphafoxz/foxden/issues)

---

<div align="center">

**⭐ If you like this project, please give it a star! ⭐**

</div>
