package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 参数配置业务对象 sys_config
 *
 * @author Lion Li
 */
data class SysConfigBo(
    /**
     * 参数主键
     */
    var configId: Long? = null,

    /**
     * 参数名称
     */
    @get:NotBlank(message = "参数名称不能为空")
    @get:Size(min = 0, max = 100, message = "参数名称长度不能超过{max}个字符")
    var configName: String? = null,

    /**
     * 参数键名
     */
    @get:NotBlank(message = "参数键名不能为空")
    @get:Size(min = 0, max = 100, message = "参数键名长度不能超过{max}个字符")
    var configKey: String? = null,

    /**
     * 参数键值
     */
    @get:NotBlank(message = "参数键值不能为空")
    @get:Size(min = 0, max = 500, message = "参数键值长度不能超过{max}个字符")
    var configValue: String? = null,

    /**
     * 系统内置（Y是 N否）
     */
    var configType: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
