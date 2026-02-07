package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 客户端管理视图对象 sys_client
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysClientVo(
    /**
     * 客户端ID
     */
    var id: Long? = null,

    /**
     * 客户端ID
     */
    var clientId: String? = null,

    /**
     * 客户端密钥
     */
    var clientKey: String? = null,

    /**
     * 客户端名称
     */
    var clientName: String? = null,

    /**
     * 客户端类型
     */
    var clientType: String? = null,

    /**
     * 授权类型
     */
    var grantType: String? = null,

    /**
     * 回调地址
     */
    var redirectUri: String? = null,

    /**
     * 授权自动登录
     */
    var autoApprove: Boolean? = null,

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 设备类型
     */
    var deviceType: String? = null,

    /**
     * Token 超时时间（秒）
     */
    var timeout: Long? = null,

    /**
     * Token 有效期（秒）
     */
    var activeTimeout: Long? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
