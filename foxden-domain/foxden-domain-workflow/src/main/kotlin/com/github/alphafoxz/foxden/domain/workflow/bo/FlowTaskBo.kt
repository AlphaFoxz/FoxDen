package com.github.alphafoxz.foxden.domain.workflow.bo

import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

/**
 * 任务请求对象
 *
 * @author AprilWind
 */
data class FlowTaskBo(
    /**
     * 任务名称
     */
    var nodeName: String? = null,

    /**
     * 流程定义名称
     */
    var flowName: String? = null,

    /**
     * 流程定义编码
     */
    var flowCode: String? = null,

    /**
     * 流程分类id
     */
    var category: String? = null,

    /**
     * 流程实例id
     */
    var instanceId: Long? = null,

    /**
     * 流程状态
     */
    var flowStatus: String? = null,

    /**
     * 权限列表
     */
    var permissionList: List<String>? = null,

    /**
     * 申请人Ids
     */
    var createByIds: List<Long>? = null,

    /**
     * 请求参数
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var params: Map<String, Any> = mapOf()
) : Serializable
