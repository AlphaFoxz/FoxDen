package com.github.alphafoxz.foxden.domain.workflow.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 工作流配置属性
 *
 * @author AprilWind
 */
@ConfigurationProperties(prefix = "foxden.workflow")
data class WorkflowProperties(
    /**
     * 是否启用工作流模块
     */
    val enabled: Boolean = false,

    /**
     * 数据库类型
     */
    val databaseType: String = "mysql",

    /**
     * 流程定义存储路径
     */
    val definitionPath: String = "workflow/definitions/",

    /**
     * 是否自动部署流程
     */
    val autoDeploy: Boolean = false,

    /**
     * 流程图存储路径
     */
    val chartPath: String = "workflow/charts/"
)
