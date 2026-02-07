package com.github.alphafoxz.foxden.common.jimmer.entity.comm

import org.babyfish.jimmer.sql.LogicalDeleted
import org.babyfish.jimmer.sql.MappedSuperclass

/**
 * 逻辑删除Trait
 * 逻辑删除字段定义
 */
@MappedSuperclass
interface CommDelFlag {
    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @LogicalDeleted("true")
    val delFlag: Boolean
}
