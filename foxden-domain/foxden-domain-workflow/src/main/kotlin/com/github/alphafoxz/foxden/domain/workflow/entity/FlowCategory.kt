package com.github.alphafoxz.foxden.domain.workflow.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 流程分类表 flow_category
 */
@Entity
@Table(name = "flow_category")
interface FlowCategory : CommDelFlag, CommInfo, CommTenant {
    /**
     * 主键ID（对应数据库的 category_id）
     */
    @org.babyfish.jimmer.sql.Column(name = "category_id")
    @Id
    @GeneratedValue
    val id: Long

    /**
     * 父分类ID
     */
    val parentId: Long?

    /**
     * 祖级列表
     */
    val ancestors: String?

    /**
     * 分类名称
     */
    val categoryName: String

    /**
     * 显示顺序
     */
    val orderNum: Int?
}
