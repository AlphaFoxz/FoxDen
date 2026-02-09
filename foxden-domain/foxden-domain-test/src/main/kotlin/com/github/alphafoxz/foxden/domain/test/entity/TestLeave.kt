package com.github.alphafoxz.foxden.domain.test.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 请假申请表 test_leave
 */
@Entity
@Table(name = "test_leave")
interface TestLeave : CommId, CommInfo, CommTenant {
    /**
     * 申请编号
     */
    val applyCode: String

    /**
     * 请假类型
     */
    val leaveType: String

    /**
     * 开始时间
     */
    val startDate: java.time.LocalDateTime

    /**
     * 结束时间
     */
    val endDate: java.time.LocalDateTime

    /**
     * 请假天数
     */
    val leaveDays: Int

    /**
     * 备注
     */
    val remark: String?

    /**
     * 状态
     */
    val status: String?
}
