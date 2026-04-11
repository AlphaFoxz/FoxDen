# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FoxDen is a multi-tenant SaaS system built with Spring Boot, Kotlin, and Jimmer ORM. It features user management, role-based access control (RBAC), social login integration, and data permission filtering. The system supports multiple authentication strategies (password, SMS, email, social, WeChat mini-program).

### Legacy Reference Code

The `old-version/` directory contains the original ruoyi Java/MyBatis codebase for reference. **Always search within `old-version/` when looking at legacy code** — never search parent directories.

## Module Structure

```
foxden/
├── foxden-bom/                      # Dependency management (Bill of Materials)
├── foxden-common/                   # Common modules parent
│   ├── foxden-common-core/         # Core utilities, constants, exceptions, DTOs
│   ├── foxden-common-jimmer/       # Jimmer ORM utilities, traits, data permissions
│   ├── foxden-common-web/          # Web common (captcha, XSS filter, i18n)
│   ├── foxden-common-security/     # Sa-Token security configuration
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
│   ├── foxden-common-encrypt/      # API encryption/decryption (RSA+AES)
│   ├── foxden-common-job/          # Task scheduling (SnailJob)
│   ├── foxden-common-sensitive/    # Sensitive data masking
│   ├── foxden-common-sse/          # Server-Sent Events
│   ├── foxden-common-tenant/       # Tenant common utilities
│   └── foxden-common-translation/  # Dictionary translation service
├── foxden-domain/                   # Domain layer parent
│   ├── foxden-domain-system/       # System domain (user, role, menu, dept, etc.)
│   ├── foxden-domain-tenant/       # Tenant domain
│   ├── foxden-domain-infrastructure/ # Infrastructure services (repositories base)
│   ├── foxden-domain-gen/          # Code generation domain
│   ├── foxden-domain-job/          # Task scheduling domain
│   ├── foxden-domain-test/         # Test domain
│   └── foxden-domain-workflow/     # Workflow domain (WarmFlow)
├── foxden-app/                      # Application modules parent
│   ├── foxden-app-admin/           # Admin application (auth, login, registration) - port 12003
│   ├── foxden-app-system/          # System management controllers - port 12004
│   ├── foxden-app-job/             # Task scheduling application - port 12005
│   └── foxden-app-workflow/        # Workflow application - port 12006
└── vue/                             # Vue 3 frontend (see vue/CLAUDE.md)
```

## Build and Development Commands

### Backend (Gradle)
```bash
./gradlew build                                          # Build entire project
./gradlew :foxden-domain:foxden-domain-system:build      # Build specific module
./gradlew :foxden-app:foxden-app-admin:bootRun           # Run admin application
./gradlew :foxden-app:foxden-app-system:bootRun          # Run system application
./gradlew test                                           # Run all tests
./gradlew :foxden-app:foxden-app-admin:test --tests FoxdenAdminApplicationTests  # Single test
./gradlew clean build                                    # Clean build
./gradlew --stop                                         # Stop Gradle daemon
```

### Frontend (Vue)
```bash
cd vue
bun install          # Install dependencies (use bun, not npm)
bun run dev          # Dev server (proxies API to localhost:12003)
bun run build        # Production build
bun run test:unit    # Vitest unit tests
npx xo [file]        # Lint with XO
```

### Code Generation
Run `code-gen/run.bat` to regenerate Jimmer DTOs and fetchers from entity definitions.

## Technology Stack

- **Language**: Kotlin 2.3.0, TypeScript 5.9 (frontend)
- **Framework**: Spring Boot 3.5.11, Vue 3.5+ (frontend)
- **Build Tool**: Gradle (Kotlin DSL) with KSP
- **Java Version**: 21
- **ORM**: Jimmer 0.10.6 (Kotlin-first ORM with compile-time validation)
- **Database**: PostgreSQL (dev: localhost:12001)
- **Cache**: Redis with Redisson
- **Security**: Sa-Token 1.44.0 with JWT (simple mode)
- **Frontend**: Vite 7.x, PrimeVue 4.5+, vue-i18n, Axios
- **Key Libraries**: JustAuth 1.16.7 (social), Hutool 5.8.43, EasyExcel 4.0.3, SnailJob 1.9.0 (scheduling), WarmFlow 1.8.4 (workflow)

## Architecture

### Jimmer ORM Entity Model

The project uses Jimmer ORM with a **trait-based entity design**. All entities are Kotlin interfaces composed from reusable `@MappedSuperclass` traits:

| Trait | Purpose |
|-------|---------|
| `CommId` | Primary key (`id: Long`) with auto-generated ID |
| `CommTenant` | Multi-tenancy support (`tenantId: String`) |
| `CommInfo` | Audit fields (createDept, createBy, createTime, updateBy, updateTime, remark) |
| `CommDelFlag` | Soft delete (`delFlag: Boolean` with `@LogicalDeleted("true")`) |

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
}
```

### Authentication Strategy Pattern

Multiple auth methods via strategy pattern. Each strategy implements `AuthStrategy` and is auto-registered as `{type}AuthStrategy`:
- `PasswordAuthStrategy`, `SmsAuthStrategy`, `EmailAuthStrategy`, `SocialAuthStrategy`, `XcxAuthStrategy`

### Data Permission Filtering

AOP-based via `@DataPermission` and `@DataColumn` annotations. Use `DataPermissionHelper.ignore { }` to bypass filtering when needed.

### Multi-Tenancy

- Tenant ID passed via HTTP header/parameter (`TenantConstants.TENANT_ID`)
- `TenantHelper.getTenantId()` / `TenantHelper.dynamic(tenantId) { }` for tenant context
- Entities with `CommTenant` are automatically filtered by tenant ID

### Repository Pattern

No traditional repository interfaces. Domain services use Jimmer's `KSqlClient` directly with extension functions for CRUD operations.

### Package Structure

```
com.github.alphafoxz.foxden
├── common/
│   ├── core/                        # Core utilities, constants, exceptions, DTOs
│   ├── jimmer/                      # Jimmer traits, data permissions, helpers
│   │   ├── annotation/              # Data permission annotations
│   │   ├── aspect/                  # AOP advice for data permissions
│   │   ├── entity/comm/             # Common entity traits
│   │   ├── helper/                  # TenantHelper, DataPermissionHelper
│   │   └── config/                  # Jimmer configuration
│   ├── web/                         # Web common (captcha, XSS, i18n)
│   ├── security/                    # Sa-Token configuration
│   └── [other common modules]       # mail, sms, oss, excel, job, sse, etc.
├── domain/
│   ├── system/                      # System domain
│   │   ├── entity/                  # SysUser, SysRole, SysMenu, etc.
│   │   ├── service/                 # Business logic interfaces & impl/
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

## Configuration

- **Active Profile**: `dev` (see `foxden-app-admin/src/main/resources/application.yaml`)
- **Database**: PostgreSQL at `localhost:12001` (dev)
- **Server Ports**: admin=12003, system=12004, job=12005, workflow=12006
- **Key Properties**: `user.password.maxRetryCount` (5), `user.password.lockTime` (10 min), `foxden.captchaEnabled`, `foxden.addressEnabled`

### Kotlin Compiler Options

- `-Xjsr305=strict`: Strict nullability annotations
- KSP2 enabled via `ksp.useKSP2=true` in gradle.properties
- KSP generates code to `build/generated/ksp/main/kotlin` (automatically included in source set)

### Known Issues

- JMX warnings on startup are harmless (JMX is disabled in config)
- Jimmer's `@LogicalDeleted` on Boolean properties requires explicit `value` parameter

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
