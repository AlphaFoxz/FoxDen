# FoxDen Common Social

FoxDen 社交登录模块，基于 JustAuth 实现。

## 功能特性

- ✅ 支持多种第三方登录平台
- ✅ 自动配置支持
- ✅ Redis状态缓存
- ✅ 扩展的AuthRequest实现

## 支持的社交平台

| 平台 | 标识 | 说明 |
|-----|------|------|
| 钉钉 | dingtalk | 钉钉v2登录 |
| 百度 | baidu | 百度登录 |
| GitHub | github | GitHub登录 |
| Gitee | gitee | Gitee登录 |
| 微博 | weibo | 新浪微博登录 |
| Coding | coding | Coding登录 |
| OSChina | oschina | OSChina登录 |
| 支付宝 | alipay_wallet | 支付宝钱包登录 |
| QQ | qq | QQ登录 |
| 微信开放平台 | wechat_open | 微信开放平台登录 |
| 淘宝 | taobao | 淘宝登录 |
| 抖音 | douyin | 抖音登录 |
| 领英 | linkedin | 领英登录 |
| 微软 | microsoft | 微软登录 |
| 人人 | renren | 人人网登录 |
| Stack Overflow | stack_overflow | Stack Overflow登录 |
| 华为 | huawei | 华为登录(v3) |
| 企业微信 | wechat_enterprise | 企业微信登录 |
| GitLab | gitlab | GitLab登录 |
| 微信公众号 | wechat_mp | 微信公众号登录 |
| 阿里云 | aliyun | 阿里云登录 |
| MaxKey | maxkey | MaxKey登录 |
| TopIam | topiam | TopIam登录 |
| Gitea | gitea | Gitea私有部署登录 |

## 配置说明

### application.yaml

```yaml
justauth:
  type:
    gitee:
      client-id: your-client-id
      client-secret: your-client-secret
      redirect-uri: http://localhost:8080/callback
      scopes:
        - user_info
        - emails
    github:
      client-id: your-client-id
      client-secret: your-client-secret
      redirect-uri: http://localhost:8080/callback
```

### 配置属性说明

| 属性 | 说明 | 必填 |
|-----|------|------|
| clientId | 应用ID | 是 |
| clientSecret | 应用密钥 | 是 |
| redirectUri | 回调地址 | 是 |
| unionId | 是否获取unionId | 否 |
| scopes | 请求范围 | 否 |

## 使用方式

### 1. 引入依赖

```kotlin
implementation(project(":foxden-common:foxden-common-social"))
```

### 2. 注入配置

```kotlin
@Autowired
private lateinit var socialProperties: SocialProperties
```

### 3. 使用工具类

```kotlin
// 进行社交登录授权
val response = SocialUtils.loginAuth(
    source = "gitee",
    code = authCode,
    state = state,
    socialProperties = socialProperties
)

val authUser = response.data
```

### 4. 获取AuthRequest

```kotlin
val authRequest = SocialUtils.getAuthRequest(
    source = "gitee",
    socialProperties = socialProperties
)
```

## 扩展支持

本模块提供了以下扩展的AuthRequest实现：

- **AuthGiteaRequest** - Gitea私有部署支持
- **AuthMaxKeyRequest** - MaxKey登录支持
- **AuthTopIamRequest** - TopIam登录支持

这些扩展支持自定义服务器地址等高级配置。

## 自动配置

模块包含自动配置类 `SocialAutoConfiguration`，会自动配置：
- `AuthStateCache` - 使用Redis缓存授权状态
- `SocialProperties` - 社交登录配置属性

## 技术栈

- **JustAuth** 1.16.7 - 社交登录核心库
- **Spring Boot** 3.4.1 - 自动配置支持
- **Redis (Redisson)** - 状态缓存
- **Hutool** 5.8.43 - HTTP请求工具

## 注意事项

1. 回调地址需要在各社交平台配置白名单
2. 不同平台的scope配置不同，请参考各平台文档
3. Redis缓存用于存储授权状态，默认超时3分钟
4. 部分平台需要额外配置（如支付宝公钥、企业微信agentId等）

## 许可证

Copyright © 2025 FoxDen Team
