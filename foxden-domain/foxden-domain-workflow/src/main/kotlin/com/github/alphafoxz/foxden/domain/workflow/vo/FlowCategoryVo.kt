package com.github.alphafoxz.foxden.domain.workflow.vo

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 流程分类视图对象
 */
data class FlowCategoryVo(
    /**
     * 流程分类ID
     */
    var categoryId: Long? = null,

    /**
     * 父级分类id
     */
    var parentId: Long? = null,

    /**
     * 父级分类名称
     */
    var parentName: String? = null,

    /**
     * 祖级列表
     */
    var ancestors: String? = null,

    /**
     * 流程分类名称
     */
    var categoryName: String? = null,

    /**
     * 显示顺序
     */
    var orderNum: Int? = null,

    /**
     * 创建时间
     */
    var createTime: LocalDateTime? = null
) : Serializable
