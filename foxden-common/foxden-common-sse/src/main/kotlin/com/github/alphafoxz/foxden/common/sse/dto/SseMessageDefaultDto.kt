package com.github.alphafoxz.foxden.common.sse.dto

/**
 * 默认消息 DTO
 *
 * @author FoxDen Team
 */
data class SseMessageDefaultDto(
    private var _userIds: List<Long>? = null,
    private var _content: SseMessage.Content = SseMessage.Content()
) : SseMessage {

    override fun getContent(): SseMessage.Content = _content
    override fun getUserIds(): List<Long>? = _userIds
    override fun setUserIds(userIds: List<Long>?) {
        this._userIds = userIds
    }

    companion object {
        /**
         * 创建消息
         */
        fun message(value: Any): SseMessageDefaultDto {
            val dto = SseMessageDefaultDto()
            dto._content.type = SseMessage.CONTENT_TYPE_MESSAGE
            dto._content.value = value
            return dto
        }
    }
}
