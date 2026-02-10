package com.github.alphafoxz.foxden.domain.workflow.bo

import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import jakarta.validation.constraints.NotBlank
import java.io.Serializable

/**
 * 启动流程对象
 *
 * @author AprilWind
 */
data class StartProcessBo(
    /**
     * 业务唯一值id
     */
    @NotBlank(message = "业务ID不能为空", groups = [AddGroup::class])
    var businessId: String? = null,

    /**
     * 流程定义编码
     */
    @NotBlank(message = "流程定义编码不能为空", groups = [AddGroup::class])
    var flowCode: String? = null,

    /**
     * 办理人(可不填 用于覆盖当前节点办理人)
     */
    var handler: String? = null,

    /**
     * 流程变量，前端会提交一个元素{'entity': {业务详情数据对象}}
     */
    var variablesData: MutableMap<String, Any>? = null,

    /**
     * 流程业务扩展信息
     */
    var bizExtData: FlowInstanceBizExt? = null
) : Serializable {

    /**
     * 获取流程变量，去除空值
     */
    fun getVariables(): Map<String, Any> {
        val vars = variablesData ?: mutableMapOf()
        vars.entries.removeIf { it.value == null }
        return vars
    }

    /**
     * 设置流程变量
     */
    fun setVariables(value: Map<String, Any>?) {
        this.variablesData = value?.toMutableMap()
    }

    /**
     * 获取业务扩展信息，如果为空则创建新实例
     */
    fun getBizExt(): FlowInstanceBizExt {
        if (bizExtData == null) {
            return FlowInstanceBizExt()
        }
        return bizExtData!!
    }

    /**
     * 设置业务扩展信息
     */
    fun setBizExt(value: FlowInstanceBizExt?) {
        this.bizExtData = value
    }
}

/**
 * 流程实例业务扩展信息
 */
data class FlowInstanceBizExt(
    /**
     * 业务编码
     */
    var businessCode: String? = null,

    /**
     * 业务标题
     */
    var businessTitle: String? = null
)
