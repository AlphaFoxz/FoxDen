package com.github.alphafoxz.foxden.common.sse.dto

import java.io.Serializable

/**
 * SSE 消息接口
 *
 * @author FoxDen Team
 */
interface SseMessage : Serializable {
    fun getContent(): Content
    fun getUserIds(): List<Long>?
    fun setUserIds(userIds: List<Long>?)

    /**
     * 消息内容
     */
    data class Content(
        var id: String? = null,
        var type: String? = null,
        var key: String? = null,
        var value: Any? = null
    )

    companion object {
        const val CONTENT_TYPE_MESSAGE = "message"
        const val CONTENT_TYPE_DIFY_DONE = "dify.done"
        const val CONTENT_TYPE_DIFY_MESSAGE = "dify.message"
        const val CONTENT_TYPE_DIFY_META = "dify.meta"
        const val CONTENT_TYPE_DIFY_ERROR = "dify.error"

        fun copy(message: SseMessage): SseMessage {
            return when (message) {
                is SseDifyDto -> {
                    val result = SseDifyDto()
                    result.setUserIds(message.getUserIds())
                    val content = message.getContent()
                    result.getContent().id = content.id
                    result.getContent().type = content.type
                    result.getContent().key = content.key
                    result.getContent().value = content.value
                    result
                }
                is SseMessageDefaultDto -> {
                    val result = SseMessageDefaultDto()
                    result.setUserIds(message.getUserIds())
                    val content = message.getContent()
                    result.getContent().type = content.type
                    result.getContent().value = content.value
                    result
                }
                else -> throw IllegalArgumentException("SseMessage类型错误")
            }
        }
    }
}
