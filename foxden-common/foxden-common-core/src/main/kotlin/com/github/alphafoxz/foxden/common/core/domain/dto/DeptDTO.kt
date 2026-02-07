package com.github.alphafoxz.foxden.common.core.domain.dto

import java.io.Serializable

/**
 * 部门
 */
data class DeptDTO(
    /**
     * 部门ID
     */
    var deptId: Long? = null,

    /**
     * 父部门ID
     */
    var parentId: Long? = null,

    /**
     * 部门名称
     */
    var deptName: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
