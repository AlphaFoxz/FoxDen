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
     * 分类编码
     */
    var categoryCode: String? = null,

    /**
     * 分类名称
     */
    var categoryName: String? = null,

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
     * 显示顺序
     */
    var orderNum: Int? = null,

    /**
     * 租户ID
     */
    var tenantId: String? = null,

    /**
     * 删除标记
     */
    var delFlag: String? = null,

    /**
     * 创建时间
     */
    var createTime: LocalDateTime? = null,

    /**
     * 更新时间
     */
    var updateTime: LocalDateTime? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 更新者
     */
    var updateBy: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 子分类列表
     */
    var children: List<FlowCategoryVo>? = null
) : Serializable
