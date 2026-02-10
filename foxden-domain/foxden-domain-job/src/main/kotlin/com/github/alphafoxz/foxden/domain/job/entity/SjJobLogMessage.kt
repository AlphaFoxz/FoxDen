package com.github.alphafoxz.foxden.domain.job.entity

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.*
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
/**
 * 调度日志
 */
@Entity
@Table(name = "sj_job_log_message")
interface SjJobLogMessage : CommId {
    /** 命名空间id */
    val namespaceId: String

    /** 组名称 */
    val groupName: String

    /** 任务信息id */
    val jobId: Long

    /** 任务批次id */
    val taskBatchId: Long

    /** 调度任务id */
    val taskId: Long

    /** 调度信息 */
    val message: String

    /** 日志数量 */
    val logNum: Int

    /** 上报时间 */
    val realTime: Long

    /** 扩展字段 */
    val extAttrs: String?

}
