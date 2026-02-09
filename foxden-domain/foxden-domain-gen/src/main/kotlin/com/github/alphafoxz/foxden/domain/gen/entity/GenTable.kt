package com.github.alphafoxz.foxden.domain.gen.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 代码生成业务表表 gen_table
 */
@Entity
@Table(name = "gen_table")
interface GenTable : CommId, CommInfo {
    /**
     * 数据源名称
     */
    @org.babyfish.jimmer.sql.Column(name = "data_name")
    val dataName: String?

    /**
     * 表名称
     */
    val tableName: String?

    /**
     * 表描述
     */
    val tableComment: String?

    /**
     * 关联子表的表名
     */
    val subTableName: String?

    /**
     * 子表关联的外键名
     */
    val subTableFkName: String?

    /**
     * 实体类名称
     */
    val className: String?

    /**
     * 使用的模板（crud单表 tree树表）
     */
    val tplCategory: String?

    /**
     * 生成包路径
     */
    val packageName: String?

    /**
     * 生成模块名
     */
    val moduleName: String?

    /**
     * 生成业务名
     */
    val businessName: String?

    /**
     * 生成功能名
     */
    val functionName: String?

    /**
     * 生成功能作者
     */
    val functionAuthor: String?

    /**
     * 生成代码方式（0zip压缩包 1自定义路径）
     */
    val genType: String

    /**
     * 生成路径
     */
    val genPath: String?

    /**
     * 其他生成选项
     */
    val options: String?

    /**
     * 备注
     */
    val remark: String?
}
