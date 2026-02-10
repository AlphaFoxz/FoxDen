package com.github.alphafoxz.foxden.domain.workflow.service

import com.github.alphafoxz.foxden.common.core.domain.dto.UserDTO

/**
 * 任务分配人服务接口
 *
 * @author AprilWind
 */
interface FlowTaskAssigneeService {

    /**
     * 批量解析多个存储标识符（storageIds），按类型分类并合并查询用户列表
     * 输入格式支持多个以逗号分隔的标识（如 "user:123,role:456,789"）
     * 会自动去重返回结果，非法格式的标识将被忽略
     *
     * @param storageIds 多个存储标识符字符串（逗号分隔）
     * @return 合并后的用户列表，去重后返回，非法格式的标识将被跳过
     */
    fun fetchUsersByStorageIds(storageIds: String): List<UserDTO>

    /**
     * 获取用户的待办任务ID列表
     *
     * @param userId 用户ID
     * @return 任务ID列表
     */
    fun getTaskIdsByUser(userId: String): List<Long>

    /**
     * 获取用户的抄送任务ID列表
     *
     * @param userId 用户ID
     * @return 任务ID列表
     */
    fun getCopyTaskIdsByUser(userId: String): List<Long>
}
