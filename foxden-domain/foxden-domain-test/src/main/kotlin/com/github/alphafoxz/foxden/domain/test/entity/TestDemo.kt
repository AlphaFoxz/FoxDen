package com.github.alphafoxz.foxden.domain.test.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 测试表 test_demo
 * 注意：del_flag 为 integer 类型，不能继承 CommDelFlag
 */
@Entity
@Table(name = "test_demo")
interface TestDemo : CommId, CommInfo, CommTenant {
    /**
     * 部门ID
     */
    val deptId: Long?

    /**
     * 用户ID
     */
    val userId: Long?

    /**
     * 排序
     */
    val orderNum: Int?

    /**
     * key键
     */
    val testKey: String?

    /**
     * 值
     */
    val value: String?

    /**
     * 版本
     */
    val version: Int?

    /**
     * 删除标志（0存在 1删除）
     */
    val delFlag: Int?
}
