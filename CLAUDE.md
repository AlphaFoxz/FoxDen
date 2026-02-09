# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FoxDen is a multi-tenant SaaS system built with Spring Boot, Kotlin, and Jimmer ORM. It features user management, role-based access control (RBAC), social login integration, and data permission filtering. The system supports multiple authentication strategies (password, SMS, email, social, WeChat mini-program).

## Module Structure

This is a multi-module Gradle project with the following structure:

```
foxden/
├── foxden-bom/                      # Dependency management (Bill of Materials)
├── foxden-common/                   # Common modules parent
│   ├── foxden-common-core/         # Core utilities, constants, exceptions, DTOs
│   ├── foxden-common-jimmer/       # Jimmer ORM common utilities, traits, data permissions
│   ├── foxden-common-web/          # Web common (base controllers, captcha, XSS filter)
│   ├── foxden-common-security/     # Sa-Token security configuration and utilities
│   ├── foxden-common-redis/        # Redis caching with Redisson
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
│   └── foxden-common-encrypt/      # API encryption/decryption (RSA+AES)
├── foxden-domain/                   # Domain layer parent
│   ├── foxden-domain-system/       # System domain (user, role, menu, dept, etc.)
│   ├── foxden-domain-tenant/       # Tenant domain
│   └── foxden-domain-infrastructure/ # Infrastructure services (repositories base)
├── foxden-app/                      # Application modules parent
│   ├── foxden-app-admin/           # Admin application (auth, login, registration)
│   └── foxden-app-system/          # System management controllers
└── gradle/
```

## Applications

- **foxden-app-admin**: Main admin application running on port 12003. Handles authentication, registration, and tenant selection. Entry point: `FoxdenAdminApplication.kt`
- **foxden-app-system**: System management controllers (user, role, menu, dept, etc.)

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

### Run a single test
```bash
./gradlew :foxden-app:foxden-app-admin:test --tests FoxdenAdminApplicationTests
```

## Technology Stack

- **Language**: Kotlin 2.3.0
- **Framework**: Spring Boot 3.5.10
- **Build Tool**: Gradle (Kotlin DSL) with KSP
- **Java Version**: 21
- **ORM**: Jimmer 0.10.6 (Kotlin-first ORM with compiled validation)
- **Database**: PostgreSQL
- **Security**: Sa-Token 1.44.0 with JWT (simple mode)
- **Key Dependencies**:
  - Spring Security, Spring WebMVC, Spring Validation
  - Jimmer Spring Boot Starter with KSP code generation
  - Jackson Kotlin Module 2.18.2
  - Spring DevTools
  - Redisson 3.35.0 (Redis/distributed locking)
  - Lock4j 2.2.4 (distributed locking)
  - JustAuth 1.16.7 (social login)
  - Hutool 5.8.43 (Chinese utility library)
  - SpringDoc OpenAPI 2.8.2 (documentation)
  - EasyExcel 4.0.3 (Excel import/export)

## Architecture

### Jimmer ORM Entity Model

The project uses Jimmer ORM with a **trait-based entity design**. All entities are Kotlin interfaces composed from reusable `@MappedSuperclass` traits:

| Trait | Purpose |
|-------|---------|
| `CommId` | Primary key (`id: Long`) with auto-generated ID |
| `CommTenant` | Multi-tenancy support (`tenantId: String`) |
| `CommInfo` | Audit fields (createDept, createBy, createTime, updateBy, updateTime, remark) |
| `CommDelFlag` | Soft delete (`delFlag: Boolean` with `@LogicalDeleted("true")`) |

Example entity composition:
```kotlin
@Entity
@Table(name = "sys_user")
interface SysUser : CommDelFlag, CommId, CommInfo, CommTenant {
    val userName: String
    val nickName: String
    @OnDissociate(DissociateAction.DELETE)
    val password: String?
    @ManyToMany
    @JoinTable(name = "sys_user_role")
    val roles: List<SysRole>
    // ... other fields
}
```

### Authentication Strategy Pattern

The system uses a strategy pattern for multiple authentication methods. Each strategy implements `AuthStrategy`:
- `PasswordAuthStrategy`: Username/password with BCrypt
- `SmsAuthStrategy`: SMS verification code
- `EmailAuthStrategy`: Email verification code
- `SocialAuthStrategy`: OAuth social login (JustAuth)
- `XcxAuthStrategy`: WeChat mini-program

Strategies are auto-registered with Spring using naming convention: `{type}AuthStrategy` where `{type}` is the auth type.

### Data Permission Filtering

Data permissions are implemented via AOP using `@DataPermission` and `@DataColumn` annotations:
- `@DataPermission`: Class or method-level annotation for data filtering
- `@DataColumn`: Defines placeholder keys (e.g., "deptName") and column mappings (e.g., "dept_id")
- `DataPermissionAdvice`: Intercepts repository methods to inject SQL filters
- `DataPermissionHelper.ignore { }`: Bypass data permission filtering when needed

### Multi-Tenancy

- Tenant ID is passed via HTTP header or parameter (`TenantConstants.TENANT_ID`)
- `TenantHelper.getTenantId()`: Retrieves current tenant from request
- `TenantHelper.dynamic(tenantId) { }`: Execute code block with specific tenant context
- Entities implementing `CommTenant` are automatically filtered by tenant ID
- Super admin can switch tenants dynamically

