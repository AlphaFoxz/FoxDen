package com.github.alphafoxz.foxden.common.jimmer.entity.comm

import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.jackson.LongToStringConverter
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.MappedSuperclass

/**
 * 主键Trait
 * 所有实体表的ID字段定义
 */
@MappedSuperclass
interface CommId {
    /**
     * 主键ID
     */
    @Id
    @JsonConverter(LongToStringConverter::class)
    val id: Long
}
