package com.github.alphafoxz.foxden.domain.workflow.bo

import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * 流程分类业务对象
 */
data class FlowCategoryBo(
    /**
     * 流程分类ID
     */
    @get:NotNull(message = "流程分类ID不能为空", groups = [EditGroup::class])
    var categoryId: Long? = null,

    /**
     * 分类编码
     */
    var categoryCode: String? = null,

    /**
     * 父流程分类id
     */
    @get:NotNull(message = "父流程分类id不能为空", groups = [AddGroup::class, EditGroup::class])
    var parentId: Long? = null,

    /**
     * 流程分类名称
     */
    @get:NotBlank(message = "流程分类名称不能为空", groups = [AddGroup::class, EditGroup::class])
    var categoryName: String? = null,

    /**
     * 显示顺序
     */
    var orderNum: Int? = null,

    /**
     * 备注
     */
    var remark: String? = null
)
