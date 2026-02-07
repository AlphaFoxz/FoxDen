package com.github.alphafoxz.foxden.common.social.utils

import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.social.config.properties.SocialLoginConfigProperties
import com.github.alphafoxz.foxden.common.social.config.properties.SocialProperties
import me.zhyd.oauth.config.AuthConfig
import me.zhyd.oauth.exception.AuthException
import me.zhyd.oauth.model.AuthCallback
import me.zhyd.oauth.model.AuthResponse
import me.zhyd.oauth.model.AuthUser
import me.zhyd.oauth.request.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 认证授权工具类
 *
 * @author FoxDen Team
 */
object SocialUtils {

    private val STATE_CACHE: AuthRedisStateCache by lazy {
        SpringUtils.getBean(AuthRedisStateCache::class.java)
    }

    /**
     * 社交登录授权
     *
     * @param source 第三方登录类型
     * @param code 授权码
     * @param state 状态
     * @param socialProperties 社交登录配置
     * @return AuthResponse
     * @throws AuthException
     */
    @JvmStatic
    @Throws(AuthException::class)
    fun loginAuth(
        source: String,
        code: String,
        state: String,
        socialProperties: SocialProperties
    ): AuthResponse<AuthUser> {
        val authRequest = getAuthRequest(source, socialProperties)
        val callback = AuthCallback()
        callback.code = code
        callback.state = state
        return authRequest.login(callback)
    }

    /**
     * 获取AuthRequest实例
     *
     * @param source 第三方登录类型
     * @param socialProperties 社交登录配置
     * @return AuthRequest
     * @throws AuthException
     */
    @JvmStatic
    @Throws(AuthException::class)
    fun getAuthRequest(source: String, socialProperties: SocialProperties): AuthRequest {
        val obj = socialProperties.type?.get(source)
            ?: throw AuthException("不支持的第三方登录类型")

        val builder = AuthConfig.builder()
            .clientId(obj.clientId)
            .clientSecret(obj.clientSecret)
            .redirectUri(URLEncoder.encode(obj.redirectUri, StandardCharsets.UTF_8))
            .scopes(obj.scopes)

        return when (source.lowercase()) {
            "dingtalk" -> AuthDingTalkV2Request(builder.build(), STATE_CACHE)
            "baidu" -> AuthBaiduRequest(builder.build(), STATE_CACHE)
            "github" -> AuthGithubRequest(builder.build(), STATE_CACHE)
            "gitee" -> AuthGiteeRequest(builder.build(), STATE_CACHE)
            "weibo" -> AuthWeiboRequest(builder.build(), STATE_CACHE)
            "coding" -> AuthCodingRequest(builder.build(), STATE_CACHE)
            "oschina" -> AuthOschinaRequest(builder.build(), STATE_CACHE)
            "alipay_wallet" -> AuthAlipayRequest(
                builder.build(),
                socialProperties.type?.get("alipay_wallet")?.alipayPublicKey,
                STATE_CACHE
            )
            "qq" -> AuthQqRequest(builder.build(), STATE_CACHE)
            "wechat_open" -> AuthWeChatOpenRequest(builder.build(), STATE_CACHE)
            "taobao" -> AuthTaobaoRequest(builder.build(), STATE_CACHE)
            "douyin" -> AuthDouyinRequest(builder.build(), STATE_CACHE)
            "linkedin" -> AuthLinkedinRequest(builder.build(), STATE_CACHE)
            "microsoft" -> AuthMicrosoftRequest(builder.build(), STATE_CACHE)
            "renren" -> AuthRenrenRequest(builder.build(), STATE_CACHE)
            "stack_overflow" -> {
                val key = obj.stackOverflowKey ?: throw AuthException("stack_overflow key is required")
                AuthStackOverflowRequest(builder.stackOverflowKey(key).build(), STATE_CACHE)
            }
            "huawei" -> AuthHuaweiV3Request(builder.build(), STATE_CACHE)
            "wechat_enterprise" -> AuthWeChatEnterpriseQrcodeV2Request(
                builder.agentId(obj.agentId).build(),
                STATE_CACHE
            )
            "gitlab" -> AuthGitlabRequest(builder.build(), STATE_CACHE)
            "wechat_mp" -> AuthWeChatMpRequest(builder.build(), STATE_CACHE)
            "aliyun" -> AuthAliyunRequest(builder.build(), STATE_CACHE)
            "gitea" -> com.github.alphafoxz.foxden.common.social.gitea.AuthGiteaRequest(
                builder.build(),
                STATE_CACHE
            )
            else -> throw AuthException("未获取到有效的Auth配置")
        }
    }
}
