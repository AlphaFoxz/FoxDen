package com.github.alphafoxz.foxden.common.sse.dto

import java.io.Serializable

/**
 * SSE 消息 DTO (用于登录欢迎消息等场景)
 *
 * @author FoxDen Team
 */
data class SseMessageDto(
    var userIds: List<Long>? = null,
    var message: String? = null
) : Serializable
