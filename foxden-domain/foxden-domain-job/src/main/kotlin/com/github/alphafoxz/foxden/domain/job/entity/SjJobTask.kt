package com.github.alphafoxz.foxden.domain.job.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.*
/**
 * 任务实例
 */
@Entity
@Table(name = "sj_job_task")
interface SjJobTask : CommId {
    /** 命名空间id */
    val namespaceId: String

    /** 组名称 */
    val groupName: String

    /** 任务信息id */
    val jobId: Long

    /** 调度任务id */
    val taskBatchId: Long

    /** 父执行器id */
    val parentId: Long

    /** 执行的状态 0、失败 1、成功 */
    val taskStatus: Int

    /** 重试次数 */
    val retryCount: Int

    /** 动态分片所处阶段 1:map 2:reduce 3:mergeReduce */
    val mrStage: Int?

    /** 叶子节点 */
    val leaf: Int

    /** 任务名称 */
    val taskName: String

    /** 客户端地址 clientId#ip:port */
    val clientInfo: String?

    /** 工作流全局上下文 */
    val wfContext: String?

    /** 执行结果 */
    val resultMessage: String

    /** 执行方法参数 */
    val argsStr: String?

    /** 参数类型 */
    val argsType: Int

    /** 扩展字段 */
    val extAttrs: String?

}
