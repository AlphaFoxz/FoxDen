package com.github.alphafoxz.foxden.domain.workflow.config

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.ComponentScan

/**
 * 工作流模块自动配置
 *
 * @author AprilWind
 */
@AutoConfiguration
@ConditionalOnProperty(
    name = ["foxden.workflow.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
@ComponentScan("com.github.alphafoxz.foxden.domain.workflow")
class WorkflowAutoConfiguration
