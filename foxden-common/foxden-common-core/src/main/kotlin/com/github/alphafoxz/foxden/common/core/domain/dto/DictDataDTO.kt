package com.github.alphafoxz.foxden.common.core.domain.dto

import java.io.Serializable

/**
 * 字典数据DTO
 */
data class DictDataDTO(
    /**
     * 字典标签
     */
    var dictLabel: String? = null,

    /**
     * 字典键值
     */
    var dictValue: String? = null,

    /**
     * 是否默认（Y是 N否）
     */
    var isDefault: String? = null,

    /**
     * 备注
     */
    var remark: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
