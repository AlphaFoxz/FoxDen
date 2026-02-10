package com.github.alphafoxz.foxden.domain.workflow.bo

import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import jakarta.validation.constraints.NotBlank
import java.io.Serializable

/**
 * 流程spel表达式定义业务对象 flow_spel
 *
 * @author AprilWind
 */
data class FlowSpelBo(
    /**
     * 主键id
     */
    var id: Long? = null,

    /**
     * 组件名称
     */
    var componentName: String? = null,

    /**
     * 方法名
     */
    var methodName: String? = null,

    /**
     * 参数
     */
    var methodParams: String? = null,

    /**
     * 预览spel值
     */
    @NotBlank(message = "预览spel值不能为空", groups = [AddGroup::class, EditGroup::class])
    var viewSpel: String? = null,

    /**
     * 状态（0正常 1停用）
     */
    @NotBlank(message = "状态（0正常 1停用）不能为空", groups = [AddGroup::class, EditGroup::class])
    var status: String? = null,

    /**
     * 备注
     */
    var remark: String? = null
) : Serializable
