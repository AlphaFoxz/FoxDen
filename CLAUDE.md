# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FoxDen is a multi-tenant SaaS system built with Spring Boot, Kotlin, and Jimmer ORM. It features user management, role-based access control (RBAC), and social login integration.

## Module Structure

This is a multi-module Gradle project with the following structure:

```
foxden/
├── foxden-bom/                      # Dependency management (Bill of Materials)
├── foxden-common/                   # Common modules parent
│   ├── foxden-common-core/         # Core utilities, constants, exceptions
│   ├── foxden-common-jimmer/       # Jimmer ORM common utilities and base classes
│   ├── foxden-common-web/          # Web common utilities (base controllers, etc.)
│   ├── foxden-common-security/     # Security utilities and configurations
│   └── foxden-common-email/        # Email functionality (future)
├── foxden-domain/                   # Domain layer parent
│   ├── foxden-domain-system/       # System domain (user, role, menu, dept, etc.)
│   ├── foxden-domain-tenant/       # Tenant domain
│   └── foxden-domain-infrastructure/ # Infrastructure services (repositories base)
├── foxden-app/                      # Application modules parent
│   └── foxden-app-admin/           # Admin application (main entry point)
└── gradle/
```

## Build and Development Commands

### Build entire project
```bash
./gradlew build
```

### Build specific module
```bash
./gradlew :foxden-domain:foxden-domain-system:build
```

### Run the application
```bash
./gradlew :foxden-app:foxden-app-admin:bootRun
```

### Run tests
```bash
./gradlew test
```

### Clean build
```bash
./gradlew clean build
```

### Stop Gradle Daemon (if needed for JMX/connection issues)
```bash
./gradlew --stop
```

## Technology Stack

- **Language**: Kotlin 2.3.0
- **Framework**: Spring Boot 3.5.10
- **Build Tool**: Gradle (Kotlin DSL) with KSP
- **Java Version**: 21
- **ORM**: Jimmer 0.9.120 (Kotlin-first ORM with compiled validation)
- **Database**: PostgreSQL (production), H2 (development/testing)
- **Key Dependencies**:
  - Spring Security, Spring WebMVC, Spring Validation
  - Jimmer Spring Boot Starter with KSP code generation
  - Jackson Kotlin Module
  - Spring DevTools

## Architecture

### Jimmer ORM Entity Model

The project uses Jimmer ORM with a **trait-based entity design**. All entities are Kotlin interfaces composed from reusable `@MappedSuperclass` traits:

| Trait | Purpose |
|-------|---------|
| `CommId` | Primary key (`id: Long`) with IDENTITY generation |
| `CommTenant` | Multi-tenancy support (`tenantId: String`) |
| `CommInfo` | Audit fields (createDept, createBy, createTime, updateBy, updateTime) |
| `CommDelFlag` | Soft delete (`delFlag: Boolean` with `@LogicalDeleted("true")`) |

Example entity composition:
```kotlin
@Entity
@Table(name = "sys_user")
interface SysUser : CommDelFlag, CommId, CommInfo, CommTenant {
    val userName: String
    val nickName: String
    // ... other fields
}
```

### Repository Pattern

Repositories extend `KRepository<Entity, ID>` and support Jimmer's `Fetcher` API for optimized partial object loading:

```kotlin
interface SysUserRepository : KRepository<SysUser, Long> {
    fun findByUserName(username: String, fetcher: Fetcher<SysUser>): SysUser?
}
```

### Package Structure

```
com.github.alphafoxz.foxden
├── common/
│   ├── core/                        # Core utilities, constants, exceptions
│   ├── jimmer/                      # Jimmer traits (CommId, CommTenant, etc.)
│   ├── web/                         # Web common utilities
│   ├── security/                    # Security utilities
│   └── email/                       # Email functionality
├── domain/
│   ├── system/                      # System domain entities, repositories, services
│   │   ├── entity/                  # SysUser, SysRole, SysMenu, etc.
│   │   ├── repository/              # Jimmer repositories
│   │   └── service/                 # Business logic
│   ├── tenant/                      # Tenant domain
│   │   ├── entity/                  # SysTenant, SysTenantPackage
│   │   ├── repository/              # Tenant repositories
│   │   └── service/                 # Tenant services
│   └── infrastructure/              # Base repositories, utilities
└── app/
    └── admin/                       # Admin application
        ├── FoxdenAdminApplication.kt # Main entry point
        ├── controller/              # REST controllers
        └── config/                  # Configurations
```

### Domain Areas

- **Tenant Management**: `sys_tenant`, `sys_tenant_package` (foxden-domain-tenant)
- **Organization**: `sys_dept`, `sys_post` (foxden-domain-system)
- **User & Roles**: `sys_user`, `sys_role`, `sys_menu` (foxden-domain-system)
- **RBAC Relations**: `rel_user_role`, `rel_role_menu`, `rel_role_dept`, `rel_user_post` (foxden-domain-system)
- **Social Login**: `sys_social` (foxden-domain-system)
- **System Logs**: `sys_oper_log`, `sys_logininfor` (foxden-domain-system)
- **Configuration**: `sys_dict_type`, `sys_dict_data`, `sys_config` (foxden-domain-system)
- **Notifications**: `sys_notice` (foxden-domain-system)
- **Storage**: `sys_oss`, `sys_oss_config` (foxden-domain-system)
- **Client Management**: `sys_client` (foxden-domain-system)

## Code Generation

The project includes `code-gen/` directory with Jimmer code generator tool:
- `init.bat` / `init.sh` - Download the `jimmer-code-gen.jar`
- `run.bat` / `run.sh` - Run the code generator
- `model-[FoxDen-sys].json` - Generator configuration

Run `code-gen/run.bat` to regenerate DTOs and fetchers based on entity definitions.

## Configuration

### Active Profile
Default profile is `dev` (see `foxden-app-admin/src/main/resources/application.yaml`). Configure database and Jimmer settings in `application-dev.yaml`:
```yaml
jimmer:
  dialect: org.babyfish.jimmer.sql.dialect.PostgresDialect
  show-sql: true
  pretty-sql: true
```

For H2 compatibility, use `org.babyfish.jimmer.sql.dialect.H2Dialect`.

### Known Issues
- JMX warnings on startup are harmless (JMX is disabled in config)
- Jimmer's `@LogicalDeleted` on Boolean properties requires explicit `value` parameter

## Kotlin Compiler Options

- `-Xjsr305=strict`: Strict nullability annotations
- KSP generates code to `build/generated/ksp/main/kotlin` (included in source set)
