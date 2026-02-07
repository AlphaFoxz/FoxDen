package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 参数配置视图对象 sys_config
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysConfigVo(
    /**
     * 参数主键
     */
    var configId: Long? = null,

    /**
     * 参数名称
     */
    var configName: String? = null,

    /**
     * 参数键名
     */
    var configKey: String? = null,

    /**
     * 参数键值
     */
    var configValue: String? = null,

    /**
     * 系统内置（0是 1否）
     */
    var configType: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
