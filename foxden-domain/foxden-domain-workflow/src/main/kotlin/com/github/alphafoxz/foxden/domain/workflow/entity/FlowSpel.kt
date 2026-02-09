package com.github.alphafoxz.foxden.domain.workflow.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 流程Spel表达式表 flow_spel
 */
@Entity
@Table(name = "flow_spel")
interface FlowSpel : CommDelFlag, CommId, CommInfo {
    /**
     * 组件名称
     */
    val componentName: String?

    /**
     * 方法名称
     */
    val methodName: String?

    /**
     * 方法参数
     */
    val methodParams: String?

    /**
     * 视图Spel表达式
     */
    val viewSpel: String?

    /**
     * 备注
     */
    val remark: String?

    /**
     * 状态
     */
    val status: String?
}
