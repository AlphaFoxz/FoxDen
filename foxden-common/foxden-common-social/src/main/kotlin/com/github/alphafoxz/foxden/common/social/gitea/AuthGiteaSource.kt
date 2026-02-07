package com.github.alphafoxz.foxden.common.social.gitea

import me.zhyd.oauth.config.AuthSource
import me.zhyd.oauth.request.AuthDefaultRequest

/**
 * Gitea Oauth2 默认接口说明
 *
 * @author FoxDen Team
 */
enum class AuthGiteaSource : AuthSource {

    /**
     * 自己搭建的 gitea 私服
     */
    GITEA {
        /**
         * 授权的api
         */
        override fun authorize(): String {
            return AuthGiteaRequest.SERVER_URL + "/login/oauth/authorize"
        }

        /**
         * 获取accessToken的api
         */
        override fun accessToken(): String {
            return AuthGiteaRequest.SERVER_URL + "/login/oauth/access_token"
        }

        /**
         * 获取用户信息的api
         */
        override fun userInfo(): String {
            return AuthGiteaRequest.SERVER_URL + "/login/oauth/userinfo"
        }

        /**
         * 平台对应的 AuthRequest 实现类，必须继承自 {@link AuthDefaultRequest}
         */
        override fun getTargetClass(): Class<out AuthDefaultRequest> {
            return AuthGiteaRequest::class.java
        }
    }
}
