package com.github.alphafoxz.foxden.domain.job.entity

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.*
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
/**
 * 工作流批次
 */
@Entity
@Table(name = "sj_workflow_task_batch")
interface SjWorkflowTaskBatch : CommDelFlag, CommId {
    /** 命名空间id */
    val namespaceId: String

    /** 组名称 */
    val groupName: String

    /** 工作流任务id */
    val workflowId: Long

    /** 任务批次状态 0、失败 1、成功 */
    val taskBatchStatus: Int

    /** 操作原因 */
    val operationReason: Int

    /** 流程信息 */
    val flowInfo: String?

    /** 全局上下文 */
    val wfContext: String?

    /** 任务执行时间 */
    val executionAt: Long

    /** 扩展字段 */
    val extAttrs: String?

    /** 版本号 */
    val version: Int

}