### Repository Pattern

The project does NOT use traditional repository interfaces. Instead, domain services use:
- JImmer's `KDbContext` for direct SQL execution (injected into services)
- Extension functions in `ServiceExtensions.kt` for common CRUD operations
- Fetcher-based partial loading for performance

Example service usage:
```kotlin
// Using extension functions
val user = sqlClient.findById(SysUser::class, userId)
val users = sqlClient.query(SysUser::class).where(table -> table.userName.eq(username)).execute()
```

### Package Structure

```
com.github.alphafoxz.foxden
├── common/
│   ├── core/                        # Core utilities, constants, exceptions
│   ├── jimmer/                      # Jimmer traits (CommId, CommTenant, etc.)
│   ├── web/                         # Web common utilities
│   ├── security/                    # Security utilities
│   └── mail/                        # Email functionality
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

### Detailed Package Structure

```
com.github.alphafoxz.foxden
├── common/
│   ├── core/                        # Core utilities, constants, exceptions, DTOs
│   ├── jimmer/                      # Jimmer traits (CommId, CommTenant, etc.)
│   │   ├── annotation/              # Data permission annotations
│   │   ├── aspect/                  # AOP advice for data permissions
│   │   ├── entity/comm/             # Common entity traits
│   │   ├── helper/                  # TenantHelper, DataPermissionHelper
│   │   └── config/                  # Jimmer configuration
│   ├── web/                         # Web common (captcha, XSS, i18n)
│   ├── security/                    # Sa-Token configuration
│   └── [other common modules]       # mail, sms, oss, excel, etc.
├── domain/
│   ├── system/                      # System domain
│   │   ├── entity/                  # SysUser, SysRole, SysMenu, etc.
│   │   ├── service/                 # Business logic interfaces
│   │   │   └── impl/                # Service implementations
│   │   ├── service/extensions/      # Kotlin extension functions for CRUD
│   │   ├── bo/                      # Business Objects (input)
│   │   └── vo/                      # View Objects (output)
│   ├── tenant/                      # Tenant domain entities/services
│   └── infrastructure/              # Base infrastructure
└── app/
    ├── admin/                       # Admin application
    │   ├── FoxdenAdminApplication.kt # Main entry point
    │   ├── controller/              # Auth, captcha, index controllers
    │   ├── service/                 # Login, register, auth strategies
    │   └── domain/vo/               # App-specific VOs (LoginVo, etc.)
    └── system/                      # System management controllers
        └── controller/              # User, role, menu, dept, etc.
```

## Code Generation

The project includes `code-gen/` directory with Jimmer code generator tool:
- `init.bat` / `init.sh` - Download the `jimmer-code-gen.jar`
- `run.bat` / `run.sh` - Run the code generator
- `model-[FoxDen-sys].json` - Generator configuration

Run `code-gen/run.bat` to regenerate DTOs and fetchers based on entity definitions.

## Configuration

### Active Profile
Default profile is `dev` (see `foxden-app-admin/src/main/resources/application.yaml`).

### Database Configuration
For PostgreSQL:
```yaml
jimmer:
  dialect: org.babyfish.jimmer.sql.dialect.PostgresDialect
```

For PostgreSQL (production):
```yaml
jimmer:
  dialect: org.babyfish.jimmer.sql.dialect.PostgresDialect
```

### Key Configuration Properties
- `user.password.maxRetryCount`: Maximum login retry attempts (default: 5)
- `user.password.lockTime`: Account lock duration in minutes (default: 10)
- `foxden.captchaEnabled`: Enable/disable captcha verification
- `foxden.addressEnabled`: Enable/disable IP address resolution

### Known Issues
- JMX warnings on startup are harmless (JMX is disabled in config)
- Jimmer's `@LogicalDeleted` on Boolean properties requires explicit `value` parameter
- KSP generates code to `build/generated/ksp/main/kotlin` (automatically included)

## Kotlin Compiler Options

- `-Xjsr305=strict`: Strict nullability annotations
- KSP generates code to `build/generated/ksp/main/kotlin` (included in source set)
- KSP2 is enabled via `ksp.useKSP2=true` in gradle.properties

## Development Notes

### Adding a New Entity
1. Define entity interface in appropriate domain module, extending relevant traits
2. Add KSP annotations (`@Entity`, `@Table`, relationships)
3. Create corresponding BO (input), VO (output), and Fetcher
4. Run `code-gen/run.bat` to regenerate DTOs if using the code generator
5. Implement service interface and implementation

### Adding a New Authentication Strategy
1. Create class in `foxden-app-admin/service/impl/` named `{Type}AuthStrategy`
2. Implement `AuthStrategy` interface
3. Register as Spring bean with name: `{type}AuthStrategy` where `{type}` is the auth type
4. Strategy will be automatically discovered and used

### Using Data Permissions
1. Add `@DataPermission` annotation to service method or class
2. Specify `@DataColumn` mappings for placeholder replacement
3. Use `DataPermissionHelper.ignore { }` to bypass filtering when needed
