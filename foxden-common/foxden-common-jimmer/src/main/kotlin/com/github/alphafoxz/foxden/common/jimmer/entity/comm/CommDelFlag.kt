package com.github.alphafoxz.foxden.common.jimmer.entity.comm

import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.LogicalDeleted
import org.babyfish.jimmer.sql.MappedSuperclass

/**
 * @author wong
 */
@MappedSuperclass
interface CommDelFlag {
    /**
     * 删除标识
     */
    @LogicalDeleted(value = "true")
    @Column(name = "del_flag")
    val delFlag: Boolean
}
