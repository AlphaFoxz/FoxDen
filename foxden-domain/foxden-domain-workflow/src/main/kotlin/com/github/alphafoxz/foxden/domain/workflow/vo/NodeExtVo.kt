package com.github.alphafoxz.foxden.domain.workflow.vo

import com.github.alphafoxz.foxden.domain.workflow.enums.NodeExtEnum
import java.io.Serializable

/**
 * 节点扩展视图对象
 *
 * @author AprilWind
 */
data class NodeExtVo(
    /**
     * 扩展属性列表
     */
    var ext: List<NodeExtEnumItem>? = null
) : Serializable {

    /**
     * 节点扩展属性项
     */
    data class NodeExtEnumItem(
        /**
         * 属性名称
         */
        var name: String? = null,

        /**
         * 属性值
         */
        var value: String? = null,

        /**
         * 扩展枚举
         */
        var nodeExtEnum: NodeExtEnum? = null
    )
}
