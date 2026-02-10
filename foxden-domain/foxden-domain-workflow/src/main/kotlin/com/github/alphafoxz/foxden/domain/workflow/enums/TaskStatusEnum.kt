package com.github.alphafoxz.foxden.domain.workflow.enums

/**
 * 任务状态枚举
 *
 * @author may
 */
enum class TaskStatusEnum(private val status: String, private val desc: String) {

    /**
     * 撤销
     */
    CANCEL("cancel", "撤销"),

    /**
     * 通过
     */
    PASS("pass", "通过"),

    /**
     * 待审核
     */
    WAITING("waiting", "待审核"),

    /**
     * 作废
     */
    INVALID("invalid", "作废"),

    /**
     * 退回
     */
    BACK("back", "退回"),

    /**
     * 终止
     */
    TERMINATION("termination", "终止"),

    /**
     * 转办
     */
    TRANSFER("transfer", "转办"),

    /**
     * 委托
     */
    DEPUTE("depute", "委托"),

    /**
     * 抄送
     */
    COPY("copy", "抄送"),

    /**
     * 加签
     */
    SIGN("sign", "加签"),

    /**
     * 减签
     */
    SIGN_OFF("sign_off", "减签"),

    /**
     * 超时
     */
    TIMEOUT("timeout", "超时");

    companion object {
        private val STATUS_DESC_MAP: Map<String, String> = entries.associate { it.status to it.desc }

        /**
         * 任务业务状态描述
         *
         * @param status 状态
         * @return 状态描述
         */
        fun findByStatus(status: String): String {
            return STATUS_DESC_MAP[status] ?: ""
        }

        /**
         * 判断状态是否为通过或退回
         *
         * @param status 状态值
         * @return true 表示是通过或退回状态
         */
        fun isPassOrBack(status: String?): Boolean {
            return status == PASS.status || status == BACK.status
        }
    }

    fun getStatus(): String = status
    fun getDesc(): String = desc
}
