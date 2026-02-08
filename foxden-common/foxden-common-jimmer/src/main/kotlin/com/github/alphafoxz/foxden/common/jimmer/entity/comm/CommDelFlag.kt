package com.github.alphafoxz.foxden.common.jimmer.entity.comm

import org.babyfish.jimmer.sql.MappedSuperclass

/**
 * 逻辑删除Trait
 * 逻辑删除字段定义
 *
 * 注意：数据库中 del_flag 列为 TEXT 类型，存储 "0" 或 "1"
 * 不使用 @LogicalDeleted 注解，需手动在查询中过滤
 */
@MappedSuperclass
interface CommDelFlag {
    /**
     * 删除标志（"0"代表存在 "1"代表删除）
     * 数据库使用 TEXT 类型存储字符串
     */
    val delFlag: String
}
