package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 社会化关系对象 sys_social
 */
@Entity
interface SysSocial : CommId, CommDelFlag, CommInfo, CommTenant {
    /**
     * 用户ID
     */
    val userId: Long?

    /**
     * 的唯一ID
     */
    val authId: String?

    /**
     * 用户来源
     */
    val source: String?

    /**
     * 用户的授权令牌
     */
    val accessToken: String?

    /**
     * 用户的授权令牌的有效期，部分平台可能没有
     */
    val expireIn: Int?

    /**
     * 刷新令牌，部分平台可能没有
     */
    val refreshToken: String?

    /**
     * 用户的 open id
     */
    val openId: String?

    /**
     * 授权的第三方账号
     */
    val userName: String?

    /**
     * 授权的第三方昵称
     */
    val nickName: String?

    /**
     * 授权的第三方邮箱
     */
    val email: String?

    /**
     * 授权的第三方头像地址
     */
    val avatar: String?

    /**
     * 平台的授权信息，部分平台可能没有
     */
    val accessCode: String?

    /**
     * 用户的 unionid
     */
    val unionId: String?

    /**
     * 授予的权限，部分平台可能没有
     */
    val scope: String?

    /**
     * 个别平台的授权信息，部分平台可能没有
     */
    val tokenType: String?

    /**
     * id token，部分平台可能没有
     */
    val idToken: String?

    /**
     * 小米平台用户的附带属性，部分平台可能没有
     */
    val macAlgorithm: String?

    /**
     * 小米平台用户的附带属性，部分平台可能没有
     */
    val macKey: String?

    /**
     * 用户的授权code，部分平台可能没有
     */
    val code: String?

    /**
     * Twitter平台用户的附带属性，部分平台可能没有
     */
    val oauthToken: String?

    /**
     * Twitter平台用户的附带属性，部分平台可能没有
     */
    val oauthTokenSecret: String?
}
