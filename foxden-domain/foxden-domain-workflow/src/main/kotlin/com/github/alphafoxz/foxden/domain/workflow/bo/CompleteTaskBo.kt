package com.github.alphafoxz.foxden.domain.workflow.bo

import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import jakarta.validation.constraints.NotNull
import java.io.Serializable

/**
 * 办理任务请求对象
 *
 * @author AprilWind
 */
data class CompleteTaskBo(
    /**
     * 任务id
     */
    @NotNull(message = "任务id不能为空", groups = [AddGroup::class])
    var taskId: Long? = null,

    /**
     * 附件id
     */
    var fileId: String? = null,

    /**
     * 抄送人员
     */
    var flowCopyList: List<FlowCopyBo>? = null,

    /**
     * 消息类型
     */
    var messageType: List<String>? = null,

    /**
     * 办理意见
     */
    var message: String? = null,

    /**
     * 消息通知
     */
    var notice: String? = null,

    /**
     * 办理人(可不填 用于覆盖当前节点办理人)
     */
    var handler: String? = null,

    /**
     * 流程变量（原始值）
     */
    var variablesData: MutableMap<String, Any>? = null,

    /**
     * 弹窗选择的办理人
     */
    var assigneeMap: Map<String, Any>? = null,

    /**
     * 扩展变量(此处为逗号分隔的ossId)
     */
    var ext: String? = null
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
}
