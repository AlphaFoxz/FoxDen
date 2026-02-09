package com.github.alphafoxz.foxden.domain.workflow.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 流程实例业务扩展表 flow_instance_biz_ext
 */
@Entity
@Table(name = "flow_instance_biz_ext")
interface FlowInstanceBizExt : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 业务编码
     */
    val businessCode: String?

    /**
     * 业务标题
     */
    val businessTitle: String?

    /**
     * 流程实例ID
     */
    val instanceId: Long?

    /**
     * 业务编号
     */
    val businessId: String?
}
