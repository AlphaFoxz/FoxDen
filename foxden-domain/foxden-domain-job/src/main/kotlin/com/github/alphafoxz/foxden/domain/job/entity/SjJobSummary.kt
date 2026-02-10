package com.github.alphafoxz.foxden.domain.job.entity

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.*
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
/**
 * DashBoard_Job
 */
@Entity
@Table(name = "sj_job_summary")
interface SjJobSummary : CommId {
    /** 命名空间id */
    val namespaceId: String

    /** 组名称 */
    val groupName: String

    /** 业务id (job_id或workflow_id) */
    val businessId: Long

    /** 任务类型 3、JOB任务 4、WORKFLOW任务 */
    val systemTaskType: Int

    /** 统计时间 */
    val triggerAt: java.time.LocalDateTime

    /** 执行成功-日志数量 */
    val successNum: Int

    /** 执行失败-日志数量 */
    val failNum: Int

    /** 失败原因 */
    val failReason: String

    /** 执行停止-日志数量 */
    val stopNum: Int

    /** 停止原因 */
    val stopReason: String

    /** 取消数量 */
    val cancelNum: Int

    /** 取消原因 */
    val cancelReason: String

}
