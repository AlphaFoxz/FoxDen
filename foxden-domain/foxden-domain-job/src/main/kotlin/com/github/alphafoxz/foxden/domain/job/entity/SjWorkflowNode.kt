package com.github.alphafoxz.foxden.domain.job.entity

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.*
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
/**
 * 工作流节点
 */
@Entity
@Table(name = "sj_workflow_node")
interface SjWorkflowNode : CommDelFlag, CommId {
    /** 命名空间id */
    val namespaceId: String

    /** 节点名称 */
    val nodeName: String

    /** 组名称 */
    val groupName: String

    /** 任务信息id */
    val jobId: Long

    /** 工作流ID */
    val workflowId: Long

    /** 1、任务节点 2、条件节点 */
    val nodeType: Int

    /** 1、SpEl、2、Aviator 3、QL */
    val expressionType: Int

    /** 失败策略 1、跳过 2、阻塞 */
    val failStrategy: Int

    /** 工作流节点状态 0、关闭、1、开启 */
    val workflowNodeStatus: Int

    /** 优先级 */
    val priorityLevel: Int

    /** 节点信息 */
    val nodeInfo: String?

    /** 版本号 */
    val version: Int

    /** 扩展字段 */
    val extAttrs: String?

}
