package com.github.alphafoxz.foxden.common.ratelimiter.config

import com.github.alphafoxz.foxden.common.ratelimiter.aspectj.RateLimiterAspect
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConfiguration

/**
 * @author guangxin
 * @date 2023/1/18
 */
@AutoConfiguration(after = [RedisConfiguration::class])
class RateLimiterConfig {

    @Bean
    fun rateLimiterAspect(): RateLimiterAspect {
        return RateLimiterAspect()
    }
}
