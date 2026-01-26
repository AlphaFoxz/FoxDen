package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 系统授权
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_client")
interface SysClient : CommDelFlag, CommId, CommInfo {
    /**
     * 客户端id
     */
    @Column(name = "client_id")
    @get:Length(max = 2147483647)
    val clientId: String

    /**
     * 客户端key
     */
    @Column(name = "client_key")
    @get:Length(max = 2147483647)
    val clientKey: String

    /**
     * 客户端秘钥
     */
    @Column(name = "client_secret")
    @get:Length(max = 2147483647)
    val clientSecret: String

    /**
     * 授权类型
     */
    @Column(name = "grant_type")
    @get:Length(max = 2147483647)
    val grantType: String

    /**
     * 设备类型
     */
    @Column(name = "device_type")
    @get:Length(max = 2147483647)
    val deviceType: String?

    /**
     * token活跃超时时间
     */
    @Column(name = "active_timeout")
    @get:Max(value = 2147483647, message = "token活跃超时时间不可大于2147483647")
    @get:Min(value = 0, message = "token活跃超时时间不可小于0")
    val activeTimeout: Int?

    /**
     * token固定超时
     */
    @Column(name = "timeout")
    @get:Max(value = 2147483647, message = "token固定超时不可大于2147483647")
    @get:Min(value = 0, message = "token固定超时不可小于0")
    val timeout: Int?

    /**
     * 状态（0正常 1停用）
     */
    @Column(name = "status")
    @get:Length(max = 1)
    val status: String
}
