package com.github.alphafoxz.foxden.domain.workflow.enums

/**
 * 消息类型枚举
 *
 * @author may
 */
enum class MessageTypeEnum(private val code: String, private val desc: String) {

    /**
     * 站内信
     */
    SYSTEM_MESSAGE("1", "站内信"),

    /**
     * 邮箱
     */
    EMAIL_MESSAGE("2", "邮箱"),

    /**
     * 短信
     */
    SMS_MESSAGE("3", "短信");

    companion object {
        private val MESSAGE_TYPE_ENUM_MAP: Map<String, MessageTypeEnum> = entries.associate { it.code to it }

        /**
         * 根据消息类型 code 获取 MessageTypeEnum
         *
         * @param code 消息类型code
         * @return MessageTypeEnum
         */
        fun getByCode(code: String): MessageTypeEnum? {
            return MESSAGE_TYPE_ENUM_MAP[code]
        }
    }

    fun getCode(): String = code
    fun getDesc(): String = desc
}
