package com.github.alphafoxz.foxden.common.jimmer.entity.comm

import org.babyfish.jimmer.sql.MappedSuperclass
import java.time.LocalDateTime

/**
 * 审计字段Trait
 * 包含创建和更新相关字段
 */
@MappedSuperclass
interface CommInfo {
    /**
     * 创建部门ID
     */
    val createDept: Long?

    /**
     * 创建者ID
     */
    val createBy: Long?

    /**
     * 创建时间
     */
    val createTime: LocalDateTime?

    /**
     * 更新者ID
     */
    val updateBy: Long?

    /**
     * 更新时间
     */
    val updateTime: LocalDateTime?
}
