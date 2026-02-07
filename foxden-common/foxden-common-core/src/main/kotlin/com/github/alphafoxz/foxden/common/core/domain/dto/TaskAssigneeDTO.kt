package com.github.alphafoxz.foxden.common.core.domain.dto

import java.io.Serializable
import java.util.Date
import java.util.function.Function

/**
 * 任务受让人
 */
data class TaskAssigneeDTO(
    /**
     * 总大小
     */
    var total: Long = 0L,

    /**
     * 列表
     */
    var list: List<TaskHandler>? = null
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L

        /**
         * 将源列表转换为 TaskHandler 列表
         */
        fun <T> convertToHandlerList(
            sourceList: List<T>,
            storageId: Function<T, String>,
            handlerCode: Function<T, String>,
            handlerName: Function<T, String>,
            groupName: Function<T, String>,
            createTimeMapper: Function<T, Date>
        ): List<TaskHandler> {
            return sourceList.map { item ->
                TaskHandler(
                    storageId.apply(item),
                    handlerCode.apply(item),
                    handlerName.apply(item),
                    groupName.apply(item),
                    createTimeMapper.apply(item)
                )
            }
        }
    }

    /**
     * 任务处理器
     */
    data class TaskHandler(
        /**
         * 主键
         */
        var storageId: String? = null,

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
        var groupName: String? = null,

        /**
         * 创建时间
         */
        var createTime: Date? = null
    ) : Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }
}
