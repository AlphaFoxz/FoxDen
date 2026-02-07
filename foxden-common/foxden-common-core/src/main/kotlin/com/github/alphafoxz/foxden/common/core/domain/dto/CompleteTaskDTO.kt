package com.github.alphafoxz.foxden.common.core.domain.dto

import java.io.Serializable

/**
 * 办理任务请求对象
 */
data class CompleteTaskDTO(
    /**
     * 任务id
     */
    var taskId: Long? = null,

    /**
     * 附件id
     */
    var fileId: String? = null,

    /**
     * 抄送人员
     */
    var flowCopyList: List<FlowCopyDTO>? = null,

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
     * 流程变量
     */
    private var _variables: Map<String, Any>? = null,

    /**
     * 扩展变量(此处为逗号分隔的ossId)
     */
    var ext: String? = null
) : Serializable {

    var variables: Map<String, Any>?
        get() {
            if (_variables == null) {
                return emptyMap()
            }
            return _variables!!.filterValues { it != null }
        }
        set(value) {
            _variables = value
        }

    companion object {
        private const val serialVersionUID = 1L
    }
}
