package com.github.alphafoxz.foxden.domain.workflow.service.impl

import com.github.alphafoxz.foxden.common.core.domain.dto.UserDTO
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.domain.workflow.adapter.WorkflowEngineAdapter
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowUser
import com.github.alphafoxz.foxden.domain.workflow.enums.TaskAssigneeEnum
import com.github.alphafoxz.foxden.domain.workflow.service.FlowTaskAssigneeService
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.dromara.warm.flow.core.FlowEngine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 任务分配人服务实现
 *
 * @author AprilWind
 */
@Service
class FlowTaskAssigneeServiceImpl(
    private val sqlClient: KSqlClient,
    private val workflowEngineAdapter: WorkflowEngineAdapter
) : FlowTaskAssigneeService {

    private val log = LoggerFactory.getLogger(FlowTaskAssigneeServiceImpl::class.java)

    // WarmFlow service
    private val userService = FlowEngine.userService()

    /**
     * 批量解析多个存储标识符（storageIds），按类型分类并合并查询用户列表
     * 输入格式支持多个以逗号分隔的标识（如 "user:123,role:456,789"）
     * 会自动去重返回结果，非法格式的标识将被忽略
     *
     * @param storageIds 多个存储标识符字符串（逗号分隔）
     * @return 合并后的用户列表，去重后返回，非法格式的标识将被跳过
     */
    override fun fetchUsersByStorageIds(storageIds: String): List<UserDTO> {
        // TODO: 需要集成系统服务后实现
        // 业务逻辑：
        // 1. 解析 storageIds 字符串，按逗号分割成多个标识
        // 2. 对每个标识调用 parseStorageId() 解析类型和ID：
        //
        //    支持的类型：
        //    - user:123 - 直接指定用户ID为123的用户
        //    - role:456 - 角色ID为456的角色下的所有用户
        //    - dept:789 - 部门ID为789的部门下的所有用户
        //    - post:101 - 岗位ID为101的岗位下的所有用户
        //    - $xxx - SpEL表达式，动态计算办理人
        //    - #xxx - SpEL表达式，动态计算办理人
        //    - 123 - 默认为用户ID，等效于 user:123
        //
        // 3. 根据类型批量查询用户：
        //    - user: 直接查询 sys_user 表
        //    - role: 查询 rel_user_role 关联表获取用户ID列表
        //    - dept: 查询 sys_user 表中 dept_id 等于指定ID的用户
        //    - post: 查询 rel_user_post 关联表获取用户ID列表
        //    - SpEL: 调用 SpEL 解析器动态计算，递归调用本方法
        //
        // 4. 合并所有查询结果，按 userId 去重
        // 5. 转换为 UserDTO 列表返回
        //
        // 实现方式：
        // - 需要 SysUserService, SysRoleService, SysDeptService 等系统服务
        // - 需要集成 FlowSpelService 来解析 SpEL 表达式
        //
        // 当前实现：记录日志并返回空列表
        log.info("查询任务分配人 - storageIds: {}", storageIds)
        return emptyList()
    }

    /**
     * 获取用户的待办任务ID列表
     *
     * @param userId 用户ID
     * @return 任务ID列表
     */
    override fun getTaskIdsByUser(userId: String): List<Long> {
        // 查询用户关联的任务（作为审批人）
        val users = sqlClient.createQuery(FoxFlowUser::class) {
            select(table)
        }.execute().filter { it.processedBy == userId && it.delFlag == "0" && it.type == "1" }

        return users.map { it.associated }.distinct()
    }

    /**
     * 获取用户的抄送任务ID列表
     *
     * @param userId 用户ID
     * @return 任务ID列表
     */
    override fun getCopyTaskIdsByUser(userId: String): List<Long> {
        // 查询用户关联的任务（作为抄送人）
        val users = sqlClient.createQuery(FoxFlowUser::class) {
            select(table)
        }.execute().filter { it.processedBy == userId && it.delFlag == "0" && it.type == "2" }

        return users.map { it.associated }.distinct()
    }

    /**
     * 解析 storageId 字符串，返回类型和ID的组合
     *
     * @param storageId 例如 "user:123" 或 "456"
     * @return Pair(TaskAssigneeEnum, String)，如果格式非法返回 null
     */
    private fun parseStorageId(storageId: String): Pair<TaskAssigneeEnum, String>? {
        if (storageId.isBlank()) {
            return null
        }

        if (isSpelExpression(storageId)) {
            return TaskAssigneeEnum.SPEL to storageId
        }

        return try {
            val parts = storageId.split(":", limit = 2)
            if (parts.size < 2) {
                TaskAssigneeEnum.USER to parts[0]
            } else {
                val type = TaskAssigneeEnum.fromCode(parts[0] + ":")
                type to parts[1]
            }
        } catch (e: Exception) {
            log.warn("解析 storageId 失败，格式非法：{}，错误信息：{}", storageId, e.message)
            null
        }
    }

    /**
     * 判断给定字符串是否符合 SPEL 表达式格式（以 $ 或 # 开头）
     *
     * @param value 待判断字符串
     * @return 是否为 SPEL 表达式
     */
    private fun isSpelExpression(value: String): Boolean {
        if (value.isBlank()) {
            return false
        }
        // $前缀表示默认办理人变量策略
        // #前缀表示spel办理人变量策略
        return StringUtils.startsWith(value, "\$") || StringUtils.startsWith(value, "#")
    }
}
