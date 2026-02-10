package com.github.alphafoxz.foxden.domain.job.entity

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.*
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
/**
 * 任务批次
 */
@Entity
@Table(name = "sj_job_task_batch")
interface SjJobTaskBatch : CommDelFlag, CommId {
    /** 命名空间id */
    val namespaceId: String

    /** 组名称 */
    val groupName: String

    /** 任务id */
    val jobId: Long

    /** 工作流节点id */
    val workflowNodeId: Long

    /** 工作流任务父批次id */
    val parentWorkflowNodeId: Long

    /** 工作流任务批次id */
    val workflowTaskBatchId: Long

    /** 任务批次状态 0、失败 1、成功 */
    val taskBatchStatus: Int

    /** 操作原因 */
    val operationReason: Int

    /** 任务执行时间 */
    val executionAt: Long

    /** 任务类型 3、JOB任务 4、WORKFLOW任务 */
    val systemTaskType: Int

    /** 父节点 */
    val parentId: String

    /** 扩展字段 */
    val extAttrs: String?

}
