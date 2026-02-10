package com.github.alphafoxz.foxden.domain.workflow.vo

import java.io.Serializable

/**
 * 抄送对象视图对象
 *
 * @author AprilWind
 */
data class FlowCopyVo(
    /**
     * 用户id
     */
    var userId: Long? = null,

    /**
     * 用户名称
     */
    var userName: String? = null
) : Serializable {

    constructor(userId: Long) : this(userId, null)
}
