package com.github.alphafoxz.foxden.domain.workflow.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 流程用户表 flow_user
 *
 * 用于存储任务与用户/角色/部门/岗位的关联关系
 * - associated 字段存储的是 flow_task 表的 id（任务ID）
 * - type 字段表示人员类型：
 *   - 1: 待办任务的审批人权限
 *   - 2: 待办任务的转办人权限
 *   - 3: 待办任务的委托人权限
 *   - 4: 抄送任务的抄送人权限
 * - processed_by 字段存储具体的用户ID、角色ID、部门ID或岗位ID
 */
@Entity
@Table(name = "flow_user")
interface FoxFlowUser : CommDelFlag, CommId {
    /**
     * 人员类型
     * 1: 待办任务的审批人权限
     * 2: 待办任务的转办人权限
     * 3: 待办任务的委托人权限
     * 4: 抄送任务的抄送人权限
     */
    val type: String

    /**
     * 权限人（用户ID、角色ID、部门ID或岗位ID）
     */
    val processedBy: String?

    /**
     * 关联ID - 对应 flow_task 表的 id
     * 表示该用户/角色/部门/岗位关联到哪个任务
     */
    val associated: Long

    /**
     * 创建时间
     */
    val createTime: java.time.LocalDateTime?

    /**
     * 创建者
     */
    val createBy: String?

    /**
     * 更新时间
     */
    val updateTime: java.time.LocalDateTime?

    /**
     * 更新者
     */
    val updateBy: String?

    /**
     * 租户ID
     */
    val tenantId: String?
}
