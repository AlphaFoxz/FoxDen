# FoxDen Admin Application

FoxDen 管理端应用，提供用户认证、授权、租户管理等功能。

## 技术栈

- Kotlin 2.3.0
- Spring Boot 3.4.1
- Jimmer 0.10.6 ORM
- Sa-Token 1.44.0 (认证授权)
- Redis (Redisson 3.35.0)
- PostgreSQL / H2

## 模块结构

```
foxden-app-admin/
├── src/main/kotlin/com/github/alphafoxz/foxden/app/admin/
│   ├── FoxdenAdminApplication.kt      # 主应用类
│   ├── config/                        # 配置类
│   │   └── ApplicationConfig.kt
│   ├── controller/                    # 控制器
│   │   ├── AuthController.kt          # 认证控制器
│   │   ├── CaptchaController.kt       # 验证码控制器
│   │   └── IndexController.kt         # 首页控制器
│   ├── service/                       # 服务层
│   │   ├── AuthStrategy.kt            # 认证策略接口
│   │   ├── SysLoginService.kt         # 登录服务
│   │   ├── SysRegisterService.kt      # 注册服务
│   │   └── impl/                      # 认证策略实现
│   │       ├── PasswordAuthStrategy.kt
│   │       ├── EmailAuthStrategy.kt
│   │       └── SmsAuthStrategy.kt
│   ├── listener/                      # 监听器
│   │   └── UserActionListener.kt
│   └── domain/vo/                     # 视图对象
│       ├── LoginVo.kt
│       ├── CaptchaVo.kt
│       ├── LoginTenantVo.kt
│       └── TenantListVo.kt
└── src/main/resources/
    ├── application.yaml               # 主配置文件
    └── application-dev.yaml           # 开发环境配置
```

## 功能特性

### 1. 用户认证
- 密码登录
- 邮箱验证码登录
- 短信验证码登录
- 社交登录（待完善）
- 小程序登录（待完善）

### 2. 验证码
- 图形验证码
- 数学验证码
- 邮箱验证码
- 短信验证码

### 3. 租户管理
- 多租户支持
- 租户隔离
- 租户选择

### 4. 安全特性
- 登录失败锁定
- 密码加密（BCrypt）
- Token管理（Sa-Token）
- 限流保护

## 运行方式

### 开发环境
```bash
./gradlew :foxden-app:foxden-app-admin:bootRun
```

### 生产环境
```bash
./gradlew :foxden-app:foxden-app-admin:build
java -jar foxden-app/foxden-app-admin/build/libs/foxden-app-admin.jar
```

## 配置说明

### application.yaml
主要配置项：
- 服务端口：12003
- 数据库：PostgreSQL（生产）/ H2（开发）
- Jimmer ORM配置（Kotlin DSL）
- API加密配置（RSA+AES混合加密）
- 日志配置

### application-dev.yaml
开发环境配置：
- H2内存数据库
- Jimmer H2方言
- SQL日志输出

## API接口

### 认证接口
- POST /auth/login - 用户登录
- POST /auth/logout - 用户登出
- POST /auth/register - 用户注册
- GET /auth/tenant/list - 获取租户列表

### 验证码接口
- GET /auth/code - 获取图形验证码
- GET /resource/sms/code - 获取短信验证码
- GET /resource/email/code - 获取邮箱验证码

### 其他
- GET / - 首页欢迎信息

## 依赖关系

本模块依赖以下模块：
- foxden-common-* (所有公共模块)
- foxden-domain-system (系统域)
- foxden-domain-tenant (租户域)

## 开发说明

### 添加新的认证策略

1. 实现 `AuthStrategy` 接口
2. 添加 `@Service` 注解，bean名称格式为 `{grantType}AuthStrategy`
3. 在 `LoginBody` 中添加对应的登录体类

### 添加新的验证码类型

1. 在 `CaptchaController` 中添加新的接口
2. 实现验证码生成逻辑
3. 存储到Redis中供后续验证

## 注意事项

1. 本模块已完成从Java到Kotlin的迁移
2. 部分社交登录功能待完善
3. 需要配合前端Vue项目使用
4. 生产环境请配置PostgreSQL数据库

## 许可证

Copyright © 2025 FoxDen Team
