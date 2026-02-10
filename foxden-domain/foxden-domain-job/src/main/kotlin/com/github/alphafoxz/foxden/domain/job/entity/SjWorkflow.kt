package com.github.alphafoxz.foxden.domain.job.entity

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.*
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
/**
 * 工作流
 */
@Entity
@Table(name = "sj_workflow")
interface SjWorkflow : CommDelFlag, CommId {
    /** 工作流名称 */
    val workflowName: String

    /** 命名空间id */
    val namespaceId: String

    /** 组名称 */
    val groupName: String

    /** 工作流状态 0、关闭、1、开启 */
    val workflowStatus: Int

    /** 触发类型 1.CRON 表达式 2. 固定时间 */
    val triggerType: Int

    /** 间隔时长 */
    val triggerInterval: String

    /** 下次触发时间 */
    val nextTriggerAt: Long

    /** 阻塞策略 1、丢弃 2、覆盖 3、并行 */
    val blockStrategy: Int

    /** 任务执行超时时间，单位秒 */
    val executorTimeout: Int

    /** 描述 */
    val description: String

    /** 流程信息 */
    val flowInfo: String?

    /** 上下文 */
    val wfContext: String?

    /** 通知告警场景配置id列表 */
    val notifyIds: String

    /** bucket */
    val bucketIndex: Int

    /** 版本号 */
    val version: Int

    /** 负责人id */
    val ownerId: Long?

    /** 扩展字段 */
    val extAttrs: String?
}
