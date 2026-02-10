package com.github.alphafoxz.foxden.domain.workflow.constant

/**
 * 流程常量
 *
 * @author AprilWind
 */
object FlowConstant {
    /**
     * 流程发起人
     */
    const val INITIATOR = "initiator"

    /**
     * 业务ID
     */
    const val BUSINESS_ID = "businessId"

    /**
     * 部门ID
     */
    const val INITIATOR_DEPT_ID = "initiatorDeptId"

    /**
     * 流程分类名称缓存key
     */
    const val FLOW_CATEGORY_NAME = "flow_category_name#30d"

    /**
     * 流程分类Id转名称
     */
    const val CATEGORY_ID_TO_NAME = "category_id_to_name"

    /**
     * 默认流程分类ID（租户OA申请分类）
     */
    const val FLOW_CATEGORY_ID: Long = 100

    /**
     * 是否为申请人提交常量
     */
    const val SUBMIT = "submit"

    /**
     * 抄送常量
     */
    const val FLOW_COPY_LIST = "flowCopyList"

    /**
     * 消息类型常量
     */
    const val MESSAGE_TYPE = "messageType"

    /**
     * 消息通知常量
     */
    const val MESSAGE_NOTICE = "messageNotice"

    /**
     * 任务状态
     */
    const val WF_TASK_STATUS = "wf_task_status"

    /**
     * 自动通过
     */
    const val AUTO_PASS = "autoPass"

    /**
     * 业务编码
     */
    const val BUSINESS_CODE = "businessCode"

    /**
     * 忽略-办理权限校验（true：忽略，false：不忽略）
     */
    const val VAR_IGNORE = "ignore"

    /**
     * 忽略-委派处理（true：忽略，false：不忽略）
     */
    const val VAR_IGNORE_DEPUTE = "ignoreDepute"

    /**
     * 忽略-会签票签处理（true：忽略，false：不忽略）
     */
    const val VAR_IGNORE_COOPERATE = "ignoreCooperate"

    /**
     * 流程状态常量
     */
    object FlowStatus {
        const val CREATED = "created"        // 已创建
        const val RUNNING = "running"         // 运行中
        const val FINISHED = "finished"       // 已完成
        const val TERMINATED = "terminated"   // 已终止
        const val CANCELED = "canceled"       // 已取消
    }

    /**
     * 任务操作常量
     */
    object TaskOperation {
        const val DELEGATE = "delegateTask"        // 委派
        const val TRANSFER = "transferTask"        // 转办
        const val ADD_SIGN = "addSignature"        // 加签
        const val SUB_SIGN = "reductionSignature"  // 减签
    }
}

