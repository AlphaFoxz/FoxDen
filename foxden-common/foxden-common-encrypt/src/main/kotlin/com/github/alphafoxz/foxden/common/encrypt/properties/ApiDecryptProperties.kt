package com.github.alphafoxz.foxden.common.encrypt.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * API解密属性配置类
 *
 * @author wdhcr
 */
@ConfigurationProperties(prefix = "api-decrypt")
data class ApiDecryptProperties(
    /**
     * 加密开关
     */
    var enabled: Boolean? = null,

    /**
     * 头部标识
     */
    var headerFlag: String? = null,

    /**
     * 响应加密公钥
     */
    var publicKey: String? = null,

    /**
     * 请求解密私钥
     */
    var privateKey: String? = null
)
