package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 部门
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_dept")
interface SysDept : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 父部门ID
     */
    @Column(name = "parent_id")
    @get:Max(value = 9223372036854775807, message = "父部门ID不可大于9223372036854775807")
    @get:Min(value = 0, message = "父部门ID不可小于0")
    val parentId: Long?

    /**
     * 祖级列表
     */
    @Column(name = "ancestors")
    @get:Length(max = 2147483647)
    val ancestors: String?

    /**
     * 部门名称
     */
    @Column(name = "dept_name")
    @get:Length(max = 2147483647)
    val deptName: String

    /**
     * 部门类别编码
     */
    @Column(name = "dept_category")
    @get:Length(max = 2147483647)
    val deptCategory: String?

    /**
     * 显示顺序
     */
    @Column(name = "order_num")
    @get:Max(value = 2147483647, message = "显示顺序不可大于2147483647")
    @get:Min(value = 0, message = "显示顺序不可小于0")
    val orderNum: Int?

    /**
     * 负责人
     */
    @Column(name = "leader")
    @get:Max(value = 9223372036854775807, message = "负责人不可大于9223372036854775807")
    @get:Min(value = 0, message = "负责人不可小于0")
    val leader: Long?

    /**
     * 联系电话
     */
    @Column(name = "phone")
    @get:Length(max = 2147483647)
    val phone: String?

    /**
     * 邮箱
     */
    @Column(name = "email")
    @get:Length(max = 2147483647)
    val email: String?

    /**
     * 部门状态（0正常 1停用）
     */
    @Column(name = "status")
    @get:Length(max = 1)
    val status: String
}
