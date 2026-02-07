package com.github.alphafoxz.foxden.common.core.domain.dto

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 岗位 DTO
 */
data class PostDTO(
    /**
     * 岗位ID
     */
    var postId: Long? = null,

    /**
     * 部门id
     */
    var deptId: Long? = null,

    /**
     * 岗位编码
     */
    var postCode: String? = null,

    /**
     * 岗位名称
     */
    var postName: String? = null,

    /**
     * 岗位类别编码
     */
    var postCategory: String? = null,

    /**
     * 显示顺序
     */
    var postSort: Int? = null,

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
    var createTime: LocalDateTime? = null,

    /**
     * 更新者
     */
    var updateBy: String? = null,

    /**
     * 更新时间
     */
    var updateTime: LocalDateTime? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
