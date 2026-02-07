package com.github.alphafoxz.foxden.common.core.domain.dto

import java.io.Serializable

/**
 * 字典类型DTO
 */
data class DictTypeDTO(
    /**
     * 字典主键
     */
    var dictId: Long? = null,

    /**
     * 字典名称
     */
    var dictName: String? = null,

    /**
     * 字典类型
     */
    var dictType: String? = null,

    /**
     * 备注
     */
    var remark: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
