package com.github.alphafoxz.foxden.common.sse.dto

/**
 * Dify 消息 DTO
 *
 * @author FoxDen Team
 */
data class SseDifyDto(
    private var _userIds: List<Long>? = null,
    private var _content: SseMessage.Content = SseMessage.Content()
) : SseMessage {

    override fun getContent(): SseMessage.Content = _content
    override fun getUserIds(): List<Long>? = _userIds
    override fun setUserIds(userIds: List<Long>?) {
        this._userIds = userIds
    }
}
