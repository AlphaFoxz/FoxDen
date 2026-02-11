package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * 字典类型业务对象 sys_dict_type
 *
 * @author Lion Li
 */
data class SysDictTypeBo(
    /**
     * 字典主键
     */
    var dictId: Long? = null,

    /**
     * 字典名称
     */
    @get:NotBlank(message = "字典名称不能为空")
    @get:Size(min = 0, max = 100, message = "字典类型名称长度不能超过{max}个字符")
    var dictName: String? = null,

    /**
     * 字典类型
     */
    @get:NotBlank(message = "字典类型不能为空")
    @get:Size(min = 0, max = 100, message = "字典类型类型长度不能超过{max}个字符")
    @get:Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字典类型必须以小写字母开头")
    var dictType: String? = null,

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null,

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

