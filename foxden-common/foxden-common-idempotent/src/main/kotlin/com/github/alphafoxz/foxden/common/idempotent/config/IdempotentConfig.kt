package com.github.alphafoxz.foxden.common.idempotent.config

import com.github.alphafoxz.foxden.common.idempotent.aspectj.RepeatSubmitAspect
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean

/**
 * 幂等性配置
 *
 * @author Lion Li
 */
@AutoConfiguration
class IdempotentConfig {

    @Bean
    fun repeatSubmitAspect(): RepeatSubmitAspect {
        return RepeatSubmitAspect()
    }
}
