package com.github.alphafoxz.foxden.common.jimmer.entity.comm

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.MappedSuperclass

/**
 * 多租户Trait
 * 多租户实体的租户ID字段定义
 */
@MappedSuperclass
interface CommTenant {
    /**
     * 租户ID
     */
    val tenantId: String
}
