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
 * 岗位信息
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_post")
interface SysPost : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 部门id
     */
    @Column(name = "dept_id")
    @get:Max(value = 9223372036854775807, message = "部门id不可大于9223372036854775807")
    @get:Min(value = 0, message = "部门id不可小于0")
    val deptId: Long

    /**
     * 岗位编码
     */
    @Column(name = "post_code")
    @get:Length(max = 2147483647)
    val postCode: String

    /**
     * 岗位类别编码
     */
    @Column(name = "post_category")
    @get:Length(max = 2147483647)
    val postCategory: String?

    /**
     * 岗位名称
     */
    @Column(name = "post_name")
    @get:Length(max = 2147483647)
    val postName: String

    /**
     * 显示顺序
     */
    @Column(name = "post_sort")
    @get:Max(value = 2147483647, message = "显示顺序不可大于2147483647")
    @get:Min(value = 0, message = "显示顺序不可小于0")
    val postSort: Int?

    /**
     * 状态（0正常 1停用）
     */
    @Column(name = "status")
    @get:Length(max = 1)
    val status: String

    /**
     * 备注
     */
    @Column(name = "remark")
    @get:Length(max = 2147483647)
    val remark: String?
}
