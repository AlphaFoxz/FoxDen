package com.github.alphafoxz.foxden.domain.gen.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 代码生成字段表 gen_table_column
 */
@Entity
@Table(name = "gen_table_column")
interface GenTableColumn : CommId, CommInfo {
    /**
     * 归属表ID
     */
    val tableId: Long?

    /**
     * 列名称
     */
    val columnName: String?

    /**
     * 列描述
     */
    val columnComment: String?

    /**
     * 列类型
     */
    val columnType: String?

    /**
     * JAVA类型
     */
    val javaType: String?

    /**
     * JAVA字段名
     */
    val javaField: String?

    /**
     * 是否主键（0是 1否）
     */
    @org.babyfish.jimmer.sql.Column(name = "is_pk")
    val pkFlag: String?

    /**
     * 是否自增（0是 1否）
     */
    @org.babyfish.jimmer.sql.Column(name = "is_increment")
    val incrementFlag: String?

    /**
     * 是否必填（0是 1否）
     */
    @org.babyfish.jimmer.sql.Column(name = "is_required")
    val requiredFlag: String?

    /**
     * 是否为插入字段（0是 1否）
     */
    @org.babyfish.jimmer.sql.Column(name = "is_insert")
    val insertFlag: String?

    /**
     * 是否编辑字段（0是 1否）
     */
    @org.babyfish.jimmer.sql.Column(name = "is_edit")
    val editFlag: String?

    /**
     * 是否列表字段（0是 1否）
     */
    @org.babyfish.jimmer.sql.Column(name = "is_list")
    val listFlag: String?

    /**
     * 是否查询字段（0是 1否）
     */
    @org.babyfish.jimmer.sql.Column(name = "is_query")
    val queryFlag: String?

    /**
     * 查询方式（等于、不等于、大于、小于、范围）
     */
    val queryType: String?

    /**
     * 显示类型（文本框、文本域、下拉框、复选框）
     */
    val htmlType: String?

    /**
     * 字典类型
     */
    val dictType: String?

    /**
     * 排序
     */
    val sort: Int?
}
