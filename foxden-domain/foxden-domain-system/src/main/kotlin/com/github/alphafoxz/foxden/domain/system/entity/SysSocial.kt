package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 社会化关系
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_social")
interface SysSocial : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 用户ID
     */
    @Column(name = "user_id")
    @get:Max(value = 9223372036854775807, message = "用户ID不可大于9223372036854775807")
    @get:Min(value = 0, message = "用户ID不可小于0")
    val userId: Long

    /**
     * 平台+平台唯一id
     */
    @Column(name = "auth_id")
    @get:Length(max = 2147483647)
    val authId: String?

    /**
     * 用户来源
     */
    @Column(name = "source")
    @get:Length(max = 2147483647)
    val source: String

    /**
     * 平台编号唯一id
     */
    @Column(name = "open_id")
    @get:Length(max = 2147483647)
    val openId: String

    /**
     * 登录账号
     */
    @Column(name = "user_name")
    @get:Length(max = 2147483647)
    val userName: String?

    /**
     * 用户昵称
     */
    @Column(name = "nick_name")
    @get:Length(max = 2147483647)
    val nickName: String?

    /**
     * 用户邮箱
     */
    @Column(name = "email")
    @get:Length(max = 2147483647)
    val email: String?

    /**
     * 头像地址
     */
    @Column(name = "avatar")
    @get:Length(max = 2147483647)
    val avatar: String?

    /**
     * 用户的授权令牌
     */
    @Column(name = "access_token")
    @get:Length(max = 2147483647)
    val accessToken: String?

    /**
     * 用户的授权令牌的有效期
     */
    @Column(name = "expire_in")
    @get:Max(value = 9223372036854775807, message = "用户的授权令牌的有效期不可大于9223372036854775807")
    @get:Min(value = 0, message = "用户的授权令牌的有效期不可小于0")
    val expireIn: Long?

    /**
     * 刷新令牌
     */
    @Column(name = "refresh_token")
    @get:Length(max = 2147483647)
    val refreshToken: String?

    /**
     * 平台的授权信息
     */
    @Column(name = "access_code")
    @get:Length(max = 2147483647)
    val accessCode: String?

    /**
     * 用户的 unionid
     */
    @Column(name = "union_id")
    @get:Length(max = 2147483647)
    val unionId: String?

    /**
     * 授予的权限
     */
    @Column(name = "scope")
    @get:Length(max = 2147483647)
    val scope: String?

    /**
     * 个别平台的授权信息
     */
    @Column(name = "token_type")
    @get:Length(max = 2147483647)
    val tokenType: String?

    /**
     * id token
     */
    @Column(name = "id_token")
    @get:Length(max = 2147483647)
    val idToken: String?

    /**
     * 小米平台用户的附带属性
     */
    @Column(name = "mac_algorithm")
    @get:Length(max = 2147483647)
    val macAlgorithm: String?

    /**
     * 小米平台用户的附带属性
     */
    @Column(name = "mac_key")
    @get:Length(max = 2147483647)
    val macKey: String?

    /**
     * 用户的授权code
     */
    @Column(name = "code")
    @get:Length(max = 2147483647)
    val code: String?

    /**
     * Twitter平台用户的附带属性
     */
    @Column(name = "oauth_token")
    @get:Length(max = 2147483647)
    val oauthToken: String?

    /**
     * Twitter平台用户的附带属性
     */
    @Column(name = "oauth_token_secret")
    @get:Length(max = 2147483647)
    val oauthTokenSecret: String?
}
