package com.github.alphafoxz.foxden.domain.workflow.vo

import java.io.Serializable
import java.time.LocalDateTime

/**
 * 流程spel表达式定义视图对象
 *
 * @author AprilWind
 */
data class FlowSpelVo(
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
    var viewSpel: String? = null,

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 创建时间
     */
    var createTime: LocalDateTime? = null
) : Serializable
