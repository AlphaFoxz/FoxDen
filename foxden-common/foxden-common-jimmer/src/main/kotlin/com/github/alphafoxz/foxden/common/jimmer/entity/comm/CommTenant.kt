package com.github.alphafoxz.foxden.common.jimmer.entity.comm

import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.MappedSuperclass

/**
 * 租户
 * 
 * @author wong
 */
@MappedSuperclass
interface CommTenant {
    /**
     * 租户编号
     */
    @Column(name = "tenant_id")
    val tenantId: String
}
