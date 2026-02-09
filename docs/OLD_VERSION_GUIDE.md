# RuoYi 项目结构导航文档

> **路径**: `/mnt/f/idea_projects/FoxDen/old-version/`
> **用途**: 旧项目参考实现（Java/MyBatis），用于对比和参考
> **最后更新**: 2026-02-10

---

## 目录

- [项目概述](#项目概述)
- [模块结构](#模块结构)
- [业务模块导航](#业务模块导航)
- [通用模块导航](#通用模块导航)
- [常用代码位置索引](#常用代码位置索引)
- [与 FoxDen 新项目对应关系](#与-foxden-新项目对应关系)

---

## 项目概述

### 技术栈
- **语言**: Java
- **框架**: Spring Boot 3.x
- **构建工具**: Maven
- **ORM**: MyBatis-Plus
- **数据库**: PostgreSQL
- **安全**: Sa-Token
- **缓存**: Redis (Redisson)

### 项目结构
```
old-version/
├── ruoyi-admin/          # 管理后台应用（启动入口）
├── ruoyi-common/         # 通用模块
│   ├── ruoyi-common-bom/    # BOM 依赖管理
│   ├── ruoyi-common-core/   # 核心工具类
│   ├── ruoyi-common-mybatis/# MyBatis-Plus 封装
│   ├── ruoyi-common-satoken/# Sa-Token 封装
│   ├── ruoyi-common-redis/  # Redis 缓存
│   ├── ruoyi-common-sse/    # SSE 服务端推送
│   └── ...其他通用模块
└── ruoyi-modules/        # 业务模块
    ├── ruoyi-system/      # 系统管理模块
    ├── ruoyi-job/         # 定时任务模块
    ├── ruoyi-workflow/    # 工作流模块
    └── ruoyi-demo/         # 示例模块
```

---

## 模块结构

### 1. ruoyi-admin（管理后台）
**路径**: `/old-version/ruoyi-admin/`

**功能**: 主应用程序，包含认证、登录、注册等核心功能

**关键文件**:
```
ruoyi-admin/src/main/java/org/dromara/
├── DromaraApplication.java           # 启动类
├── web/
│   ├── controller/                   # 控制器
│   │   ├── AuthController.java      # 认证控制器
│   │   ├── CaptchaController.java   # 验证码控制器
│   │   └── IndexController.java     # 首页控制器
│   ├── service/                      # 服务层
│   │   ├── SysLoginService.java     # 登录服务
│   │   ├── SysRegisterService.java  # 注册服务
│   │   └── impl/                    # 服务实现
│   │       ├── PasswordAuthStrategy.java    # 密码登录策略
│   │       ├── SmsAuthStrategy.java        # 短信登录策略
│   │       ├── EmailAuthStrategy.java      # 邮箱登录策略
│   │       ├── SocialAuthStrategy.java    # 社交登录策略
│   │       └── XcxAuthStrategy.java        # 小程序登录策略
│   ├── listener/                     # 监听器
│   │   └── UserActionListener.java  # 用户行为监听器（登录/退出）
│   └── domain/vo/                     # 视图对象
│       ├── LoginVo.java             # 登录响应VO
│       ├── CaptchaVo.java           # 验证码VO
│       └── LoginTenantVo.java       # 租户登录VO
```

---

### 2. ruoyi-modules/ruoyi-system（系统管理模块）
**路径**: `/old-version/ruoyi-modules/ruoyi-system/`

**功能**: 用户、角色、菜单、部门等系统管理功能

**控制器映射**:
```
system/controller/system/
├── SysUserController.java        # 用户管理
├── SysRoleController.java        # 角色管理
├── SysMenuController.java        # 菜单管理
├── SysDeptController.java        # 部门管理
├── SysPostController.java        # 岗位管理
├── SysTenantController.java     # 租户管理
├── SysTenantPackageController.java  # 租户套餐管理
├── SysConfigController.java     # 参数配置
├── SysDictTypeController.java   # 字典类型
├── SysDictDataController.java   # 字典数据
├── SysNoticeController.java     # 通知公告
├── SysClientController.java     # 客户端管理
├── SysOssController.java        # 对象存储管理
├── SysOssConfigController.java # OSS配置
├── SysSocialController.java     # 社交账号管理
└── SysProfileController.java   # 个人信息

system/controller/monitor/
├── SysOperLogController.java    # 操作日志
├── SysLogininforController.java # 登录日志
└── SysUserOnlineController.java # 在线用户
```

**服务层**:
```
system/service/
├── ISysUserService.java         # 用户服务接口
├── ISysRoleService.java         # 角色服务接口
├── ISysMenuService.java         # 菜单服务接口
├── ISysDeptService.java         # 部门服务接口
├── ISysPostService.java         # 岗位服务接口
├── ISysTenantService.java       # 租户服务接口
├── ISysPermissionService.java   # 权限服务接口
└── impl/                        # 服务实现
    ├── SysUserServiceImpl.java
    ├── SysRoleServiceImpl.java
    ├── SysMenuServiceImpl.java
    └── ...
```

**数据访问层（Mapper）**:
```
system/mapper/
├── SysUserMapper.java           # 用户Mapper
├── SysRoleMapper.java           # 角色Mapper
├── SysMenuMapper.java           # 菜单Mapper
├── SysDeptMapper.java           # 部门Mapper
├── SysPostMapper.java           # 岗位Mapper
├── SysTenantMapper.java         # 租户Mapper
└── ...
```

**实体类（Domain）**:
```
system/domain/
├── SysUser.java                 # 用户实体
├── SysRole.java                 # 角色实体
├── SysMenu.java                 # 菜单实体
├── SysDept.java                 # 部门实体
├── SysPost.java                 # 岗位实体
├── SysTenant.java               # 租户实体
├── SysConfig.java               # 配置实体
├── SysDictType.java             # 字典类型实体
├── SysDictData.java             # 字典数据实体
├── SysNotice.java               # 通知公告实体
├── SysOss.java                  # OSS实体
├── SysSocial.java               # 社交账号实体
└── ...
```

---

### 3. ruoyi-modules/ruoyi-workflow（工作流模块）
**路径**: `/old-version/ruoyi-modules/ruoyi-workflow/`

**功能**: 工作流引擎集成（Flowable）

**关键包**:
```
ruoyi-workflow/src/main/java/org/dromara/workflow/
├── controller/                  # 工作流控制器
├── service/                     # 工作流服务
├── domain/                      # 工作流实体
└── listener/                    # 工作流监听器
```

---

### 4. ruoyi-modules/ruoyi-job（定时任务模块）
**路径**: `/old-version/ruoyi-modules/ruoyi-job/`

**功能**: 定时任务调度

**关键文件**:
```
ruoyi-job/src/main/java/org/dromara/job/
├── controller/
│   └── SysJobController.java    # 定时任务控制器
├── service/
│   ├── ISysJobService.java      # 任务服务接口
│   └── impl/
│       └── SysJobServiceImpl.java
└── task/                       # 定时任务定义
```

---

## 通用模块导航

### 1. ruoyi-common-core（核心模块）
**路径**: `/old-version/ruoyi-common/ruoyi-common-core/`

**功能**: 核心工具类、常量、异常、注解

**关键包**:
```
ruoyi-common-core/src/main/java/org/dromara/common/core/
├── constant/                    # 常量定义
│   ├── CacheConstants.java      # 缓存常量
│   ├── Constants.java            # 通用常量
│   ├── HttpStatus.java           # HTTP状态码
│   ├── TenantConstants.java      # 租户常量
│   └── ...
├── domain/                      # 通用领域对象
│   ├── dto/                     # 数据传输对象
│   │   ├── LoginUser.java        # 登录用户信息
│   │   ├── UserDTO.java          # 用户DTO
│   │   └── ...
│   └── model/                   # 业务模型
├── exception/                   # 自定义异常
│   ├── ServiceException.java     # 服务异常
│   ├── ServiceException.java     # 业务异常
│   └── ...
├── utils/                       # 工具类
│   ├── SpringUtils.java         # Spring工具类
│   ├── StringUtils.java         # 字符串工具
│   ├── ServletUtils.java        # Servlet工具类
│   ├── DateUtils.java           # 日期工具
│   └── ...
├── validate/                    # 校验注解
│   └── annotation/
│       ├── EnumPattern.java     # 枚举校验
│       └── DictPattern.java     # 字典校验
└── enums/                       # 枚举定义
```

---

### 2. ruoyi-common-mybatis（MyBatis-Plus模块）
**路径**: `/old-version/ruoyi-common/ruoyi-common-mybatis/`

**功能**: MyBatis-Plus 封装，数据权限、分页等

**关键类**:
```
ruoyi-common-mybatis/src/main/java/org/dromara/common/mybatis/core/
├── mapper/BaseMapperPlus.java           # 基础Mapper接口
├── page/PageQuery.java                  # 分页查询参数
├── page/PageUtils.java                  # 分页工具类
└── page/TableDataInfo.java             # 分页响应对象
```

---

### 3. ruoyi-common-satoken（Sa-Token模块）
**路径**: `/old-version/ruoyi-common/ruoyi-common-satoken/`

**功能**: Sa-Token 权限认证封装

**关键类**:
```
ruoyi-common-satoken/src/main/java/org/dromara/common/satoken/
├── utils/
│   ├── LoginHelper.java              # 登录助手
│   └── SaTokenLoginRunnable.java     # 异步登录处理
└── config/
    └── SaTokenConfig.java            # Sa-Token配置
```

---

### 4. ruoyi-common-redis（Redis模块）
**路径**: `/old-version/ruoyi-common/ruoyi-common-redis/`

**功能**: Redis 缓存封装

**关键类**:
```
ruoyi-common-redis/src/main/java/org/dromara/common/redis/
├── utils/
│   ├── RedisUtils.java              # Redis工具类
│   └── CacheUtils.java              # 缓存工具类
└── config/
    └── RedisConfig.java             # Redis配置
```

---

### 5. ruoyi-common-sse（SSE模块）
**路径**: `/old-version/ruoyi-common/ruoyi-common-sse/`

**功能**: Server-Sent Events 服务端推送

**关键类**:
```
ruoyi-common-sse/src/main/java/org/dromara/common/sse/
├── controller/
│   └── SseController.java           # SSE控制器
├── core/
│   └── SseEmitterManager.java       # SSE连接管理器
├── listener/
│   └── SseTopicListener.java        # SSE主题监听器
└── dto/
    ├── SseMessage.java              # SSE消息接口
    └── SseMessageDefaultDto.java     # 默认消息DTO
```

---

### 6. 其他通用模块

| 模块 | 路径 | 功能 |
|------|------|------|
| ruoyi-common-log | `/old-version/ruoyi-common/ruoyi-common-log/` | 日志记录、操作日志、登录日志 |
| ruoyi-common-excel | `/old-version/ruoyi-common/ruoyi-common-excel/` | Excel导入导出 |
| ruoyi-common-mail | `/old-version/ruoyi-common/ruoyi-common-mail/` | 邮件发送 |
| ruoyi-common-sms | `/old-version/ruoyi-common/ruoyi-common-sms/` | 短信发送 |
| ruoyi-common-oss | `/old-version/ruoyi-common/ruoyi-common-oss/` | 对象存储管理 |
| ruoyi-common-social | `/old-version/ruoyi-common/ruoyi-common-social/` | 第三方登录（JustAuth） |
| ruoyi-common-encrypt | `/old-version/ruoyi-common/ruoyi-common-encrypt/` | 加密解密 |
| ruoyi-common-ratelimiter | `/old-version/ruoyi-common/ruoyi-common-ratelimiter/` | 限流 |
| ruoyi-common-idempotent | `/old-version/ruoyi-common/ruoyi-common-idempotent/` | 幂等性 |
| ruoyi-common-json | `/old-version/ruoyi-common/ruoyi-common-json/` | JSON处理 |
| ruoyi-common-doc | `/old-version/ruoyi-common/ruoyi-common-doc/` | 接口文档（Knife4j） |

---

## 常用代码位置索引

### 1. 登录认证相关
**功能位置映射**:
| 功能 | 旧项目位置 | 新项目位置 |
|------|-----------|-----------|
| 登录控制器 | `ruoyi-admin/web/controller/AuthController.java` | `foxden-app-admin/controller/AuthController.kt` |
| 登录服务 | `ruoyi-admin/web/service/SysLoginService.java` | `foxden-app-admin/service/SysLoginService.kt` |
| 密码登录策略 | `ruoyi-admin/web/service/impl/PasswordAuthStrategy.java` | `foxden-app-admin/service/impl/PasswordAuthStrategy.kt` |
| 短信登录策略 | `ruoyi-admin/web/service/impl/SmsAuthStrategy.java` | `foxden-app-admin/service/impl/SmsAuthStrategy.kt` |
| 邮箱登录策略 | `ruoyi-admin/web/service/impl/EmailAuthStrategy.java` | `foxden-app-admin/service/impl/EmailAuthStrategy.kt` |
| 社交登录策略 | `ruoyi-admin/web/service/impl/SocialAuthStrategy.java` | `foxden-app-admin/service/impl/SocialAuthStrategy.kt` |
| 小程序登录策略 | `ruoyi-admin/web/service/impl/XcxAuthStrategy.java` | `foxden-app-admin/service/impl/XcxAuthStrategy.kt` |
| 用户行为监听器 | `ruoyi-admin/web/listener/UserActionListener.java` | `foxden-app-admin/listener/UserActionListener.kt` |
| 验证码控制器 | `ruoyi-admin/web/controller/CaptchaController.java` | `foxden-app-admin/controller/CaptchaController.kt` |

**搜索命令**:
```bash
# 查找登录相关代码
find /mnt/f/idea_projects/FoxDen/old-version/ruoyi-admin/src/main/java -name "*Auth*" -o -name "*Login*"
grep -rn "login" /mnt/f/idea_projects/FoxDen/old-version/ruoyi-admin/src/main/java/org/dromara/web/controller/
```

---

### 2. 用户管理相关
**功能位置映射**:
| 功能 | 旧项目位置 | 新项目位置 |
|------|-----------|-----------|
| 用户控制器 | `ruoyi-modules/ruoyi-system/.../controller/system/SysUserController.java` | `foxden-app-system/controller/SysUserController.kt` |
| 用户服务 | `ruoyi-modules/ruoyi-system/.../service/ISysUserService.java` | `foxden-domain-system/service/SysUserService.kt` |
| 用户实体 | `ruoyi-modules/ruoyi-system/.../domain/SysUser.java` | `foxden-domain-system/entity/SysUser.kt` |
| 用户Mapper | `ruoyi-modules/ruoyi-system/.../mapper/SysUserMapper.java` | (Jimmer替代，无Mapper) |
| 用户VO | `ruoyi-modules/ruoyi-system/.../domain/vo/SysUserVo.java` | `foxden-domain-system/vo/SysUserVo.kt` |
| 用户BO | `ruoyi-modules/ruoyi-system/.../domain/bo/SysUserBo.java` | `foxden-domain-system/bo/SysUserBo.kt` |

**搜索命令**:
```bash
# 查找用户管理代码
find /mnt/f/idea_projects/FoxDen/old-version/ruoyi-modules/ruoyi-system -name "*User*Controller.java"
find /mnt/f/idea_projects/FoxDen/old-version/ruoyi-modules/ruoyi-system -name "*User*Service.java"
find /mnt/f/idea_projects/FoxDen/old-version/ruoyi-modules/ruoyi-system -name "*User*Mapper.java"
```

---

### 3. 角色权限相关
**功能位置映射**:
| 功能 | 旧项目位置 | 新项目位置 |
|------|-----------|-----------|
| 角色控制器 | `.../controller/system/SysRoleController.java` | `foxden-app-system/controller/SysRoleController.kt` |
| 角色服务 | `.../service/ISysRoleService.java` | `foxden-domain-system/service/SysRoleService.kt` |
| 角色实体 | `.../domain/SysRole.java` | `foxden-domain-system/entity/SysRole.kt` |
| 菜单控制器 | `.../controller/system/SysMenuController.java` | `foxden-app-system/controller/SysMenuController.kt` |
| 菜单服务 | `.../service/ISysMenuService.java` | `foxden-domain-system/service/SysMenuService.kt` |
| 菜单实体 | `.../domain/SysMenu.java` | `foxden-domain-system/entity/SysMenu.kt` |
| 权限服务 | `.../service/ISysPermissionService.java` | `foxden-domain-system/service/SysPermissionService.kt` |

**搜索命令**:
```bash
# 查找角色权限代码
find /mnt/f/idea_projects/FoxDen/old-version/ruoyi-modules/ruoyi-system -name "*Role*" -o -name "*Menu*"
grep -rn "permission" /mnt/f/idea_projects/FoxDen/old-version/ruoyi-modules/ruoyi-system/src/main/java/org/dromara/system/service/
```

---

### 4. 租户管理相关
**功能位置映射**:
| 功能 | 旧项目位置 | 新项目位置 |
|------|-----------|-----------|
| 租户控制器 | `.../controller/system/SysTenantController.java` | `foxden-app-system/controller/SysTenantController.kt` |
| 租户服务 | `.../service/ISysTenantService.java` | `foxden-domain-tenant/service/SysTenantService.kt` |
| 租户实体 | `.../domain/SysTenant.java` | `foxden-domain-tenant/entity/SysTenant.kt` |
| 租户套餐 | `.../controller/system/SysTenantPackageController.java` | (待实现) |

**搜索命令**:
```bash
# 查找租户管理代码
find /mnt/f/idea_projects/FoxDen/old-version -name "*Tenant*"
```

---

### 5. 部门组织相关
**功能位置映射**:
| 功能 | 旧项目位置 | 新项目位置 |
|------|-----------|-----------|
| 部门控制器 | `.../controller/system/SysDeptController.java` | `foxden-app-system/controller/SysDeptController.kt` |
| 岗位控制器 | `.../controller/system/SysPostController.java` | `foxden-app-system/controller/SysPostController.kt` |
| 部门服务 | `.../service/ISysDeptService.java` | `foxden-domain-system/service/SysDeptService.kt` |
| 岗位服务 | `.../service/ISysPostService.java` | `foxden-domain-system/service/SysPostService.kt` |

**搜索命令**:
```bash
# 查找部门组织代码
find /mnt/f/idea_projects/FoxDen/old-version/ruoyi-modules/ruoyi-system -name "*Dept*" -o -name "*Post*"
```

---

### 6. 系统配置相关
**功能位置映射**:
| 功能 | 旧项目位置 | 新项目位置 |
|------|-----------|-----------|
| 参数配置 | `.../controller/system/SysConfigController.java` | `foxden-app-system/controller/SysConfigController.kt` |
| 字典管理 | `.../controller/system/SysDictTypeController.java` | `foxden-app-system/controller/SysDictTypeController.kt` |
| 字典数据 | `.../controller/system/SysDictDataController.java` | `foxden-app-system/controller/SysDictDataController.kt` |
| 通知公告 | `.../controller/system/SysNoticeController.java` | `foxden-app-system/controller/SysNoticeController.kt` |

**搜索命令**:
```bash
# 查找配置相关代码
find /mnt/f/idea_projects/FoxDen/old-version/ruoyi-modules/ruoyi-system -name "*Config*" -o -name "*Dict*"
```

---

### 7. 日志监控相关
**功能位置映射**:
| 功能 | 旧项目位置 | 新项目位置 |
|------|-----------|-----------|
| 操作日志 | `.../controller/monitor/SysOperLogController.java` | (待实现) |
| 登录日志 | `.../controller/monitor/SysLogininforController.java` | (待实现) |
| 在线用户 | `.../controller/monitor/SysUserOnlineController.java` | (待实现) |

**搜索命令**:
```bash
# 查找日志相关代码
find /mnt/f/idea_projects/FoxDen/old-version/ruoyi-modules/ruoyi-system -path "*/monitor/*"
```

---

### 8. 文件存储相关
**功能位置映射**:
| 功能 | 旧项目位置 | 新项目位置 |
|------|-----------|-----------|
| OSS管理 | `.../controller/system/SysOssController.java` | `foxden-app-system/controller/SysOssController.kt` |
| OSS配置 | `.../controller/system/SysOssConfigController.java` | `foxden-app-system/controller/SysOssConfigController.kt` |
| OSS服务 | `ruoyi-common/ruoyi-common-oss/` | `foxden-common-oss/` |

**搜索命令**:
```bash
# 查找OSS相关代码
find /mnt/f/idea_projects/FoxDen/old-version/ruoyi-common/ruoyi-common-oss -name "*.java"
find /mnt/f/idea_projects/FoxDen/old-version/ruoyi-modules/ruoyi-system -name "*Oss*"
```

---

## 与 FoxDen 新项目对应关系

### 模块映射表

| 旧项目模块 | 新项目模块 | 状态 | 说明 |
|-----------|-----------|------|------|
| `ruoyi-admin` | `foxden-app-admin` | ✅ 已迁移 | 管理后台应用 |
| `ruoyi-modules/ruoyi-system` | `foxden-app-system` + `foxden-domain-system` | ✅ 已迁移 | 系统管理模块 |
| `ruoyi-modules/ruoyi-tenant` | `foxden-domain-tenant` | ✅ 已迁移 | 租户管理模块 |
| `ruoyi-common/ruoyi-common-core` | `foxden-common-core` | ✅ 已迁移 | 核心工具类 |
| `ruoyi-common/ruoyi-common-mybatis` | `foxden-common-jimmer` | ✅ 已替换 | ORM（Jimmer替代MyBatis） |
| `ruoyi-common/ruoyi-common-satoken` | `foxden-common-security` | ✅ 已迁移 | Sa-Token安全认证 |
| `ruoyi-common/ruoyi-common-redis` | `foxden-common-redis` | ✅ 已迁移 | Redis缓存 |
| `ruoyi-common/ruoyi-common-sse` | `foxden-common-sse` | ✅ 已迁移 | SSE推送 |
| `ruoyi-common/ruoyi-common-log` | `foxden-common-log` | ✅ 已迁移 | 日志记录 |
| `ruoyi-common/ruoyi-common-excel` | `foxden-common-excel` | ✅ 已迁移 | Excel处理 |
| `ruoyi-common/ruoyi-common-mail` | `foxden-common-mail` | ✅ 已迁移 | 邮件发送 |
| `ruoyi-common/ruoyi-common-sms` | `foxden-common-sms` | ✅ 已迁移 | 短信发送 |
| `ruoyi-common/ruoyi-common-social` | `foxden-common-social` | ✅ 已迁移 | 社交登录 |
| `ruoyi-common/ruoyi-common-encrypt` | `foxden-common-encrypt` | ✅ 已迁移 | 加密解密 |
| `ruoyi-common/ruoyi-common-ratelimiter` | `foxden-common-ratelimiter` | ✅ 已迁移 | 限流 |
| `ruoyi-common/ruoyi-common-idempotent` | `foxden-common-idempotent` | ✅ 已迁移 | 幂等性 |
| `ruoyi-common/ruoyi-common-json` | `foxden-common-json` | ✅ 已迁移 | JSON处理 |
| `ruoyi-common/ruoyi-common-oss` | `foxden-common-oss` | ✅ 已迁移 | 对象存储 |
| `ruoyi-common/ruoyi-common-doc` | `foxden-common-doc` | ✅ 已迁移 | 接口文档 |
| `ruoyi-modules/ruoyi-workflow` | - | ⏳ 待迁移 | 工作流模块 |
| `ruoyi-modules/ruoyi-job` | - | ⏳ 待迁移 | 定时任务模块 |

---

## 快速搜索技巧

### 按功能搜索
```bash
# 搜索登录认证相关
find /mnt/f/idea_projects/FoxDen/old-version -name "*Auth*" -o -name "*Login*"

# 搜索用户管理
find /mnt/f/idea_projects/FoxDen/old-version -name "*User*Controller.java"

# 搜索角色权限
find /mnt/f/idea_projects/FoxDen/old-version -name "*Role*" -o -name "*Menu*"

# 搜索租户管理
find /mnt/f/idea_projects/FoxDen/old-version -name "*Tenant*"

# 搜索部门组织
find /mnt/f/idea_projects/FoxDen/old-version -name "*Dept*" -o -name "*Post*"
```

### 按文件类型搜索
```bash
# 搜索控制器
find /mnt/f/idea_projects/FoxDen/old-version -name "*Controller.java"

# 搜索服务接口
find /mnt/f/idea_projects/FoxDen/old-version -name "I*Service.java"

# 搜索服务实现
find /mnt/f/idea_projects/FoxDen/old-version -name "*ServiceImpl.java"

# 搜索Mapper
find /mnt/f/idea_projects/FoxDen/old-version -name "*Mapper.java"

# 搜索实体类
find /mnt/f/idea_projects/FoxDen/old-version -name "*.java" -path "*/domain/*"

# 搜索VO
find /mnt/f/idea_projects/FoxDen/old-version -name "*Vo.java"

# 搜索BO
find /mnt/f/idea_projects/FoxDen/old-version -name "*Bo.java"
```

### 内容搜索
```bash
# 在控制器中搜索特定方法
grep -rn "login" /mnt/f/idea_projects/FoxDen/old-version/ruoyi-admin/src/main/java/org/dromara/web/controller/

# 在服务中搜索特定逻辑
grep -rn "deleteUser" /mnt/f/idea_projects/FoxDen/old-version/ruoyi-modules/ruoyi-system/src/main/java/

# 搜索特定注解
grep -rn "@SaIgnore\|@Log\|@Transactional" /mnt/f/idea_projects/FoxDen/old-version/ruoyi-admin/src/main/java/
```

---

## 常见业务场景代码定位

### 场景1：实现用户登录功能
**旧项目参考**:
```
1. 登录控制器: ruoyi-admin/web/controller/AuthController.java
2. 登录服务: ruoyi-admin/web/service/SysLoginService.java
3. 密码验证: ruoyi-admin/web/service/impl/PasswordAuthStrategy.java
4. 用户服务: ruoyi-modules/ruoyi-system/.../service/ISysUserService.java
5. 用户Mapper: ruoyi-modules/ruoyi-system/.../mapper/SysUserMapper.java
```

### 场景2：实现角色权限管理
**旧项目参考**:
```
1. 角色控制器: ruoyi-modules/ruoyi-system/.../controller/system/SysRoleController.java
2. 角色服务: ruoyi-modules/ruoyi-system/.../service/ISysRoleService.java
3. 权限服务: ruoyi-modules/ruoyi-system/.../service/ISysPermissionService.java
4. 角色菜单关联: 查询 sys_role_menu 表
```

### 场景3：实现数据权限过滤
**旧项目参考**:
```
1. 数据权限注解: ruoyi-common/ruoyi-common-mybatis/.../annotation/DataPermission.java
2. 数据权限工具: ruoyi-common/ruoyi-common-mybatis/.../utils/DataPermissionUtils.java
3. 部门数据权限: 查询 sys_role_dept 表
```

### 场景4：实现租户隔离
**旧项目参考**:
```
1. 租户控制器: ruoyi-modules/ruoyi-system/.../controller/system/SysTenantController.java
2. 租户服务: ruoyi-modules/ruoyi-system/.../service/ISysTenantService.java
3. 租户工具: ruoyi-common/ruoyi-common-satoken/.../utils/LoginHelper.java
4. 租户常量: ruoyi-common/ruoyi-common-core/.../constant/TenantConstants.java
```

### 场景5：实现操作日志记录
**旧项目参考**:
```
1. 日志注解: ruoyi-common/ruoyi-common-log/.../annotation/Log.java
2. 日志事件: ruoyi-common/ruoyi-common-log/.../event/OperLogEvent.java
3. 异步监听: ruoyi-common/ruoyi-common-log/.../listener/OperLogListener.java
4. 操作日志控制器: ruoyi-modules/ruoyi-system/.../controller/monitor/SysOperLogController.java
5. 操作日志服务: ruoyi-modules/ruoyi-system/.../service/SysOperLogService.java
```

---

## 注意事项

### 1. 包名差异
- **旧项目**: `org.dromara.*`
- **新项目**: `com.github.alphafoxz.foxden.*`

### 2. 技术栈差异
| 方面 | 旧项目 | 新项目 |
|------|--------|--------|
| ORM | MyBatis-Plus | Jimmer |
| 语言 | Java | Kotlin |
| 构建工具 | Maven | Gradle (Kotlin DSL) |
| 代码生成 | MyBatis Generator | KSP (Jimmer) |

### 3. 文件组织差异
- **旧项目**: `domain/` 实体 + `vo/` + `bo/` 分离
- **新项目**: `entity/` 实体 + `vo/` + `bo/` 类似

### 4. 查询方式差异
- **旧项目**: MyBatis XML 或注解
- **新项目**: Jimmer DSL (类型安全)

---

## 更新日志

- **2026-02-10**: 初始版本，创建项目结构导航文档
- 包含完整模块映射和代码位置索引
- 添加快速搜索技巧和常见业务场景定位

---

**提示**: 使用此文档时，记得始终在 `old-version/` 目录内搜索，不要到上级目录搜索。
