package com.github.alphafoxz.foxden.common.core.service

import com.github.alphafoxz.foxden.common.core.domain.dto.UserDTO

/**
 * 用户服务接口
 *
 * @author Lion Li
 */
interface UserService {

    /**
     * 通过用户ID查询用户账户
     *
     * @param userId 用户ID
     * @return 用户账户
     */
    fun selectUserNameById(userId: Long): String?

    /**
     * 通过用户ID查询用户昵称
     *
     * @param userId 用户ID
     * @return 用户昵称
     */
    fun selectNicknameById(userId: Long): String?

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

    /**
     * 通过用户ID查询用户手机号
     *
     * @param userId 用户id
     * @return 用户手机号
     */
    fun selectPhonenumberById(userId: Long): String?

    /**
     * 通过用户ID查询用户邮箱
     *
     * @param userId 用户id
     * @return 用户邮箱
     */
    fun selectEmailById(userId: Long): String?

    /**
     * 通过用户ID查询用户列表
     *
     * @param userIds 用户ids
     * @return 用户列表
     */
    fun selectListByIds(userIds: List<Long>): List<UserDTO>

    /**
     * 通过角色ID查询用户ID
     *
     * @param roleIds 角色ids
     * @return 用户ids
     */
    fun selectUserIdsByRoleIds(roleIds: List<Long>): List<Long>

    /**
     * 通过角色ID查询用户
     *
     * @param roleIds 角色ids
     * @return 用户
     */
    fun selectUsersByRoleIds(roleIds: List<Long>): List<UserDTO>

    /**
     * 通过部门ID查询用户
     *
     * @param deptIds 部门ids
     * @return 用户
     */
    fun selectUsersByDeptIds(deptIds: List<Long>): List<UserDTO>

    /**
     * 通过岗位ID查询用户
     *
     * @param postIds 岗位ids
     * @return 用户
     */
    fun selectUsersByPostIds(postIds: List<Long>): List<UserDTO>

    /**
     * 根据用户 ID 列表查询用户名称映射关系
     *
     * @param userIds 用户 ID 列表
     * @return Map，其中 key 为用户 ID，value 为对应的用户名称
     */
    fun selectUserNamesByIds(userIds: List<Long>): Map<Long, String>
}
