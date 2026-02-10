package com.github.alphafoxz.foxden.domain.workflow.bo

import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.io.Serializable

/**
 * 流程定义业务对象
 *
 * @author AprilWind
 */
data class FlowDefinitionBo(
    var id: Long? = null,

    /** 流程编码 */
    @field:NotBlank(message = "流程编码不能为空", groups = [AddGroup::class, EditGroup::class])
    var flowCode: String? = null,

    /** 流程名称 */
    @field:NotBlank(message = "流程名称不能为空", groups = [AddGroup::class, EditGroup::class])
    var flowName: String? = null,

    /** 流程分类 */
    var category: String? = null,

    /** 流程版本 */
    var version: Int? = null,

    /** 流程信息 */
    var flowXml: String? = null,

    /** 流程定义JSON */
    var flowJson: String? = null,

    /** 备注 */
    var remark: String? = null,

    /** 流程状态 */
    var flowStatus: String? = null
) : Serializable
