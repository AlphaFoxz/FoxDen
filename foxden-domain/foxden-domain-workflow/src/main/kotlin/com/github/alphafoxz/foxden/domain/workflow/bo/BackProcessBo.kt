package com.github.alphafoxz.foxden.domain.workflow.bo

import java.io.Serializable

/**
 * 退回流程业务对象
 *
 * @author AprilWind
 */
data class BackProcessBo(
    /**
     * 任务id
     */
    var taskId: Long? = null,

    /**
     * 退回到的节点id
     */
    var targetNodeId: Long? = null,

    /**
     * 退回意见
     */
    var message: String? = null,

    /**
     * 退回到的节点编码
     */
    var nodeCode: String? = null,

    /**
     * 消息类型
     */
    var messageType: List<String>? = null,

    /**
     * 消息通知
     */
    var notice: String? = null
) : Serializable
