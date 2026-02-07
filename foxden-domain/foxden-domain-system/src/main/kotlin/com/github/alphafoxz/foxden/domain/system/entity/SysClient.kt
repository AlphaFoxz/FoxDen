package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 授权管理对象 sys_client
 */
@Entity
interface SysClient : CommDelFlag, CommId, CommInfo {
    /**
     * 客户端id
     */
    val clientId: String

    /**
     * 客户端key
     */
    val clientKey: String

    /**
     * 客户端秘钥
     */
    val clientSecret: String?

    /**
     * 授权类型
     */
    val grantType: String?

    /**
     * 设备类型
     */
    val deviceType: String?

    /**
     * token活跃超时时间
     */
    val activeTimeout: Long?

    /**
     * token固定超时时间
     */
    val timeout: Long?

    /**
     * 状态（0正常 1停用）
     */
    val status: String?
}
