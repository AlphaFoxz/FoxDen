package com.github.alphafoxz.foxden.domain.workflow.bo

import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import jakarta.validation.constraints.NotNull
import java.io.Serializable

/**
 * 任务操作业务对象，用于描述任务委派、转办、加签等操作的必要参数
 * 包含了用户ID、任务ID、任务相关的消息、以及加签/减签的用户ID
 *
 * @author AprilWind
 */
data class TaskOperationBo(
    /**
     * 委派/转办人的用户ID（必填，准对委派/转办人操作）
     */
    @NotNull(message = "委派/转办人id不能为空", groups = [AddGroup::class])
    var userId: String? = null,

    /**
     * 加签/减签人的用户ID列表（必填，针对加签/减签操作）
     */
    @NotNull(message = "加签/减签id不能为空", groups = [EditGroup::class])
    var userIds: List<String>? = null,

    /**
     * 任务ID（必填）
     */
    @NotNull(message = "任务id不能为空")
    var taskId: Long? = null,

    /**
     * 意见或备注信息（可选）
     */
    var message: String? = null
) : Serializable
