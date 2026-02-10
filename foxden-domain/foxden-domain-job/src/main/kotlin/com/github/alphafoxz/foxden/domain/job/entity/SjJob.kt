package com.github.alphafoxz.foxden.domain.job.entity

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.*
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
/**
 * 任务信息
 */
@Entity
@Table(name = "sj_job")
interface SjJob : CommDelFlag, CommId {
    /** 命名空间id */
    val namespaceId: String

    /** 组名称 */
    val groupName: String

    /** 名称 */
    val jobName: String

    /** 执行方法参数 */
    val argsStr: String?

    /** 参数类型 */
    val argsType: Int

    /** 下次触发时间 */
    val nextTriggerAt: Long

    /** 任务状态 0、关闭、1、开启 */
    val jobStatus: Int

    /** 任务类型 1、集群 2、广播 3、切片 */
    val taskType: Int

    /** 路由策略 */
    val routeKey: Int

    /** 执行器类型 */
    val executorType: Int

    /** 执行器名称 */
    val executorInfo: String?

    /** 触发类型 1.CRON 表达式 2. 固定时间 */
    val triggerType: Int

    /** 间隔时长 */
    val triggerInterval: String

    /** 阻塞策略 1、丢弃 2、覆盖 3、并行 4、恢复 */
    val blockStrategy: Int

    /** 任务执行超时时间，单位秒 */
    val executorTimeout: Int

    /** 最大重试次数 */
    val maxRetryTimes: Int

    /** 并行数 */
    val parallelNum: Int

    /** 重试间隔 */
    val retryInterval: Int

    /** bucket */
    val bucketIndex: Int

    /** 是否是常驻任务 */
    val resident: Int

    /** 通知告警场景配置id列表 */
    val notifyIds: String

    /** 负责人id */
    val ownerId: Long?

    /** 标签 */
    val labels: String?

    /** 描述 */
    val description: String

    /** 扩展字段 */
    val extAttrs: String?
}
