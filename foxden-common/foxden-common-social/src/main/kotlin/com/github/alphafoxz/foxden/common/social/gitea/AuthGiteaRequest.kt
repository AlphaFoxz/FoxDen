package com.github.alphafoxz.foxden.common.social.gitea

import cn.hutool.core.lang.Dict
import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import me.zhyd.oauth.cache.AuthStateCache
import me.zhyd.oauth.config.AuthConfig
import me.zhyd.oauth.exception.AuthException
import me.zhyd.oauth.model.AuthCallback
import me.zhyd.oauth.model.AuthToken
import me.zhyd.oauth.model.AuthUser
import me.zhyd.oauth.request.AuthDefaultRequest
import org.slf4j.LoggerFactory

/**
 * Gitea 认证请求
 *
 * @author FoxDen Team
 */
class AuthGiteaRequest : AuthDefaultRequest {

    companion object {
        private val log = LoggerFactory.getLogger(AuthGiteaRequest::class.java)

        @JvmStatic
        val SERVER_URL: String = SpringUtils.getProperty("justauth.type.gitea.server-url", "")
    }

    constructor(config: AuthConfig) : super(config, AuthGiteaSource.GITEA)

    constructor(config: AuthConfig, authStateCache: AuthStateCache) : super(config, AuthGiteaSource.GITEA, authStateCache)

    override fun getAccessToken(authCallback: AuthCallback): AuthToken {
        val body = doPostAuthorizationCode(authCallback.code)
        val `object` = JsonUtils.parseMap(body)
        // oauth/token 验证异常
        if (`object`?.containsKey("error") == true) {
            throw AuthException(`object`?.getStr("error_description"))
        }
        // user 验证异常
        if (`object`?.containsKey("message") == true) {
            throw AuthException(`object`?.getStr("message"))
        }
        return AuthToken.builder()
            .accessToken(`object`?.getStr("access_token"))
            .refreshToken(`object`?.getStr("refresh_token"))
            .idToken(`object`?.getStr("id_token"))
            .tokenType(`object`?.getStr("token_type"))
            .scope(`object`?.getStr("scope"))
            .build()
    }

    override fun doPostAuthorizationCode(code: String): String {
        val request: HttpRequest = HttpRequest.post(source.accessToken())
            .form("client_id", config.clientId)
            .form("client_secret", config.clientSecret)
            .form("grant_type", "authorization_code")
            .form("code", code)
            .form("redirect_uri", config.redirectUri)
        val response: HttpResponse = request.execute()
        return response.body()
    }

    override fun getUserInfo(authToken: AuthToken): AuthUser {
        val body = doGetUserInfo(authToken)
        val `object` = JsonUtils.parseMap(body)
        // oauth/token 验证异常
        if (`object`?.containsKey("error") == true) {
            throw AuthException(`object`?.getStr("error_description"))
        }
        // user 验证异常
        if (`object`?.containsKey("message") == true) {
            throw AuthException(`object`?.getStr("message"))
        }
        return AuthUser.builder()
            .uuid(`object`?.getStr("sub"))
            .username(`object`?.getStr("name"))
            .nickname(`object`?.getStr("preferred_username"))
            .avatar(`object`?.getStr("picture"))
            .email(`object`?.getStr("email"))
            .token(authToken)
            .source(source.toString())
            .build()
    }
}
