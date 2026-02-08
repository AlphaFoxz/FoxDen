package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 岗位表 sys_post
 */
@Entity
@Table(name = "sys_post")
interface SysPost : CommInfo, CommTenant {
    /**
     * 主键ID
     */
    @org.babyfish.jimmer.sql.Column(name = "post_id")
    @Id
    @GeneratedValue
    val id: Long

    /**
     * 部门id
     */
    @IdView
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
    @ManyToMany(mappedBy = "posts")
    val users: List<SysUser>
}
