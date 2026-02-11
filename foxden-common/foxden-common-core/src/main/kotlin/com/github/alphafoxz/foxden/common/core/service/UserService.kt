package com.github.alphafoxz.foxden.common.core.service

/**
 * 用户服务接口
 *
 * @author Lion Li
 */
interface UserService {

    /**
     * 根据用户ID字符串获取对应的用户账号列表（逗号分隔）
     *
     * @param userIds 以逗号分隔的用户ID字符串
     * @return 用户账号字符串（逗号分隔）
     */
    fun selectUserNameByIds(userIds: String): String?

    /**
     * 根据用户ID字符串获取对应的用户昵称列表（逗号分隔）
     *
     * @param userIds 以逗号分隔的用户ID字符串
     * @return 用户昵称字符串（逗号分隔）
     */
    fun selectNicknameByIds(userIds: String): String?
}
