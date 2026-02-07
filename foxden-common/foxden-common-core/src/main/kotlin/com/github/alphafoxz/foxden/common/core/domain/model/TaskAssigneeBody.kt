package com.github.alphafoxz.foxden.common.core.domain.model

import java.io.Serializable

/**
 * 任务受让人
 */
data class TaskAssigneeBody(
    /**
     * 权限编码
     */
    var handlerCode: String? = null,

    /**
     * 权限名称
     */
    var handlerName: String? = null,

    /**
     * 权限分组
     */
    var groupId: String? = null,

    /**
     * 开始时间
     */
    var beginTime: String? = null,

    /**
     * 结束时间
     */
    var endTime: String? = null,

    /**
     * 当前页
     */
    var pageNum: Int = 1,

    /**
     * 每页显示条数
     */
    var pageSize: Int = 10
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
