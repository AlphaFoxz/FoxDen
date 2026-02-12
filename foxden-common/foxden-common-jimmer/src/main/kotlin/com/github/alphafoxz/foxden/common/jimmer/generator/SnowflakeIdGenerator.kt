package com.github.alphafoxz.foxden.common.jimmer.generator

import cn.hutool.core.util.IdUtil
import org.babyfish.jimmer.sql.meta.UserIdGenerator
import org.springframework.stereotype.Component

/**
 * 雪花算法ID生成器
 *
 * 基于 MAC 地址的分布式 ID 生成策略
 * 与老项目 MyBatis-Plus 的 DefaultIdentifierGenerator 行为一致
 */
@Component
class SnowflakeIdGenerator : UserIdGenerator<Long> {
    private val idGenerator = IdUtil.getSnowflake(0L)

    override fun generate(entityType: Class<*>?): Long {
        return idGenerator.nextId()
    }
}
