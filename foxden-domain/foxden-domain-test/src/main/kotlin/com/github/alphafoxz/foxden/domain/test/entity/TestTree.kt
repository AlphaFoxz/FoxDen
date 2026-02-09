package com.github.alphafoxz.foxden.domain.test.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 测试树表 test_tree
 * 注意：del_flag 为 integer 类型，不能继承 CommDelFlag
 */
@Entity
@Table(name = "test_tree")
interface TestTree : CommId, CommInfo, CommTenant {
    /**
     * 父ID
     */
    val parentId: Long?

    /**
     * 部门ID
     */
    val deptId: Long?

    /**
     * 用户ID
     */
    val userId: Long?

    /**
     * 树节点名
     */
    val treeName: String?

    /**
     * 版本
     */
    val version: Int?

    /**
     * 删除标志（0存在 1删除）
     */
    val delFlag: Int?
}
