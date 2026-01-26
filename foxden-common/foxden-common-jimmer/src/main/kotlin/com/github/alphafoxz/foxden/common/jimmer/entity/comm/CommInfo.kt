package com.github.alphafoxz.foxden.common.jimmer.entity.comm

import jakarta.validation.constraints.Max
import java.time.ZonedDateTime
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.MappedSuperclass

/**
 * 通用数据字段
 * 
 * @author wong
 */
@MappedSuperclass
interface CommInfo {
    /**
     * 创建部门
     */
    @Column(name = "create_dept")
    @get:Max(value = 9223372036854775807, message = "创建部门不可大于9223372036854775807")
    val createDept: Long

    /**
     * 创建者
     */
    @Column(name = "create_by")
    @get:Max(value = 9223372036854775807, message = "创建者不可大于9223372036854775807")
    val createBy: Long

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    val createTime: ZonedDateTime

    /**
     * 更新者
     */
    @Column(name = "update_by")
    @get:Max(value = 9223372036854775807, message = "更新者不可大于9223372036854775807")
    val updateBy: Long

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    val updateTime: ZonedDateTime
}
