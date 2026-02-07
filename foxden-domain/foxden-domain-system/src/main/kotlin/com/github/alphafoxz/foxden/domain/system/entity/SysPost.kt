package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 岗位表 sys_post
 */
@Entity
interface SysPost : CommId, CommInfo, CommTenant {
    /**
     * 部门id
     */
    val deptId: Long?

    /**
     * 岗位编码
     */
    val postCode: String

    /**
     * 岗位名称
     */
    val postName: String

    /**
     * 岗位类别编码
     */
    val postCategory: String?

    /**
     * 岗位排序
     */
    val postSort: Int?

    /**
     * 状态（0正常 1停用）
     */
    val status: String?

    /**
     * 部门
     */
    @ManyToOne
    @JoinColumn(name = "dept_id")
    val dept: SysDept?

    /**
     * 用户
     */
    @OnDissociate(DissociateAction.DELETE)
    @ManyToMany
    @JoinTable(name = "sys_user_post")
    val users: List<SysUser>
}
