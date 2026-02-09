package com.github.alphafoxz.foxden.domain.workflow.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 流程用户表 flow_user
 */
@Entity
@Table(name = "flow_user")
interface FlowUser : CommDelFlag, CommId {
    /**
     * 类型
     */
    val type: String

    /**
     * 处理人
     */
    val processedBy: String?

    /**
     * 关联ID
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
