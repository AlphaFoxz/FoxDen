package com.github.alphafoxz.foxden.domain.workflow.constant

/**
 * 工作流常量
 */
object FlowConstant {
    /**
     * 流程发起人
     */
    const val INITIATOR = "initiator"

    /**
     * 业务id
     */
    const val BUSINESS_ID = "businessId"

    /**
     * 部门id
     */
    const val INITIATOR_DEPT_ID = "initiatorDeptId"

    /**
     * 委托
     */
    const val DELEGATE_TASK = "delegateTask"

    /**
     * 转办
     */
    const val TRANSFER_TASK = "transferTask"

    /**
     * 加签
     */
    const val ADD_SIGNATURE = "addSignature"

    /**
     * 减签
     */
    const val REDUCTION_SIGNATURE = "reductionSignature"

    /**
     * 流程分类Id转名称
     */
    const val CATEGORY_ID_TO_NAME = "category_id_to_name"

    /**
     * 流程分类名称缓存
     */
    const val FLOW_CATEGORY_NAME = "flow_category_name#30d"

    /**
     * 默认租户OA申请分类id
     */
    const val FLOW_CATEGORY_ID = 100L

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
}
