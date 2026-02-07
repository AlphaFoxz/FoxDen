package com.github.alphafoxz.foxden.common.core.domain.dto

import java.io.Serializable

/**
 * 启动流程对象
 */
data class StartProcessDTO(
    /**
     * 业务唯一值id
     */
    var businessId: String? = null,

    /**
     * 流程定义编码
     */
    var flowCode: String? = null,

    /**
     * 办理人(可不填 用于覆盖当前节点办理人)
     */
    var handler: String? = null,

    /**
     * 流程变量，前端会提交一个元素{'entity': {业务详情数据对象}}
     */
    private var _variables: Map<String, Any>? = null
) : Serializable {

    var variables: Map<String, Any>
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
